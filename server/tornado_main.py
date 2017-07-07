#!/usr/bin/env python

import tornado.httpserver
import tornado.ioloop
import tornado.web
import tornado.websocket
import tornado.wsgi
from enklave.wsgi import application as myapp_wsgi
from django.shortcuts import render, get_object_or_404

from backend.utils.redis_lib import RedisLib
redis_lib = RedisLib()
from backend.models import UserConnection, Message, UserLocation, LocationMessage, EnklaveCombatUser
from django.contrib.auth.models import User
from backend.custom_views import combat
import threading
import json
from django.utils import timezone
from django.utils.dateformat import format
from backend.utils import dynamodb_lib
from backend.utils import processing
import datetime
import logging
from backend.utils import validators
logger = logging.getLogger('api_exceptions')


class MyAppWebSocket(tornado.websocket.WebSocketHandler):
    # Simple Websocket echo handler. This could be extended to
    # use Redis PubSub to broadcast updates to clients.

    clients = set()
    LISTENERS = []
    target_listener = {}
    first_receive = {}
    previous_coord = {}
    total_distance = {}

    def __init__(self, _application, _request):
        tornado.websocket.WebSocketHandler.__init__(self, _application, _request)
        self.user = None
        self.last_gen = None

    def open(self, token):
        # logging.info('Client connected')
        print 'client connected ', token
        logger.info('\n%s client connected %s' % (timezone.now(), token))

        user_data = redis_lib.get_key("tkt_{0}".format(token))
        try:
            user_data = json.loads(user_data)
            user = User.objects.get(pk=user_data['user_id'])
            print user

        except User.DoesNotExist:
            user = None
            logger.info('\n%s bad ticket' % timezone.now())
            print 'log here bad ticket'

        if user:
            self.user = user
            self.last_gen = processing.process_last_gen_scrap(redis_lib.get_last_generated_scrap_data(user))

            # TODO send to dynamo daily
            #
            # coordinates = redis_lib.get_coordinates(self.user)
            # if len(coordinates) > 0:
            #     dynamodb_lib.add_coordinates(self.user, coordinates, format(timezone.now(), 'U'))
            #     redis_lib.clear_coordinates(user, coordinates)

            MyAppWebSocket.LISTENERS.append(self)
            MyAppWebSocket.clients.add(self)
            if user.id in MyAppWebSocket.target_listener:
                print 'socket closed'
                logger.info('\n%s socket closed' % timezone.now())
                old_conn = MyAppWebSocket.target_listener[user.id]
                old_conn.close(reason="You connected on a different device")

            MyAppWebSocket.target_listener[user.id] = self

    def on_message(self, message):
        message = json.loads(message)

        if message['type'] == 'message_received':
            validation_error = validators.validate_message_received(message)
            if validation_error:
                self.write_message({"msg_type": "error", "message": validation_error})
                return

            for message_id in message['params']['message_ids']:
                message_obj = Message.objects.filter(to_user=self.user, id=message_id).first()
                if message_obj:
                    message_obj.date_viewed = timezone.now()
                    message_obj.save()

            print 'message_received', message['params']['message_ids']

        elif message['type'] == 'scrap':
            validation_error = validators.validate_coord_scrap(message)
            if validation_error:
                self.write_message({"msg_type": "error", "message": validation_error})
                return

            latitude = message['params']['lat']
            longitude = message['params']['long']

            if not self.last_gen:
                try:
                    redis_lib.set_last_generated_scrap_data(self.user, latitude, longitude, timezone.now())
                    self.last_gen = processing.process_last_gen_scrap(
                        redis_lib.get_last_generated_scrap_data(self.user)
                    )
                except Exception, e:
                    logger.exception('\n%s last gen error %s' % timezone.now(), e.message)

            raw_archive = redis_lib.get_scrap_archive(self.user)
            archive = processing.process_scrap_archive(raw_archive)

            has_received_in_last_hour = processing.test_if_already_awarded_in_the_last_half_hour(
                archive,
                latitude,
                longitude,
                timezone.now())

            print has_received_in_last_hour

            if has_received_in_last_hour:
                logger.info("{0} - {1} already received in lat {2} lon {3}".format(
                    timezone.now(), self.user.username, latitude, longitude))
            else:
                logger.info("{0} - {1} has not received in lat {2} lon {3}".format(
                    timezone.now(), self.user.username, latitude, longitude))

            if not processing.test_if_already_awarded_in_the_last_half_hour(
                    archive,
                    latitude,
                    longitude,
                    timezone.now()) and \
                    processing.test_scrap_condition(
                    self.last_gen,
                    latitude,
                    longitude,
                    timezone.now()):

                scrap_data = processing.award_scrap(self.user)
                self.write_message(get_scrap_message(scrap_data))
                redis_lib.set_last_generated_scrap_data(
                    self.user,
                    latitude,
                    longitude,
                    timezone.now()
                )
                logger.info("{0} - {1} has received scrap now for lat {2} lon {3}".format(
                    timezone.now(), self.user.username, latitude, longitude))

                self.last_gen = processing.process_last_gen_scrap(redis_lib.get_last_generated_scrap_data(self.user))

            redis_lib.process_user_coordinates(
                self.user,
                latitude,
                longitude,
                timezone.now())
            user_location = UserLocation.objects.filter(user=self.user).first()

            if not user_location:
                UserLocation.objects.create(
                    user=self.user,
                    latitude=latitude,
                    longitude=longitude)
            else:
                distance = processing.calc_dist(latitude, longitude,
                                                user_location.latitude, user_location.longitude)

                if distance > .01:
                    user_location.latitude = latitude
                    user_location.longitude = longitude
                    user_location.save()

            logger.info('\n%s message %s' % (timezone.now(), message))

    def on_receive(self, message):
        self.write_message(message)

    def check_origin(self, origin):
        return True

    def on_close(self):
        if self in MyAppWebSocket.clients:
            MyAppWebSocket.clients.remove(self)

        if self in MyAppWebSocket.LISTENERS:
            MyAppWebSocket.LISTENERS.remove(self)

    @classmethod
    def broadcast(cls, message, self):
        print len(cls.clients)

        for client in cls.clients:
            if client == self:
                continue

            client.write_message(message)


