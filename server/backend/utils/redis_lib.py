__author__ = 'radu'
import redis
from django.conf import settings

import json
import logging
logger = logging.getLogger('api_exceptions')
from django.utils import timezone
from django.utils.dateformat import format


class RedisClient(object):

    @staticmethod
    def get_connection():
        redis_host = settings.REDIS_HOST

        pool = redis.ConnectionPool(host=redis_host, port=6379, db=0)
        redis_server = redis.Redis(connection_pool=pool)
        return redis_server


class RedisLib(RedisClient):

    def __init__(self):
        # if settings.TESTING:
        #     self.connection = None

        self.connection = self.get_connection()

    def get_key(self, key):
        if not self.connection:
            return None

        return self.connection.get(key)

    def publish(self, channel, message):
        if not self.connection:
            return None

        return self.connection.publish(channel, message)

    def set_ticket(self, ticket, data):
        if not self.connection:
            return None

        key = "tkt_{0}".format(ticket)

        return self.connection.set(key, json.dumps(data), ex=100000)

    def process_user_coordinates(self, user, latitude, longitude, gen_time):
        if not self.connection:
            return None

        data = "{0}_{1},{2}".format(format(gen_time, 'U'), latitude, longitude)
        key = "coord_{0}".format(user.id) if not settings.TESTING else "coord_t_{0}".format(user.id)

        return self.connection.zadd(key, data, format(gen_time, 'U'))

    def get_coordinates(self, user):
        if not self.connection:
            return None

        date_today = timezone.now()
        date_today = date_today.replace(hour=0, minute=0, second=0, microsecond=0)

        key = "coord_{0}".format(user.id) if not settings.TESTING else "coord_t_{0}".format(user.id)
        return self.connection.zrangebyscore(key, "-inf", str(format(date_today, 'U')))

    def clear_coordinates(self, user, coordinates):
        if not self.connection:
            return None

        key = "coord_{0}".format(user.id)
        self.connection.zrem(key, *coordinates)

    def get_last_generated_scrap_data(self, user):
        if not self.connection:
            return None

        key = "scrap_gen_{0}".format(user.id) if not settings.TESTING else "scrap_gen_t_{0}".format(user.id)
        return self.connection.get(key)

    def remove_user_scrap_data(self, user):
        if not self.connection:
            return None

        key = "scrap_gen_{0}".format(user.id) if not settings.TESTING else "scrap_gen_t_{0}".format(user.id)
        self.connection.delete(key)
        key = "scrap_archive_{0}".format(user.id) if not settings.TESTING else "scrap_archive_t_{0}".format(user.id)
        self.connection.delete(key)

    def set_last_generated_scrap_data(self, user, lat, lon, gen_time):
        if not self.connection:
            return None

        data = "{0}_{1},{2}".format(format(gen_time, 'U'), lat, lon)
        key = "scrap_gen_{0}".format(user.id) if not settings.TESTING else "scrap_gen_t_{0}".format(user.id)

        self.connection.set(key, data)

        key = "scrap_archive_{0}".format(user.id) if not settings.TESTING else "scrap_archive_t_{0}".format(user.id)
        data = "{0}_{1},{2}".format(format(gen_time, 'U'), lat, lon)
        self.connection.sadd(key, data)

    def get_scrap_archive(self, user):
        if not self.connection:
            return None

        key = "scrap_archive_{0}".format(user.id) if not settings.TESTING else "scrap_archive_t_{0}".format(user.id)
        return list(self.connection.smembers(key))

    def publish_attack(self, attacking_user, enklave):
        if not self.connection:
            return None

        notification_data = {
            "msg_type": "attack",
            "user": {
                "id": attacking_user.id,
                "username": attacking_user.username
            },
            "enklave": {
                "id": enklave.id,
                "name": enklave.name,
                "latitude": enklave.latitude,
                "longitude": enklave.longitude
            },
            "user_id": enklave.user.id if enklave.user else None
        }

        self.publish("enklave_line", json.dumps(notification_data))

    def publish_join(self, attacking_user, enklave_combat, enklave_combat_user, faction, attack_config,
                     energy_config_data, user_ids):
        if not self.connection:
            return None

        print user_ids

        message_data = {
            "user_ids": list(set(user_ids)),
            "payload": {
                "msg_type": "joined_combat",
                "message": 'A user has joined the combat',
                "attacker": {
                    "combatant_id": enklave_combat_user.id,
                    "time_recharging": 5,
                    "faction_id": faction.id if faction else None,
                    "attack_config": attack_config,
                    "energy_config_data": energy_config_data,
                    "id": attacking_user.id,
                    "username": attacking_user.first_name,
                    "energy": attacking_user.userprofile.energy
                },

                "enklave_combat": {
                    "id": enklave_combat.id
                }
            }
        }

        self.publish("enklave_line", json.dumps(message_data))

    def publish_leave(self, enklave_combatant, attacking_user, faction, user_ids, attack_config, enklave_combat_status):
        if not self.connection:
            return None

        message_data = {
            "user_ids": list(set(user_ids)),
            "payload": {
                "msg_type": "left_combat",
                "message": 'A user has left the combat area',
                "attack_config": attack_config,
                "user": {
                    "id": attacking_user.id,
                    "username": attacking_user.username,
                    "energy": attacking_user.userprofile.energy,

                    "combatant_id": enklave_combatant.id,
                    "time_recharging": 5,
                    "faction_id": faction.id if faction else None,
                    "attack_config": attack_config,
                    },
                'enklave_combat_status': enklave_combat_status,
                "enklave_combat": {
                    "id": enklave_combatant.enklave_combat.id
                }
            }
        }

        self.publish("enklave_line", json.dumps(message_data))

    def publish_hit(self, enklave_combatant, attacking_user, enklave_combatant_user,
                    faction, user_ids, attack_config, enklave_combat_status):
        if not self.connection:
            return None

        message_data = {
            "user_ids": list(set(user_ids)),
            "payload": {
                "msg_type": "hit",
                "message": 'A user has been hit',
                "attack_config": attack_config,
                "attacker": {
                    "energy": attacking_user.userprofile.energy,
                    "combatant_id": enklave_combatant_user.id,
                    "time_recharging": 5,
                    "faction_id": faction.id if faction else None,
                    "attack_config": attack_config,
                },
                'enklave_combat_status': enklave_combat_status,
                "attacked_user": {
                    "energy": enklave_combatant.user.userprofile.energy,
                    "combatant_id": enklave_combatant.id
                },
                "enklave_combat": {
                    "id": enklave_combatant.enklave_combat.id
                }
            }
        }

        self.publish("enklave_line", json.dumps(message_data))

    def publish_enklave_hit(self, enklave_combat, attacking_user, enklave_combatant, faction,
                            user_ids, attack_config, enklave_combat_status, attack_status, enklave_conquered):
        if not self.connection:
            return None

        message_data = {
            "user_ids": list(set(user_ids)),
            "payload": {
                "msg_type": "hit",
                "message": 'A user has been hit',
                "attack_config": attack_config,
                "attacker": {
                    "id": attacking_user.id,
                    "username": attacking_user.username,
                    "energy": attacking_user.userprofile.energy,

                    "combatant_id": enklave_combatant.id,
                    "time_recharging": 5,
                    "faction_id": faction.id,
                    "attack_config": attack_config,
                },
                'attack_status': attack_status,
                'enklave_combat_status': enklave_combat_status,
                "attacked_enklave": {
                    "id": enklave_combat.enklave.id,
                    "remaining_energy": enklave_combat.enklave.shield,
                    "enklave_conquered": enklave_conquered
                },
                "enklave_combat": {
                    "id": enklave_combat.id
                }
            }
        }

        self.publish("enklave_line", json.dumps(message_data))

    def store_attack_sequence(self, combat, sequence):
        if not self.connection:
            return None

        key = "combat_sequence_{0}".format(combat.id) if not settings.TESTING \
            else "combat_sequence_t_{0}".format(combat.id)
        return self.connection.set(key, json.dumps(sequence), ex=100000)

    def get_attack_sequence(self, combat):
        if not self.connection:
            return None

        key = "combat_sequence_{0}".format(combat.id) if not settings.TESTING \
            else "combat_sequence_t_{0}".format(combat.id)

        try:
            data = json.loads(self.connection.get(key))
        except Exception:
            return None
        return data

    def remove_combat_sequence(self, combat):
        key = "combat_sequence_{0}".format(combat.id) if not settings.TESTING \
            else "combat_sequence_t_{0}".format(combat.id)
        return self.connection.delete(key)
