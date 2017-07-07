__author__ = 'radu'
from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Message, UserLocation, LocationMessage, Faction, FactionMessage
import json
from datetime import timedelta
from django.utils import timezone
import time


class UserTests(TestCase):
    def setUp(self):
        self.test_user = User.objects.create_user("test_user", "test@user.com", "123456")
        self.test_user2 = User.objects.create_user("test_user2", "test2@user.com", "123456")

        self.application = Application(
            name="Test Application",
            redirect_uris="http://localhost",
            user=self.test_user,
            client_id='1234567',
            client_type=Application.CLIENT_CONFIDENTIAL,
            authorization_grant_type=Application.GRANT_AUTHORIZATION_CODE,
        )
        self.application.save()

    def test_send_message_user_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {
            "to_user": "test_user3",
            "txt": "hello"
        }

        response = client.post('/message/single/send/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        message = Message.objects.first()
        self.assertEqual(message, None)

    def test_get_message_history(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        message = Message.objects.create(
            from_user=self.test_user,
            to_user=self.test_user2,
            txt='test'
        )

        response = client.get('/message/single/history/?to_user={0}'.format(self.test_user2.username))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['id'], message.id)
        self.assertEqual(response.data[0]['from_user'], message.from_user.username)
        self.assertEqual(response.data[0]['to_user'], message.to_user.username)
        self.assertEqual(response.data[0]['txt'], message.txt)

    def test_get_message_history_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        response = client.get('/message/single/history/?to_user=1000')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

    def test_send_message(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {
            "to_user": "test_user2",
            "txt": "hello"
        }

        response = client.post('/message/single/send/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        message = Message.objects.first()
        self.assertEqual(message.from_user, self.test_user)
        self.assertEqual(message.to_user, self.test_user2)
        self.assertEqual(message.txt, "hello")

        response = client.get('/message/single/history/?to_user={0}'.format(self.test_user2.username))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)

    def test_send_message_location(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {
            "txt": "hello"
        }

        response = client.post('/message/location/send/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        response = client.post('/message/location/send/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        message = LocationMessage.objects.first()
        self.assertEqual(message.user, self.test_user)
        self.assertEqual(message.txt, "hello")
        self.assertEqual(message.latitude, 44)
        self.assertEqual(message.longitude, 33)

    def test_send_message_faction(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {
            "txt": "hello"
        }

        response = client.post('/message/faction/send/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        faction = Faction.objects.create(
            name='test',
            description='test',
            color='test',
            display_order=1,
            logo='abc.jpg'
        )

        self.test_user.userprofile.faction = faction
        self.test_user.userprofile.save()

        response = client.post('/message/faction/send/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        message = FactionMessage.objects.first()
        self.assertEqual(message.user, self.test_user)
        self.assertEqual(message.txt, "hello")
        self.assertEqual(message.faction, faction)

