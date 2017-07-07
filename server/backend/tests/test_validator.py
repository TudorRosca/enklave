_author__ = 'radu'
from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Enklave, EnklaveCombat, UserLocation, EnklaveCombatUser
import json
from datetime import timedelta
from django.utils import timezone
from backend.utils import validators


class ValidatorTests(TestCase):
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

    def test_validators(self):
        self.scrap_good()
        self.scrap_bad()

    def scrap_good(self):
        data = {
            "params": {
                "lat": 22,
                "long": 22
            }
        }

        err = validators.validate_coord_scrap(data)
        self.assertEqual(err, None)

    def scrap_bad(self):
        data = {
            "param": {
                "lat": 22,
                "long": 22
            }
        }

        err = validators.validate_coord_scrap(data)
        self.assertEqual(err, "u'params' is a required property")

    def test_json_error(self):
        pass




