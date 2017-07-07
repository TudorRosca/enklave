from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Turret, ResetPasswordToken
import json
from datetime import timedelta
from django.utils import timezone


class UserTests(TestCase):
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

    def test_register(self):
        data = {
            'username': 'radu',
            'email': 'radu@stoica.com',
            'password': 'radu'
        }
        url = reverse('register')
        response = self.client.post(url, data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(User.objects.count(), 2)
        self.assertEqual(User.objects.filter(username='radu').first().username, 'radu')

        data = {
            'username': 'radu',
            'email': 'radu@stoica.com',
            'password': 'radu'
        }
        url = reverse('register')
        response = self.client.post(url, data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 2)

    def test_register2(self):
        data = {
            'username': 'radu1',
            'first_name': 'radu1',
            'last_name': 'radu1',
            'email': 'radu1@stoica.com',
            'password': 'radu1'
        }
        url = reverse('register')
        response = self.client.post(url, data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(User.objects.count(), 2)
        self.assertEqual(User.objects.filter(username='radu1').first().username, 'radu1')

    def test_register_no_email(self):
        data = {
            'username': 'radu',
            'first_name': 'radu',
            'password': 'radu'
        }
        url = reverse('register')
        response = self.client.post(url, data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)

    def test_register_no_username_should_work(self):
        data = {
            # 'username': 'radu',
            'first_name': 'radu',
            'email': 'radu1@stoica.com',
            'password': 'radu'
        }
        url = reverse('register')
        self.assertEqual(User.objects.filter(email='radu1@stoica.com').count(), 0)
        response = self.client.post(url, data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(User.objects.filter(email='radu1@stoica.com').count(), 1)

    def test_login(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/user/stats/')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        # self.assertTrue('username' in response.data)

    def test_login_bad(self):

        AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + '123')
        response = client.get('/user/stats/')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    def test_profile(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        self.assertEqual(response.data['scrap'], 0)
        self.assertEqual(response.data['nr_bricks'], 0)
        self.assertEqual(response.data['nr_cells'], 0)
        self.assertEqual(response.data['nr_cells'], 0)

        Turret.objects.create(user=self.test_user)

        turrets = Turret.objects\
            .filter(enklave__isnull=True, user=self.test_user) \
            .prefetch_related('user', 'enklave')

        self.assertTrue(len(turrets), 1)

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertTrue('energy_config' in response.data)
        self.assertTrue('PLAYER_ENERGY_PROGRESSION' in response.data['energy_config'])
        self.assertTrue('PLAYER_RECHARGE_PROGRESSION' in response.data['energy_config'])

        self.assertTrue('brick_config' in response.data)
        self.assertTrue('CRAFT_BRICK_SCRAP_COST' in response.data['brick_config'])
        self.assertTrue('CRAFT_BRICK_TIME_COST' in response.data['brick_config'])
        self.assertTrue('CRAFT_BRICK_ENERGY_COST' in response.data['brick_config'])

        self.assertTrue('turret_config' in response.data)
        self.assertTrue('CRAFT_TURRET_SCRAP_COST' in response.data['turret_config'])
        self.assertTrue('CRAFT_TURRET_ENERGY_COST' in response.data['turret_config'])
        self.assertTrue('CRAFT_TURRET_TIME_COST' in response.data['turret_config'])

        self.assertTrue('shield_config' in response.data)
        self.assertTrue('CRAFT_SHIELD_SCRAP_COST' in response.data['shield_config'])
        self.assertTrue('CRAFT_SHIELD_ENERGY_COST' in response.data['shield_config'])
        self.assertTrue('CRAFT_SHIELD_TIME_COST' in response.data['shield_config'])
        self.assertTrue('xp_for_level' in response.data)
        self.assertTrue('distance_walked' in response.data)
        self.assertTrue('level' in response.data)

        # codes = ['CRAFT_BRICK_SCRAP_COST', 'CRAFT_BRICK_TIME_COST', 'CRAFT_BRICK_ENERGY_COST']

        # 'PLAYER_ENERGY_PROGRESSION': storage_level_value,
        # 'PLAYER_RECHARGE_PROGRESSION': recharge_level_value

        # user_data['energy_config'] = energy_config_data
        # brick_config_data = config_lib.get_craft_brick_configs()
        # user_data['brick_config'] = brick_config_data
        # max_xp_for_user = config_lib.get_max_xp_for_user(user)
        # user_data['xp_for_level'] = max_xp_for_user
        # user_data['turret_config'] = config_lib.get_craft_turret_configs()
        # user_data['shield_config'] = config_lib.get_craft_shield_configs()

        # self.assertEqual(response.data['turrets'][0]['id'], turrets[0].id)
        # self.assertEqual(response.data['turrets'][0]['user_id'], turrets[0].user.id)

    def test_get_user_public_data_validation(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/user/get/')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is required: user_id')

    def test_get_user_public_data_bad_input(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/user/get/?user_id=aa')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is not a valid integer: user_id')

    def test_get_user_public_data_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/user/get/?user_id=100')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['detail'], 'User not found')

    def test_get_user_public_data_good(self):
        test_user2 = User.objects.create_user("test_user2", "test_user2@user.com", "123456")

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/user/get/?user_id={0}'.format(test_user2.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['id'], test_user2.id)
        self.assertEqual(response.data['username'], test_user2.username)

    def test_forgot_password_validate(self):
        client = APIClient()
        data = {

        }
        response = client.post('/forgot_password/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "u'email' is a required property")

    def test_forgot_password_bad_input(self):
        client = APIClient()
        data = {
            'email': 'abc'
        }
        response = client.post('/forgot_password/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "u'abc' is not a u'email'")

    def test_forgot_password_not_found(self):
        client = APIClient()
        data = {
            'email': 'abc@fake.com'
        }
        response = client.post('/forgot_password/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['detail'], "Email Address not found")

    def test_forgot_password_not_active(self):
        test_user3 = User.objects.create_user("test_user3", "test_user3@user.com", "123456")
        test_user3.is_active = False
        test_user3.save()
        client = APIClient()
        data = {
            'email': test_user3.email
        }
        response = client.post('/forgot_password/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['detail'], "Email inactive")

    def test_forgot_password(self):
        client = APIClient()
        data = {
            'email': self.test_user.email
        }
        response = client.post('/forgot_password/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)


#   TODO add tests for user changes
    def test_reset_password_get_no_token(self):
        client = APIClient()
        response = client.get('/reset_password/')
        self.assertContains(response, "You don't have a token!")

    def test_reset_password_get_bad_token(self):
        client = APIClient()
        response = client.get('/reset_password/?token=token')
        self.assertContains(response, "This token is either wrong or it has expired")

    def test_reset_password_get_expired(self):
        client = APIClient()
        ResetPasswordToken.objects.create(
            user=self.test_user,
            token='token',
            expire_date=timezone.now() - timedelta(seconds=10)
        )
        response = client.get('/reset_password/?token=token')
        self.assertContains(response, "This token is either wrong or it has expired")

    def test_reset_password_get_good(self):
        client = APIClient()
        ResetPasswordToken.objects.create(
            user=self.test_user,
            token='token',
            expire_date=timezone.now() + timedelta(seconds=10)
        )
        response = client.get('/reset_password/?token=token')
        self.assertContains(response, '<input type="hidden" name="token_code" value="token"/>')

    def test_reset_password_post_validator_no_password(self):
        data = {
            # 'password': 'abc',
            'password_repeat': 'abc1',
            'token_code': 'token'
        }

        ResetPasswordToken.objects.create(
            user=self.test_user,
            token='token',
            expire_date=timezone.now() + timedelta(seconds=10)
        )
        client = APIClient()
        response = client.post('/reset_password/?token=token', data=data)
        self.assertContains(response, 'You must set the password field')

    def test_reset_password_no_password_repeat(self):
        data = {
            'password': 'abc',
            # 'password_repeat': 'abc1',
            'token_code': 'token'
        }

        ResetPasswordToken.objects.create(
            user=self.test_user,
            token='token',
            expire_date=timezone.now() + timedelta(seconds=10)
        )
        client = APIClient()
        response = client.post('/reset_password/?token=token', data=data)
        self.assertContains(response, 'You must set the repeat password field')

    def test_reset_password_no_token(self):
        data = {
            'password': 'abc',
            'password_repeat': 'abc1',
            # 'token_code': 'token'
        }

        ResetPasswordToken.objects.create(
            user=self.test_user,
            token='token',
            expire_date=timezone.now() + timedelta(seconds=10)
        )
        client = APIClient()
        response = client.post('/reset_password/?token=token', data=data)
        self.assertContains(response, "This token is either wrong or it has expired")

    def test_reset_password_mismatched_passwords(self):
        data = {
            'password': 'abc',
            'password_repeat': 'abc1',
            'token_code': 'token'
        }

        ResetPasswordToken.objects.create(
            user=self.test_user,
            token='token',
            expire_date=timezone.now() + timedelta(seconds=10)
        )

        client = APIClient()
        response = client.post('/reset_password/?token=token', data=data)
        # print response
        self.assertContains(response, "The two passwords didn&#39;t match!")

    def test_reset_password_short_password(self):
        data = {
            'password': 'ab',
            'password_repeat': 'ab',
            'token_code': 'token'
        }

        ResetPasswordToken.objects.create(
            user=self.test_user,
            token='token',
            expire_date=timezone.now() + timedelta(seconds=10)
        )

        client = APIClient()
        response = client.post('/reset_password/?token=token', data=data)
        self.assertContains(response, 'The password was too short!')

    def test_reset_password_good(self):
        data = {
            'password': 'abc',
            'password_repeat': 'abc',
            'token_code': 'token'
        }

        ResetPasswordToken.objects.create(
            user=self.test_user,
            token='token',
            expire_date=timezone.now() + timedelta(seconds=10)
        )

        self.test_user.refresh_from_db()
        self.assertTrue(self.test_user.check_password('123456'))

        client = APIClient()
        response = client.post('/reset_password/?token=token', data=data)
        self.assertContains(response, 'Successfully Updated your Password')

        self.test_user.refresh_from_db()

        self.assertTrue(self.test_user.check_password('abc'))
        self.assertFalse(self.test_user.check_password('123456'))


