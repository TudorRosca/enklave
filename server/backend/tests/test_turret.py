__author__ = 'radu'

from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Turret
import json
from datetime import timedelta
from django.utils import timezone
import time


class TurretTests(TestCase):
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

    def test_turret(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        turret = Turret.objects.create(
            user=self.test_user
        )

        self.assertEqual(turret.user, self.test_user)
        # data = {
        #     'device_id': 'aaa',
        #     'app_version': '1.2.3'
        # }
        #
        # response = client.post('/user/device/update/', data=json.dumps(data), content_type='application/json')
        # self.assertEqual(response.status_code, status.HTTP_200_OK)
        #
        # response = client.get('/user/device/mine/')
        # self.assertEqual(response.status_code, status.HTTP_200_OK)
        # self.assertEqual(response.data[0]['app_version'], '1.2.3')
        #
        # response = client.delete('/user/device/delete/?device_id={0}'.format(response.data[0]['id']))
        # self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)
        #
        # response = client.delete('/user/device/delete/?device_id={0}'.format(100000))
        # self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

