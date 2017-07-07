__author__ = 'radu'

from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Enklave, UserLocation
from backend.models import Raider2, RaiderPosition, RaiderCombat, RaiderCombatRaider, RaiderCombatUser
from backend.models import Faction
from backend.models import Brick, Turret, Shield
from datetime import timedelta
from django.utils import timezone
import time
from backend.utils import combat_lib, processing
from backend.utils.redis_lib import RedisLib
redis_lib = RedisLib()
import json


class FactionTests(TestCase):
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

    def test_join_faction_validation_error(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        data = {

        }

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.post('/faction/join/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "u'faction_id' is a required property")

    def test_join_faction_bad_input(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        data = {
            'faction_id': 'err'
        }

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.post('/faction/join/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "u'err' is not of type u'integer'")

    def test_join_faction_not_found(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        data = {
            'faction_id': 1
        }

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.post('/faction/join/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['detail'], 'Faction not found: 1')

    def test_join_faction_good(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        data = {
            'faction_id': faction.id
        }

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.post('/faction/join/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.test_user.userprofile.refresh_from_db()
        self.assertEqual(self.test_user.userprofile.faction, faction)

    def test_leave_faction(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        self.test_user.userprofile.faction = faction
        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {

        }
        response = client.post('/faction/leave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.test_user.userprofile.refresh_from_db()
        self.assertEqual(self.test_user.userprofile.faction, None)

    def test_get_factions(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        faction1 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        faction2 = Faction.objects.create(
            name='test2',
            description='test2 desc',
            color='white',
            logo='abc2.jpg',
            display_order=2
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/faction/all/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        self.assertEqual(response.data[0]['id'], faction1.id)
        self.assertEqual(response.data[0]['name'], faction1.name)
        self.assertEqual(response.data[0]['description'], faction1.description)
        self.assertEqual(response.data[0]['color'], faction1.color)
        self.assertEqual(response.data[0]['logo'], faction1.logo)
        self.assertEqual(response.data[1]['id'], faction2.id)
        self.assertEqual(response.data[1]['name'], faction2.name)
        self.assertEqual(response.data[1]['description'], faction2.description)
        self.assertEqual(response.data[1]['color'], faction2.color)
        self.assertEqual(response.data[1]['logo'], faction2.logo)
