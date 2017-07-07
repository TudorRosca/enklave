from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Enklave, UserLocation, Shield
import json
from datetime import timedelta
from django.utils import timezone
from rest_framework.exceptions import NotFound


class EnklaveTests(TestCase):
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

    def test_create_enklave(self):
        data = {
            "latitude": 45.23,
            "longitude": -23.54,
            "name": "home",
            "description": "my home2"
        }

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.post('/enklave/create/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)

        enklave = Enklave.objects.first()
        self.assertEqual(enklave.name, "home")
        self.assertEqual(enklave.description, "my home2")
        self.assertEqual(enklave.latitude, data['latitude'])
        self.assertEqual(enklave.longitude, data['longitude'])

        response = client.get('/enklave/get/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data[0]['id'], enklave.id)

        response = client.get('/enklave/details/?enklave_id={0}'.format(enklave.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['id'], enklave.id)
        self.assertEqual(response.data['status'], 'NotInCombat')
        self.assertEqual(response.data['total_shield'], 0)

        Shield.objects.create(
            enklave=enklave,
            user=self.test_user,
            energy=1001
        )

        response = client.get('/enklave/details/?enklave_id={0}'.format(enklave.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['id'], enklave.id)
        self.assertEqual(response.data['status'], 'NotInCombat')
        self.assertEqual(response.data['total_shield'], 1001)


    def test_search_enklave_no_location(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/enklave/nearby/')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_search_enklave(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=45.23,
            longitude=-23.54,
        )

        enklave = Enklave.objects.create(
            latitude=45.23,
            longitude=-23.54
        )

        response = client.get('/enklave/nearby/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        for item in response.data:
            self.assertEqual(item['latitude'], 45.23)
            self.assertEqual(item['id'], enklave.id)

    def test_enklave_manager(self):
        enklave = Enklave.objects.create(
            latitude=22,
            longitude=22,
            name='test',
            description='desc'
        )
        enklave_ee = Enklave.items.get_or_404(enklave.id)
        self.assertEqual(enklave_ee.id, enklave.id)

        self.assertRaises(NotFound, Enklave.items.get_or_404, 123)





