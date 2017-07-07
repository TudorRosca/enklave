__author__ = 'radu'

from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
import json
from datetime import timedelta
from django.utils import timezone
from backend.utils import dynamodb_lib
import time


class DynamoTests(TestCase):
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

    def test_dynamo(self):
        coordinate_data = {
            'lat': 1,
            'lon': 1
        }
        dynamodb_lib.add_coordinates(self.test_user, coordinate_data, timezone.now())

