__author__ = 'radu'

from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application, RefreshToken
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
import json
from datetime import timedelta
from django.utils import timezone
from backend.utils import helpers
import time
from django.http import HttpRequest



class HelperTests(TestCase):
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

    def test_helpers(self):
        self._test_pagination_no_page()
        self._test_pagination_bad_input()
        self._test_pagination_good_input_page1()
        self._test_pagination_good_input_page2()
        self._test_format_oauth_data()

    def _test_pagination_no_page(self):
        request = HttpRequest()
        offset, limit = helpers.get_pagination_from_request(request)
        self.assertEqual(offset, 0)
        self.assertEqual(limit, 29)

    def _test_pagination_bad_input(self):
        request = HttpRequest()
        request.GET.setdefault('page', 'abc')
        offset, limit = helpers.get_pagination_from_request(request)
        self.assertEqual(offset, 0)
        self.assertEqual(limit, 29)

    def _test_pagination_good_input_page1(self):
        request = HttpRequest()
        request.GET.setdefault('page', 1)
        offset, limit = helpers.get_pagination_from_request(request)
        self.assertEqual(offset, 0)
        self.assertEqual(limit, 29)

    def _test_pagination_good_input_page0(self):
        request = HttpRequest()
        request.GET.setdefault('page', -1)
        offset, limit = helpers.get_pagination_from_request(request)
        self.assertEqual(offset, 0)
        self.assertEqual(limit, 29)

        # request.GET.setdefault('page', 2)
        # print request.GET.get('page')
        # offset, limit = helpers.get_pagination_from_request(request)
        # self.assertEqual(offset, 30)
        # self.assertEqual(limit, 59)

    def _test_pagination_good_input_page2(self):
        request = HttpRequest()
        request.GET.setdefault('page', 2)
        offset, limit = helpers.get_pagination_from_request(request)
        self.assertEqual(offset, 30)
        self.assertEqual(limit, 59)

    def _test_format_oauth_data(self):
        access_token = AccessToken.objects.create(
            user=self.test_user, token='aaa',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        refresh_token = RefreshToken.objects.create(
            user=self.test_user,
            token='bbb',
            access_token=access_token,
            application=self.application
        )

        res = helpers.format_oauth_data(access_token, refresh_token)
        self.assertEqual(res['access_token'], 'aaa')
        self.assertEqual(res['refresh_token'], 'bbb')
        self.assertEqual(res['expires'], access_token.expires)
