__author__ = 'radu'

from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Enklave, UserLocation, Country, Faction, UserDevice, ChatRoom, UserNotification
from backend.models import Message, EnklaveCombatUser, LocationMessage, AppConfig, Raider2, FactionMessage
from backend.models import EnklaveCombat
import json
from datetime import timedelta
from django.utils import timezone
from rest_framework.exceptions import NotFound


class ModelTests(TestCase):
    def setUp(self):
        self.test_user = User.objects.create_user("test_user", "test@user.com", "123456")

        self.application = Application(
            name="Test Application",
            redirect_uris="http://localhost",
            user=self.test_user,
            client_id='1234567',
            client_type=Application.CLIENT_CONFIDENTIAL,
            authorization_grant_type=Application.GRANT_AUTHORIZATION_CODE,
        )
        self.application.save()

    def test_country_model(self):
        country = Country.objects.create(
            name='ok'
        )

        self.assertTrue(country.name, unicode(country))

    def test_faction_model(self):
        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        self.assertEqual(faction.to_json(), {
            'id': faction.id,
            'name': faction.name,
            'description': faction.description,
            'color': faction.color,
            'logo': faction.logo
        })

        self.assertTrue(faction.name, str(faction))

    def test_userprofile_model(self):
        self.assertEqual(str(self.test_user.userprofile), "%s's profile" % self.test_user)

    def test_profile_generation(self):
        self.test_user = User.objects.create_user("test_user2", "test2@user.com", "123456")
        self.test_user.save()

    def test_userdevice_model(self):
        userdevice = UserDevice.objects.create(
            user=self.test_user,
            device_id='abc',
            app_version='1.1.1'
        )

        self.assertEqual(userdevice.to_json(),{
            'id': userdevice.id,
            'user': {
                'id': userdevice.user.id,
                'username': userdevice.user.username
            },
            'device_id': userdevice.device_id,
            'app_version': userdevice.app_version,
            'arn': userdevice.arn
        })

    def test_chat_room(self):
        chat_room = ChatRoom.objects.create(
            name='test'
        )

        self.assertEqual(str(chat_room), chat_room.name)

    def test_user_notification(self):
        user_notification = UserNotification.objects.create(
            user=self.test_user,
            sender=self.test_user,
            title='aa',
            message='aa',
            type='1'
        )

        user_notification.save()

    def test_message(self):
        message = Message.objects.create(
            from_user=self.test_user,
            to_user=self.test_user,
            txt='test'
        )

        message.save()
        message.date_viewed = timezone.now()
        message.save()

    def test_user_location(self):
        user_location = UserLocation.objects.filter(user=self.test_user).first()
        if not user_location:
            user_location = UserLocation.objects.create(
                user=self.test_user,
                latitude=22,
                longitude=22
            )

        enklave = Enklave.objects.create(
            latitude=22,
            longitude=22,
            name='test',
            description='desc'
        )

        combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=enklave
        )

        combat = EnklaveCombatUser.objects.create(
            user=self.test_user,
            enklave_combat=combat
        )

        self.assertEqual(str(combat.id), str(combat))
        user_location.save()

    def test_location_message_model(self):
        message = LocationMessage.objects.create(
            user=self.test_user,
            latitude=22,
            longitude=22,
            txt='test'
        )

        message.save()

    def test_app_config(self):
        app_config = AppConfig.objects.create(
            name='test',
            value='test'
        )

        self.assertEqual(app_config.name, str(app_config))

    def test_enklave(self):
        enklave = Enklave.objects.create(
            latitude=22,
            longitude=22,
            name='test',
            description='desc'
        )

        self.assertEqual(enklave.details()['id'], enklave.id)
        self.assertEqual(enklave.details()['latitude'], enklave.latitude)
        self.assertEqual(enklave.details()['status'], 'NotInCombat')

    def test_raider(self):
        enklave = Enklave.objects.create(
            latitude=22,
            longitude=22,
            name='test2',
            description='desc'
        )

        raider = Raider2.objects.create(
            latitude=22,
            longitude=22,
            enklave=enklave
        )

        self.assertEqual(raider.to_json()['id'], raider.id)

    def test_faction_message(self):
        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        message = FactionMessage.objects.create(
            user=self.test_user,
            faction=faction,
            txt='test'
        )

        message.save()

    def test_enklave_combat(self):
        enklave = Enklave.objects.create(
            latitude=22,
            longitude=22,
            name='test',
            description='desc'
        )

        combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=enklave
        )

        self.assertEqual(str(combat.id), str(combat))