app_web_socket = MyAppWebSocket

application = tornado.web.Application([
    (r'/ws/(.*)', app_web_socket),
    (r'/(.*)', tornado.web.FallbackHandler, dict(
        fallback=tornado.wsgi.WSGIContainer(myapp_wsgi)
    )),
    ], debug=True)


from functools import partial


def get_scrap_message(scrap_data):
    # message = "You got scrap man: {0}".format(scrap_nr)
    message = scrap_data
    data = {
        "msg_type": "scrap",
        "message": message
    }
    return json.dumps(data)


def process_coord_message(message):
    lat = float(message.split(',')[0])
    lon = float(message.split(',')[1])
    return {
        "lat": lat,
        "lon": lon
    }


def validate_coordinates_socket(coordinate_string):
    coordinates = coordinate_string.split(",")
    if len(coordinates) != 2:
        return False

    lat, lon = coordinate_string.split(",")
    try:
        lat = float(lat)
    except TypeError:
        return False

    try:
        lon = float(lon)
    except TypeError:
        return False

    if lat < -90 or lat > 90:
        return False

    if lon < -180 or lon > 180:
        return False

    return True


def redis_listener():
    r = redis_lib.get_connection()
    ps = r.pubsub()
    ps.subscribe('enklave_line')
    io_loop = tornado.ioloop.IOLoop.instance()
    for message in ps.listen():
        if not message:
            continue
        try:
            message_json = json.loads(message['data'])
            if message_json["payload"]["msg_type"] == "message":
                user_id = message_json['user_id']
                client = MyAppWebSocket.target_listener[user_id]
                io_loop.add_callback(partial(client.on_receive, message_json["payload"]))
            elif message_json["payload"]["msg_type"] == "message_viewed":
                user_id = message_json['user_id']
                print user_id, 'message viewed'
                client = MyAppWebSocket.target_listener[user_id]
                io_loop.add_callback(partial(client.on_receive, message_json["payload"]))
            elif message_json["payload"]["msg_type"] in ["faction_message", "location_message", "joined_combat",
                                                         "left_combat", "hit", "attack"]:
                user_ids = message_json['user_ids']
                for user_id in user_ids:
                    if user_id in MyAppWebSocket.target_listener:
                        client = MyAppWebSocket.target_listener[user_id]
                        io_loop.add_callback(partial(client.on_receive, message_json["payload"]))
        except Exception, e:
            print e
            continue

        # if user_id in MyAppWebSocket.target_listener:
        #     io_loop.add_callback(partial(MyAppWebSocket.target_listener[user_id].on_receive, message_json))

if __name__ == '__main__':
    threading.Thread(target=redis_listener).start()
    application.listen(8888)
    tornado.ioloop.IOLoop.instance().start()







