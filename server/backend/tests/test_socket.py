__author__ = 'radu'

from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Enklave, EnklaveCombat, UserLocation, EnklaveCombatUser, UserProfile, UserCrafting
import json
from datetime import timedelta
from django.utils import timezone
from tornado_main import MyAppWebSocket
import time


class SocketTests(TestCase):
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

    def test_socket(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {

        }

        response = client.post('/socket/ticket/get/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_movement(self):
        location = UserLocation.objects.create(
            user=self.test_user,
            latitude=0,
            longitude=1
        )

        pre_dist = self.test_user.userprofile.distance_walked

        location.latitude = 0
        location.longitude = 1
        location.save()

        location.latitude = 0
        location.longitude = 1.1
        location.save()

        self.test_user.userprofile.refresh_from_db()
        self.assertTrue(pre_dist + 11 < self.test_user.userprofile.distance_walked)






