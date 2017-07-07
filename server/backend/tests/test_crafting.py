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
from backend.utils import crafting_lib


class CraftingTests(TestCase):
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

    def test_energy_regen(self):

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        time.sleep(1)
        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        user_profile = UserProfile.objects.filter(user=self.test_user).first()
        self.assertNotEqual(user_profile.energy, 0)

    def test_craft_brick_(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {
        }

        response = client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'You do not have enough scrap for this action')

        self.test_user.userprofile.energy = 100
        self.test_user.userprofile.scrap = 100
        self.test_user.userprofile.save()

        response = client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        user_profile = UserProfile.objects.filter(user=self.test_user).first()
        self.assertTrue(user_profile.scrap < 100)
        self.assertTrue(user_profile.energy < 100)

        response = client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(Brick.objects.filter(crafteditem_ptr__user=self.test_user).count(), 0)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_BRICK)

        user_crafting.current_action_completed_at -= timedelta(seconds=10)
        user_crafting.save()
        response = client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(Brick.objects.filter(crafteditem_ptr__user=self.test_user).count(), 1)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_BRICK)

    def test_craft_brick_count(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {
        }

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_bricks'], 0)

        response = client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        self.test_user.userprofile.energy = 100
        self.test_user.userprofile.scrap = 100
        self.test_user.userprofile.save()

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_bricks'], 0)

        response = client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        user_profile = UserProfile.objects.filter(user=self.test_user).first()
        self.assertTrue(user_profile.scrap < 100)
        self.assertTrue(user_profile.energy < 100)

        response = client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_bricks'], 0)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(Brick.objects.filter(crafteditem_ptr__user=self.test_user).count(), 0)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_BRICK)

        user_crafting.current_action_completed_at -= timedelta(seconds=10)
        user_crafting.save()
        response = client.post('/crafting/brick/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(Brick.objects.filter(crafteditem_ptr__user=self.test_user).count(), 1)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_BRICK)
        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_bricks'], 1)

    def test_craft_cell(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {
        }

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_cells'], 0)

        response = client.post('/crafting/cell/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        self.test_user.userprofile.energy = 2500
        self.test_user.userprofile.save()

        response = client.post('/crafting/cell/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        self.test_user.userprofile.scrap = 1500
        self.test_user.userprofile.save()

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_cells'], 0)

        response = client.post('/crafting/cell/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        response = client.post('/crafting/cell/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(Cell.objects.filter(crafteditem_ptr__user=self.test_user).count(), 0)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_CELL)

        user_crafting.current_action_completed_at -= timedelta(seconds=30)
        user_crafting.save()

        self.test_user.userprofile.energy = 2500
        self.test_user.userprofile.scrap = 100
        self.test_user.userprofile.save()

        response = client.post('/crafting/cell/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        user_profile = UserProfile.objects.filter(user=self.test_user).first()
        self.assertTrue(user_profile.scrap < 100)
        self.assertTrue(user_profile.energy < 500)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(Brick.objects.filter(crafteditem_ptr__user=self.test_user).count(), 0)
        self.assertEqual(Cell.objects.filter(crafteditem_ptr__user=self.test_user).count(), 1)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_CELL)

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_cells'], 1)

    def test_use_cell(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        cell = Cell.objects.create(user=self.test_user)
        data = {
            'cell_id': cell.id
        }

        response = client.post('/crafting/cell/use/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        response = client.post('/crafting/cell/use/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        # self.assertEqual(user_crafting.nr_cells, 1)
        self.assertEqual(Cell.objects.filter(crafteditem_ptr__user=self.test_user).count(), 0)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.USE_CELL)

        user_crafting.current_action_completed_at -= timedelta(seconds=30)
        user_crafting.save()
        response = client.post('/crafting/cell/use/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        # self.assertEqual(user_crafting.nr_cells, 0)
        self.assertEqual(Cell.objects.filter(crafteditem_ptr__user=self.test_user).count(), 0)

    def test_craft_turret(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {
        }

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_turrets'], 0)

        response = client.post('/crafting/turret/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        self.test_user.userprofile.energy = 2500
        self.test_user.userprofile.save()

        response = client.post('/crafting/turret/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        self.test_user.userprofile.scrap = 1500
        self.test_user.userprofile.save()

        response = client.post('/crafting/turret/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        response = client.post('/crafting/turret/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_turrets'], 0)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(Turret.objects.filter(crafteditem_ptr__user=self.test_user).count(), 0)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_TURRET)

        user_crafting.current_action_completed_at -= timedelta(seconds=30)
        user_crafting.save()

        self.test_user.userprofile.energy = 2500
        self.test_user.userprofile.scrap = 400
        self.test_user.userprofile.save()

        response = client.post('/crafting/turret/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        user_profile = UserProfile.objects.filter(user=self.test_user).first()
        self.assertTrue(user_profile.scrap < 400)
        self.assertTrue(user_profile.energy < 2500)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_TURRET)

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_turrets'], 1)

    def test_craft_shield(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {
        }

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_shields'], 0)

        response = client.post('/crafting/shield/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        self.test_user.userprofile.energy = 2500
        self.test_user.userprofile.save()

        response = client.post('/crafting/shield/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        self.test_user.userprofile.scrap = 1500
        self.test_user.userprofile.save()

        response = client.post('/crafting/shield/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        response = client.post('/crafting/shield/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_shields'], 0)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(Shield.objects.filter(crafteditem_ptr__user=self.test_user).count(), 0)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_SHIELD)

        user_crafting.current_action_completed_at -= timedelta(seconds=30)
        user_crafting.save()

        self.test_user.userprofile.energy = 2500
        self.test_user.userprofile.scrap = 400
        self.test_user.userprofile.save()

        response = client.post('/crafting/shield/build/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        user_profile = UserProfile.objects.filter(user=self.test_user).first()
        self.assertTrue(user_profile.scrap < 400)
        self.assertTrue(user_profile.energy < 2500)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(Shield.objects.filter(user=self.test_user).count(), 1)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.CRAFT_SHIELD)

        response = client.get('/user/profile/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['nr_shields'], 1)

    def test_install_turret_input_validator(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {

        }

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_id': 1
        }

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'turret_id': 1
        }

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_id': 'abc',
            'turret_id': 1
        }

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_id': 1,
            'turret_id': 'abc'
        }

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_install_turret_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {
            'enklave_id': 1,
            'turret_id': 1
        }

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

        enklave = Enklave.objects.create(latitude=0, longitude=0)

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

        turret = Turret.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'turret_id': turret.id
        }

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertNotEqual(response.status_code, status.HTTP_404_NOT_FOUND)

    def test_install_turret_enklave_not_usable(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        turret = Turret.objects.create(user=self.test_user)

        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        data = {
            'enklave_id': enklave.id,
            'turret_id': turret.id
        }

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Enklave does not belong to any faction')

        enklave.faction = faction
        enklave.save()

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User does not belong to any faction')

        faction2 = Faction.objects.create(
            name='test2',
            description='test desc2',
            color='black2',
            logo='abc.jpg2',
            display_order=2
        )

        self.test_user.userprofile.faction = faction2
        self.test_user.userprofile.save()

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Enklave does not belong to user faction')

        # self.test_user.userprofile.faction = faction
        # self.test_user.userprofile.save()

    def test_install_turret_no_user_location(self):
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

        turret = Turret.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'turret_id': turret.id
        }

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User has not sent location data')

    def test_place_turret_proximity_error(self):
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

        turret = Turret.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'turret_id': turret.id
        }

        UserLocation.objects.create(
            user=self.test_user,
            latitude=1,
            longitude=1
        )

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User is not close enough to the selected enklave')

    def test_install_turret_enklave_not_enough_energy(self):
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

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'You do not have enough energy for this action')

    def test_install_turret_enklave_already_working(self):
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

        self.test_user.usercrafting.current_action_completed_at = timezone.now() + timedelta(seconds=30)
        self.test_user.usercrafting.save()

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

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'You are already performing an action')

    def test_install_turret_enklave_turret_not_belonging(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        test_user2 = User.objects.create_user("test_user2", "test_user2@user.com", "123456")

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

        self.test_user.usercrafting.current_action_completed_at = timezone.now() + timedelta(seconds=30)
        self.test_user.usercrafting.save()

        turret = Turret.objects.create(user=test_user2)

        data = {
            'enklave_id': enklave.id,
            'turret_id': turret.id
        }

        UserLocation.objects.create(
            user=self.test_user,
            latitude=0,
            longitude=0
        )

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Turret does not belong to user')

    def test_install_turret_all_good(self):
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

        response = client.post('/crafting/turret/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        self.assertEqual(
            Turret.objects.filter(
                crafteditem_ptr__user=self.test_user,
                crafteditem_ptr__enklave=enklave,
                crafteditem_ptr__used_at__gte=timezone.now()).count(),
            1
        )

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.INSTALL_TURRET)

        #
        # self.test_user.usercrafting.current_action_completed_at = timezone.now() - timedelta(seconds=30)
        # self.test_user.usercrafting.save()
        #
        # self.assertEqual(response.data['detail'], 'You are already performing an action')

    def test_install_shield_input_validator(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {

        }

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_id': 1
        }

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'shield_id': 1
        }

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_id': 'abc',
            'shield_id': 1
        }

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_id': 1,
            'shield_id': 'abc'
        }

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_install_shield_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {
            'enklave_id': 1,
            'shield_id': 1
        }

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

        enklave = Enklave.objects.create(latitude=0, longitude=0)

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

        shield = Shield.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'shield_id': shield.id
        }

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertNotEqual(response.status_code, status.HTTP_404_NOT_FOUND)

    def test_install_shield_enklave_not_usable(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        shield = Shield.objects.create(user=self.test_user)

        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        data = {
            'enklave_id': enklave.id,
            'shield_id': shield.id
        }

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Enklave does not belong to any faction')

        enklave.faction = faction
        enklave.save()

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User does not belong to any faction')

        faction2 = Faction.objects.create(
            name='test2',
            description='test desc2',
            color='black2',
            logo='abc.jpg2',
            display_order=2
        )

        self.test_user.userprofile.faction = faction2
        self.test_user.userprofile.save()

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Enklave does not belong to user faction')

        # self.test_user.userprofile.faction = faction
        # self.test_user.userprofile.save()

    def test_install_shield_no_user_location(self):
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

        shield = Shield.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'shield_id': shield.id
        }

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User has not sent location data')

    def test_install_shield_proximity_error(self):
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

        shield = Shield.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'shield_id': shield.id
        }

        UserLocation.objects.create(
            user=self.test_user,
            latitude=1,
            longitude=1
        )

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User is not close enough to the selected enklave')

    def test_install_shield_enklave_not_enough_energy(self):
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

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'You do not have enough energy for this action')

    def test_install_shield_enklave_already_working(self):
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

        self.test_user.usercrafting.current_action_completed_at = timezone.now() + timedelta(seconds=30)
        self.test_user.usercrafting.save()

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

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'You are already performing an action')

    def test_install_shield_enklave_shield_not_belonging(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        test_user2 = User.objects.create_user("test_user2", "test_user2@user.com", "123456")

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

        self.test_user.usercrafting.current_action_completed_at = timezone.now() + timedelta(seconds=30)
        self.test_user.usercrafting.save()

        shield = Shield.objects.create(user=test_user2)

        data = {
            'enklave_id': enklave.id,
            'shield_id': shield.id
        }

        UserLocation.objects.create(
            user=self.test_user,
            latitude=0,
            longitude=0
        )

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Shield does not belong to user')

    def test_install_shield_all_good(self):
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

        response = client.post('/crafting/shield/install/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        self.assertEqual(
            Shield.objects.filter(
                crafteditem_ptr__user=self.test_user,
                crafteditem_ptr__enklave=enklave,
                crafteditem_ptr__used_at__gte=timezone.now()).count(),
            1
        )

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type == crafting_lib.INSTALL_SHIELD)

    def test_place_brick_validator(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {

        }

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {
            'enklave_id': 1
        }

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User has no available bricks')

        data = {
            'brick_id': 1
        }

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'brick_id': 'abc'
        }

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_place_brick_enklave_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        data = {
            'enklave_id': 1,
            'brick_id': 1
        }

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

    def test_place_brick_brick_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        data = {
            'enklave_id': enklave.id,
            'brick_id': 1
        }

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

    def test_place_brick_not_enough_bricks(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        data = {
            'enklave_id': enklave.id
        }

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_place_brick_enklave_not_usable(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        brick = Brick.objects.create(user=self.test_user)
        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        data = {
            'enklave_id': enklave.id,
            'brick_id': brick.id
        }

        # response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        # self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        # self.assertEqual(response.data['detail'], 'Enklave does not belong to any faction')
        #
        # enklave.faction = faction
        # enklave.save()

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User does not belong to any faction')

        # faction2 = Faction.objects.create(
        #     name='test2',
        #     description='test desc2',
        #     color='black2',
        #     logo='abc.jpg2',
        #     display_order=2
        # )
        #
        # self.test_user.userprofile.faction = faction2
        # self.test_user.userprofile.save()
        #
        # response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        # self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        # self.assertEqual(response.data['detail'], 'Enklave does not belong to user faction')

    def test_place_brick_brick_not_usable(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)

        faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        brick = Brick.objects.create(user=self.test_user, enklave=enklave)

        data = {
            'enklave_id': enklave.id,
            'brick_id': brick.id
        }

        # response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        # self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        # self.assertEqual(response.data['detail'], 'Enklave does not belong to any faction')
        #
        # enklave.faction = faction
        # enklave.save()

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User does not belong to any faction')

        self.test_user.userprofile.faction = faction
        self.test_user.userprofile.save()

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Brick is already assigned to an enklave')

    def test_place_brick_brick_not_belonging(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        test_user2 = User.objects.create_user("test_user2", "test_user2@user.com", "123456")

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

        self.test_user.usercrafting.current_action_completed_at = timezone.now() + timedelta(seconds=30)
        self.test_user.usercrafting.save()

        brick = Brick.objects.create(user=test_user2)

        data = {
            'enklave_id': enklave.id,
            'brick_id': brick.id
        }

        UserLocation.objects.create(
            user=self.test_user,
            latitude=0,
            longitude=0
        )

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Brick does not belong to user')

    def test_place_brick_no_user_location(self):
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

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User has not sent location data')

    def test_place_brick_proximity_error(self):
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

        UserLocation.objects.create(
            user=self.test_user,
            latitude=1,
            longitude=1
        )

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User is not close enough to the selected enklave')

    def test_place_brick_already_working(self):
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

        self.test_user.usercrafting.current_action_completed_at = timezone.now() + timedelta(seconds=30)
        self.test_user.usercrafting.save()

        brick = Brick.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id,
            'brick_id': brick.id
        }

        UserLocation.objects.create(user=self.test_user, latitude=0, longitude=0)
        self.test_user.usercrafting.nr_bricks = 4
        self.test_user.usercrafting.save()

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'You are already performing an action')

    def test_place_brick_no_energy(self):
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

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'You do not have enough energy for this action')

    def test_place_brick_no_problem(self):
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

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(user_crafting.nr_bricks, 3)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type, crafting_lib.PLACE_BRICK)
        self.assertTrue(user_crafting.current_action_enklave, enklave)

        enklave = Enklave.objects.filter(id=enklave.id).first()
        self.assertEqual(enklave.bricks, 0)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()

        user_crafting.current_action_completed_at -= timedelta(seconds=30)
        user_crafting.save()

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Brick is already assigned to an enklave')

        enklave = Enklave.objects.filter(id=enklave.id).first()
        self.assertEqual(Brick.objects.filter(crafteditem_ptr__enklave=enklave).count(), 1)

        response = client.get('/enklave/nearby/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        found = False
        for item in response.data:
            if item['id'] == enklave.id:
                found = True
                self.assertEqual(item['nr_bricks'], Brick.objects.filter(crafteditem_ptr__enklave=enklave).count())

        self.assertTrue(found)

    def test_place_random_brick_no_problem(self):
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
            'enklave_id': enklave.id
        }

        UserLocation.objects.create(user=self.test_user, latitude=0, longitude=0)
        self.test_user.usercrafting.nr_bricks = 4
        self.test_user.usercrafting.save()

        self.test_user.userprofile.energy = 2000
        self.test_user.userprofile.save()

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()
        self.assertEqual(user_crafting.nr_bricks, 3)
        self.assertTrue(user_crafting.current_action_completed_at > timezone.now())
        self.assertTrue(user_crafting.current_action_type, crafting_lib.PLACE_BRICK)
        self.assertTrue(user_crafting.current_action_enklave, enklave)

        enklave = Enklave.objects.filter(id=enklave.id).first()
        self.assertEqual(enklave.bricks, 0)

        user_crafting = UserCrafting.objects.filter(user=self.test_user).first()

        user_crafting.current_action_completed_at -= timedelta(seconds=30)
        user_crafting.save()

        data = {
            'brick_id': brick.id,
            'enklave_id': enklave.id
        }

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Brick is already assigned to an enklave')

        data = {
            'enklave_id': enklave.id
        }

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User has no available bricks')

        enklave = Enklave.objects.filter(id=enklave.id).first()
        self.assertEqual(Brick.objects.filter(crafteditem_ptr__enklave=enklave).count(), 1)

        response = client.get('/enklave/nearby/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        found = False
        for item in response.data:
            if item['id'] == enklave.id:
                found = True
                self.assertEqual(item['nr_bricks'], Brick.objects.filter(crafteditem_ptr__enklave=enklave).count())

        self.assertTrue(found)

        brick.refresh_from_db()
        self.assertEqual(brick.enklave, enklave)

    def test_place_random_brick_and_occupy_not_enough(self):
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

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        # enklave = Enklave.objects.create(latitude=0, longitude=0, faction=faction)
        self.test_user.userprofile.faction = faction
        self.test_user.userprofile.save()

        brick = Brick.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id
        }

        UserLocation.objects.create(user=self.test_user, latitude=0, longitude=0)
        self.test_user.usercrafting.nr_bricks = 4
        self.test_user.usercrafting.save()

        self.test_user.userprofile.energy = 2000
        self.test_user.userprofile.save()

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        enklave.refresh_from_db()
        self.assertTrue(enklave.faction is None)

    def test_place_random_brick_and_occupy_is_still_not_enough(self):
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

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        self.test_user.userprofile.faction = faction
        self.test_user.userprofile.save()

        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)

        brick = Brick.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id
        }

        UserLocation.objects.create(user=self.test_user, latitude=0, longitude=0)
        self.test_user.usercrafting.nr_bricks = 4
        self.test_user.usercrafting.save()

        self.test_user.userprofile.energy = 2000
        self.test_user.userprofile.save()

        self.assertTrue(enklave.faction is None)

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        enklave.refresh_from_db()
        self.assertTrue(enklave.faction is None)

    def test_place_random_brick_and_occupy_is_enough(self):
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

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        self.test_user.userprofile.faction = faction
        self.test_user.userprofile.save()

        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)
        Brick.objects.create(user=self.test_user, enklave=enklave)

        brick = Brick.objects.create(user=self.test_user)

        data = {
            'enklave_id': enklave.id
        }

        UserLocation.objects.create(user=self.test_user, latitude=0, longitude=0)
        self.test_user.usercrafting.nr_bricks = 4
        self.test_user.usercrafting.save()

        self.test_user.userprofile.energy = 2000
        self.test_user.userprofile.save()

        self.assertTrue(enklave.faction is None)

        response = client.post('/crafting/brick/place/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        enklave.refresh_from_db()
        self.assertTrue(enklave.faction == faction)

