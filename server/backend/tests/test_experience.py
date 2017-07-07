__author__ = 'radu'

from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Enklave, EnklaveCombat, UserLocation, EnklaveCombatUser, UserProfile, UserCrafting
from backend.models import Turret, Shield, Faction, Brick, Cell
import json
from datetime import timedelta
from django.utils import timezone
import time
from backend.utils import crafting_lib, config_lib


class ExperienceTests(TestCase):
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

    def test_craft_brick_get_xp(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        prev_xp = self.test_user.userprofile.experience

        data = {

        }

        client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()

        user_crafting.current_action_completed_at = timezone.now() - timedelta(seconds=10)
        user_crafting.save()
        client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        self.test_user.userprofile.refresh_from_db()
        self.assertTrue(prev_xp < self.test_user.userprofile.experience)

    def test_craft_cell_get_xp(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        prev_xp = self.test_user.userprofile.experience

        data = {

        }

        client.post('/crafting/cell/build/', data=json.dumps(data), content_type='application/json')
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()

        user_crafting.current_action_completed_at = timezone.now() - timedelta(seconds=20)
        user_crafting.save()
        client.post('/crafting/cell/build/', data=json.dumps(data), content_type='application/json')

        self.test_user.userprofile.refresh_from_db()
        self.assertTrue(prev_xp < self.test_user.userprofile.experience)

    def test_craft_turret_get_xp(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        prev_xp = self.test_user.userprofile.experience

        data = {

        }

        client.post('/crafting/turret/build/', data=json.dumps(data), content_type='application/json')
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()

        user_crafting.current_action_completed_at = timezone.now() - timedelta(seconds=20)
        user_crafting.save()
        client.post('/crafting/turret/build/', data=json.dumps(data), content_type='application/json')

        self.test_user.userprofile.refresh_from_db()
        self.assertTrue(prev_xp < self.test_user.userprofile.experience)

    def test_craft_shield_get_xp(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        prev_xp = self.test_user.userprofile.experience

        data = {

        }

        client.post('/crafting/shield/build/', data=json.dumps(data), content_type='application/json')
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()

        user_crafting.current_action_completed_at = timezone.now() - timedelta(seconds=20)
        user_crafting.save()
        client.post('/crafting/shield/build/', data=json.dumps(data), content_type='application/json')

        self.test_user.userprofile.refresh_from_db()
        self.assertTrue(prev_xp < self.test_user.userprofile.experience)

    def test_install_turret_get_xp(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        enklave = Enklave.objects.create(latitude=0, longitude=0, faction=faction)
        self.test_user.userprofile.faction = faction
        self.test_user.userprofile.save()

        self.test_user.userprofile.energy = 2000
        self.test_user.userprofile.save()

        turret = Turret.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'turret_id': turret.id
        }

        UserLocation.objects.create(
            user=self.test_user,
            latitude=0,
            longitude=0
        )

        prev_xp = self.test_user.userprofile.experience

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        user_crafting.current_action_completed_at = timezone.now() - timedelta(seconds=30)
        user_crafting.save()

        client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')

        self.test_user.userprofile.refresh_from_db()
        self.assertTrue(prev_xp < self.test_user.userprofile.experience)

    def test_install_shield_get_xp(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        enklave = Enklave.objects.create(latitude=0, longitude=0, faction=faction)
        self.test_user.userprofile.faction = faction
        self.test_user.userprofile.save()

        self.test_user.userprofile.energy = 2000
        self.test_user.userprofile.save()

        shield = Shield.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'shield_id': shield.id
        }

        UserLocation.objects.create(
            user=self.test_user,
            latitude=0,
            longitude=0
        )

        prev_xp = self.test_user.userprofile.experience

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        user_crafting.current_action_completed_at = timezone.now() - timedelta(seconds=30)
        user_crafting.save()

        client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')

        self.test_user.userprofile.refresh_from_db()
        self.assertTrue(prev_xp < self.test_user.userprofile.experience)

    def test_place_brick_get_xp(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        enklave = Enklave.objects.create(latitude=0, longitude=0, faction=faction)
        self.test_user.userprofile.faction = faction
        self.test_user.userprofile.save()

        brick = Brick.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'brick_id': brick.id
        }

        UserLocation.objects.create(user=self.test_user, latitude=0, longitude=0)
        self.test_user.usercrafting.nr_bricks = 4
        self.test_user.usercrafting.save()

        self.test_user.userprofile.energy = 2000
        self.test_user.userprofile.save()

        prev_xp = self.test_user.userprofile.experience

        client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        user_crafting.current_action_completed_at -= timedelta(seconds=30)
        user_crafting.save()

        client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.test_user.userprofile.refresh_from_db()
        self.assertTrue(prev_xp < self.test_user.userprofile.experience)

    def test_get_xp_level_up(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        xp_config = config_lib.get_max_xp_config()
        first_level_xp_max = xp_config['PLAYER_XP_PROGRESSION'][0]
        prev_lvl = self.test_user.userprofile.level
        prev_energy_config = config_lib.get_energy_config_for_user(self.test_user)
        self.test_user.userprofile.experience = first_level_xp_max - 1
        self.test_user.userprofile.save()

        crafting_lib.award_experience(self.test_user, 10)
        self.test_user.userprofile.refresh_from_db()
        self.assertTrue(prev_lvl < self.test_user.userprofile.level)

        energy_config = config_lib.get_energy_config_for_user(self.test_user)
        self.assertTrue(prev_energy_config['PLAYER_ENERGY_PROGRESSION'] < energy_config['PLAYER_ENERGY_PROGRESSION'])

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['level'], 2)