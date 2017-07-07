__author__ = 'radu'

from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Enklave, UserLocation
from backend.models import Raider2, RaiderPosition, RaiderCombat, RaiderCombatRaider, RaiderCombatUser
from backend.models import Brick, Turret, Shield
from datetime import timedelta
from django.utils import timezone
import time
from backend.utils import combat_lib, processing
from backend.utils.redis_lib import RedisLib
redis_lib = RedisLib()
import json


class RaidersTests(TestCase):
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

    def test_raider_create(self):

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        bearing = 22
        lat2, lon2 = 11, 12

        raider = Raider2.objects.create(
            enklave=enklave,
            level=1,
            energy=1,
            latitude=lat2,
            longitude=lon2,
            bearing=bearing,
            hits_at=timezone.now()
        )

        self.assertEqual(raider.enklave, enklave)

    def test_raider_generator(self):

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider_list = [combat_lib.generate_raider_object(enklave)]

        raiders = Raider2.objects.bulk_create(raider_list)

        self.assertEqual(raiders[0].enklave, enklave)
        self.assertTrue(raiders[0].hits_at > timezone.now())

    def test_generate_raider_positions(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider_list = [combat_lib.generate_raider_object(enklave)]
        raiders = Raider2.objects.bulk_create(raider_list)

        raider_position_list = combat_lib.generate_raider_position_objects(raiders[0])
        raider_positions = RaiderPosition.objects.bulk_create(raider_position_list)

        self.assertTrue(len(raider_positions), combat_lib.num_hours)
        self.assertTrue(abs(raider_positions[combat_lib.num_hours+1].latitude - enklave.latitude) < 0.01)
        self.assertTrue(abs(raider_positions[combat_lib.num_hours+1].longitude - enklave.longitude) < 0.01)

    def test_nearby_raiders_no_position_set(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        response = client.get('/raider/nearby/')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'User has not sent location data')

    def test_nearby_raiders_not_found(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider_list = [combat_lib.generate_raider_object(enklave)]
        raiders = Raider2.objects.bulk_create(raider_list)

        raider_position_list = combat_lib.generate_raider_position_objects(raiders[0])
        RaiderPosition.objects.bulk_create(raider_position_list)
        # raider_positions = RaiderPosition.objects.bulk_create(raider_position_list)

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=20,
            longitude=20
        )

        response = client.get('/raider/nearby/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 0)

    def test_nearby_raiders_found(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider_list = [combat_lib.generate_raider_object(enklave)]
        raiders = Raider2.objects.bulk_create(raider_list)

        raider_position_list = combat_lib.generate_raider_position_objects(raiders[0])
        raider_positions = RaiderPosition.objects.bulk_create(raider_position_list)

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=raider_positions[0].latitude,
            longitude=raider_positions[0].longitude
        )

        response = client.get('/raider/nearby/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['id'], raiders[0].id)

    def test_nearby_raiders_found_on_path_not_found(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider_list = [combat_lib.generate_raider_object(enklave)]
        raiders = Raider2.objects.bulk_create(raider_list)

        raiders[0].created_at -= timedelta(hours=2)
        raiders[0].save()

        raider = Raider2.objects.get(pk=raiders[0].id)

        raider_position_list = combat_lib.generate_raider_position_objects(raider)
        raider_positions = RaiderPosition.objects.bulk_create(raider_position_list)

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=raider_positions[0].latitude,
            longitude=raider_positions[0].longitude
        )

        response = client.get('/raider/nearby/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 0)

    def test_nearby_raiders_found_on_path(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider_list = [combat_lib.generate_raider_object(enklave)]
        raiders = Raider2.objects.bulk_create(raider_list)

        raiders[0].created_at -= timedelta(hours=2)
        raiders[0].save()

        raider = Raider2.objects.get(pk=raiders[0].id)

        raider_position_list = combat_lib.generate_raider_position_objects(raider)
        raider_positions = RaiderPosition.objects.bulk_create(raider_position_list)

        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        user_location = UserLocation.objects.create(
            user=self.test_user,
            latitude=raider_positions[2].latitude,
            longitude=raider_positions[2].longitude
        )

        response = client.get('/raider/nearby/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['id'], raiders[0].id)

        user_location.latitude = raider_positions[0].latitude
        user_location.longitude = raider_positions[0].longitude
        user_location.save()

        response = client.get('/raider/nearby/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 0)

        user_location.latitude = raider_positions[1].latitude
        user_location.longitude = raider_positions[1].longitude
        user_location.save()

        response = client.get('/raider/nearby/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 0)


        # raider_data['current_latitude'] = raider_position.latitude
        # raider_data['current_longitude'] = raider_position.longitude
        # self.assertEqual(response.data[0]['current_latitude'], raider_positions[3].latitude)
        # self.assertEqual(response.data[0]['current_longitude'], raider_positions[3].longitude)

    def test_raider_hit(self):

        enklave = Enklave.objects.create(latitude=0, longitude=0, bricks=5)
        raider_list = [combat_lib.generate_raider_object(enklave)]
        raiders = Raider2.objects.bulk_create(raider_list)

        raiders[0].created_at -= timedelta(hours=14)
        raiders[0].hits_at = timezone.now()
        raiders[0].save()

        enklaves_in_combat = combat_lib.get_enklaves_in_combat()
        self.assertEqual(len(enklaves_in_combat), 0)

        raiders_that_hit = combat_lib.get_raider_that_arrived_for_combat()
        self.assertEqual(len(raiders_that_hit), 1)
        self.assertEqual(raiders_that_hit[0], raiders[0])

        self.assertEqual(RaiderCombat.objects.filter(enklave=enklave).count(), 0)

        combat_lib.raiders_hit_action()

        self.assertEqual(RaiderCombat.objects.filter(enklave=enklave).count(), 1)
        self.assertEqual(RaiderCombatRaider.objects.count(), 1)

        raider_list = [combat_lib.generate_raider_object(enklave)]
        raiders = Raider2.objects.bulk_create(raider_list)

        raiders[0].created_at -= timedelta(hours=14)
        raiders[0].hits_at = timezone.now()
        raiders[0].save()

        combat_lib.raiders_hit_action()

        self.assertEqual(RaiderCombat.objects.filter(enklave=enklave).count(), 1)
        self.assertEqual(RaiderCombatRaider.objects.count(), 2)

    def test_user_joined_raider_combat(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0, bricks=5)
        raider_combat = RaiderCombat.objects.create(enklave=enklave)
        self.assertEqual(RaiderCombatUser.objects.filter(user=self.test_user, raider_combat=raider_combat).count(), 0)
        raider_combat_user = combat_lib.user_join_raider_combat(self.test_user, raider_combat)
        self.assertEqual(RaiderCombatUser.objects.filter(user=self.test_user, raider_combat=raider_combat).count(), 1)
        self.assertEqual(raider_combat_user.user, self.test_user)

    def test_combat_status_change(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0, bricks=5)
        raider = Raider2.objects.create(enklave=enklave, hits_at=timezone.now(), latitude=0, longitude=0, bearing=1)
        raider_combat = RaiderCombat.objects.create(enklave=enklave)
        RaiderCombatRaider.objects.create(raider=raider, raider_combat=raider_combat)
        combat_lib.user_join_raider_combat(self.test_user, raider_combat)

        combat_status = combat_lib.get_raider_combat_status(raider_combat)
        self.assertEqual(len(combat_status['raiders']), 1)
        self.assertEqual(len(combat_status['users']), 1)

        test_user2 = User.objects.create_user("test_user2", "test_user2@user.com", "123456")

        combat_lib.user_join_raider_combat(test_user2, raider_combat)

        combat_status = combat_lib.get_raider_combat_status(raider_combat)
        self.assertEqual(len(combat_status['raiders']), 1)
        self.assertEqual(len(combat_status['users']), 2)

        test_user3 = User.objects.create_user("test_user3", "test_user3@user.com", "123456")

        combat_lib.user_join_raider_combat(test_user3, raider_combat)

        combat_status = combat_lib.get_raider_combat_status(raider_combat)
        self.assertEqual(len(combat_status['raiders']), 1)
        self.assertEqual(len(combat_status['users']), 3)

        # Do not add same user to combat again
        combat_lib.user_join_raider_combat(test_user3, raider_combat)
        self.assertEqual(RaiderCombatUser.objects.filter(raider_combat=raider_combat).count(), 3)
        self.assertEqual(RaiderCombatRaider.objects.filter(raider=raider, raider_combat=raider_combat).count(), 1)

        combat_lib.user_leaves_raider_combat(test_user3, raider_combat)
        combat_status = combat_lib.get_raider_combat_status(raider_combat)
        self.assertEqual(len(combat_status['raiders']), 1)
        self.assertEqual(len(combat_status['users']), 2)

        # Test if leaving a second time doesn't change anything
        combat_lib.user_leaves_raider_combat(test_user3, raider_combat)
        combat_status = combat_lib.get_raider_combat_status(raider_combat)
        self.assertEqual(len(combat_status['raiders']), 1)
        self.assertEqual(len(combat_status['users']), 2)

        # Test if user can join a second time (it will be disabled on a higher level)
        combat_lib.user_join_raider_combat(test_user3, raider_combat)
        self.assertEqual(RaiderCombatUser.objects.filter(raider_combat=raider_combat).count(), 3)
        self.assertEqual(RaiderCombatRaider.objects.filter(raider=raider, raider_combat=raider_combat).count(), 1)

    def test_combat_raider_generate_actions(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0, bricks=5)
        raider = Raider2.objects.create(enklave=enklave, hits_at=timezone.now(), latitude=0, longitude=0, bearing=1)
        raider_combat = RaiderCombat.objects.create(enklave=enklave)
        rcr = RaiderCombatRaider.objects.create(raider=raider, raider_combat=raider_combat)

        actions = combat_lib.generate_raider_combat_actions(raider_combat, timezone.now())
        for action in actions:
            self.assertEqual(action['target'], 'enklave_{0}'.format(enklave.id))
            self.assertEqual(action['attacker'], 'raider_cr_{0}'.format(rcr.id))

    def test_combat_raider_generate_actions2(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0, bricks=5)
        raider = Raider2.objects.create(enklave=enklave, hits_at=timezone.now(), latitude=0, longitude=0, bearing=1)
        raider_combat = RaiderCombat.objects.create(enklave=enklave)
        rcr = RaiderCombatRaider.objects.create(raider=raider, raider_combat=raider_combat)
        ucu = RaiderCombatUser.objects.create(user=self.test_user, raider_combat=raider_combat)

        actions = combat_lib.generate_raider_combat_actions(raider_combat, timezone.now())
        for action in actions:
            self.assertTrue(action['target'], ['enklave_{0}'.format(enklave.id), 'user_cu_{0}'.format(ucu.id)])
            self.assertEqual(action['attacker'], 'raider_cr_{0}'.format(rcr.id))

    def test_combat_raider_generate_actions2_test_next_action(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0, bricks=5)
        raider = Raider2.objects.create(enklave=enklave, hits_at=timezone.now(), latitude=0, longitude=0, bearing=1)
        raider_combat = RaiderCombat.objects.create(enklave=enklave)
        rcr = RaiderCombatRaider.objects.create(raider=raider, raider_combat=raider_combat)
        ucu = RaiderCombatUser.objects.create(user=self.test_user, raider_combat=raider_combat)

        actions = combat_lib.generate_raider_combat_actions(raider_combat, timezone.now())
        for action in actions:
            self.assertTrue(action['target'], ['enklave_{0}'.format(enklave.id), 'user_cu_{0}'.format(ucu.id)])
            self.assertEqual(action['attacker'], 'raider_cr_{0}'.format(rcr.id))

        next_action = combat_lib.get_action_for_time(actions, timezone.now() - timedelta(seconds=5))
        self.assertEqual(next_action, None)
        next_action = combat_lib.get_action_for_time(actions, timezone.now() + timedelta(seconds=6))
        self.assertEqual(next_action['attacker'], 'raider_cr_{0}'.format(rcr.id))

        next_action2 = combat_lib.get_action_for_time(actions, timezone.now() + timedelta(seconds=12))
        self.assertEqual(next_action['attacker'], 'raider_cr_{0}'.format(rcr.id))
        self.assertTrue(int(next_action2['starts_at']) > int(next_action['starts_at']))

    def test_store_sequence_result(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0, bricks=5)
        raider = Raider2.objects.create(enklave=enklave, hits_at=timezone.now(), latitude=0, longitude=0, bearing=1)
        raider_combat = RaiderCombat.objects.create(enklave=enklave)
        rcr = RaiderCombatRaider.objects.create(raider=raider, raider_combat=raider_combat)
        ucu = RaiderCombatUser.objects.create(user=self.test_user, raider_combat=raider_combat)

        actions = combat_lib.generate_raider_combat_actions(raider_combat, timezone.now())
        combat_lib.store_attack_sequence(attack_sequence=actions, raider_combat=raider_combat)

        actions2 = combat_lib.get_stored_attack_sequence(raider_combat)
        self.assertTrue(actions, actions2)

        for action in actions2:
            self.assertTrue(action['target'], ['enklave_{0}'.format(enklave.id), 'user_cu_{0}'.format(ucu.id)])
            self.assertEqual(action['attacker'], 'raider_cr_{0}'.format(rcr.id))

        next_action = combat_lib.get_action_for_time(actions2, timezone.now() - timedelta(minutes=5))
        self.assertEqual(next_action, None)
        next_action = combat_lib.get_action_for_time(actions2, timezone.now() + timedelta(seconds=6))
        self.assertEqual(next_action['attacker'], 'raider_cr_{0}'.format(rcr.id))

        redis_lib.remove_combat_sequence(raider_combat)

    def test_attack_process_sequence(self):
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        brick = Brick.objects.create(enklave=enklave, energy=200, user=self.test_user)
        raider = Raider2.objects.create(enklave=enklave, hits_at=timezone.now(), latitude=0, longitude=0, bearing=1)
        raider_combat = RaiderCombat.objects.create(enklave=enklave)
        rcr = RaiderCombatRaider.objects.create(raider=raider, raider_combat=raider_combat)
        # ucu = RaiderCombatUser.objects.create(user=self.test_user, raider_combat=raider_combat)

        actions = combat_lib.generate_raider_combat_actions(raider_combat, timezone.now())
        res = combat_lib.process_raider_combat_actions(raider_combat, actions, timezone.now() + timedelta(minutes=20))

        # 12 sequences of hitting a brick
        brick.refresh_from_db()
        self.assertEqual(brick.energy, 200 - 12*5)

        actions = combat_lib.generate_raider_combat_actions(raider_combat, timezone.now())
        res = combat_lib.process_raider_combat_actions(raider_combat, actions, timezone.now() + timedelta(minutes=20))

        # 12 sequences of hitting a brick
        brick.refresh_from_db()
        self.assertEqual(brick.energy, 200 - 24*5)

        brick.energy = 10
        brick.save()

        actions = combat_lib.generate_raider_combat_actions(raider_combat, timezone.now())
        res = combat_lib.process_raider_combat_actions(raider_combat, actions, timezone.now() + timedelta(minutes=20))

        # 12 sequences of hitting a brick
        brick.refresh_from_db()
        self.assertEqual(brick.energy, 0)

        enklave.refresh_from_db()
        self.assertTrue(enklave.destroyed_at is not None)

    def test_get_raiders_for_enklave_validation(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        enklave = Enklave.objects.create(latitude=0, longitude=0)

        response = client.get('/raider/enklave/')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is required: enklave_id')

    def test_get_raiders_for_enklave_bad_input(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        enklave = Enklave.objects.create(latitude=0, longitude=0)

        response = client.get('/raider/enklave/?enklave_id=abc')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is not a valid integer: enklave_id')

    def test_get_raiders_for_enklave_good_no_raiders(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        enklave = Enklave.objects.create(latitude=0, longitude=0)

        response = client.get('/raider/enklave/?enklave_id={0}'.format(enklave.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 0)

    def test_get_raiders_for_enklave_good(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        enklave = Enklave.objects.create(latitude=0, longitude=0)

        raider = Raider2.objects.create(
            latitude=1,
            longitude=1,
            enklave=enklave
        )

        raider_position = RaiderPosition.objects.create(
            raider=raider,
            latitude=.9,
            longitude=.9,

            starts_at=timezone.now() - timedelta(seconds=10),
            ends_at=timezone.now() + timedelta(minutes=10)
        )

        response = client.get('/raider/enklave/?enklave_id={0}'.format(enklave.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 1)
        self.assertEqual(response.data[0]['id'], raider.id)
        self.assertEqual(response.data[0]['longitude_start'], raider.longitude)
        self.assertEqual(response.data[0]['latitude_start'], raider.latitude)
        self.assertEqual(response.data[0]['current_latitude'], raider_position.latitude)
        self.assertEqual(response.data[0]['current_longitude'], raider_position.longitude)

        raider2 = Raider2.objects.create(
            latitude=1,
            longitude=1,
            enklave=enklave
        )

        raider_position2 = RaiderPosition.objects.create(
            raider=raider2,
            latitude=.9,
            longitude=.9,

            starts_at=timezone.now() - timedelta(seconds=10),
            ends_at=timezone.now() + timedelta(minutes=10)
        )

        response = client.get('/raider/enklave/?enklave_id={0}'.format(enklave.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 2)
        self.assertEqual(response.data[0]['id'], raider.id)
        self.assertEqual(response.data[0]['longitude_start'], raider.longitude)
        self.assertEqual(response.data[0]['latitude_start'], raider.latitude)
        self.assertEqual(response.data[0]['current_latitude'], raider_position.latitude)
        self.assertEqual(response.data[0]['current_longitude'], raider_position.longitude)

        self.assertEqual(response.data[1]['id'], raider2.id)
        self.assertEqual(response.data[1]['longitude_start'], raider2.longitude)
        self.assertEqual(response.data[1]['latitude_start'], raider2.latitude)
        self.assertEqual(response.data[1]['current_latitude'], raider_position2.latitude)
        self.assertEqual(response.data[1]['current_longitude'], raider_position2.longitude)
        # print response.data

    def test_get_raider_by_id_validator(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/raider/get/')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is required: raider_id')

    def test_get_raider_by_id_bad_input(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/raider/get/?raider_id=abc')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is not a valid uuid hex: raider_id')

    def test_get_raider_by_id_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider2 = Raider2.objects.create(
            latitude=1,
            longitude=1,
            enklave=enklave
        )

        raider_id = raider2.id
        raider2.delete()

        response = client.get('/raider/get/?raider_id={0}'.format(raider_id))
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['detail'], 'Raider not found')

    def test_get_raider_by_id_good(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider2 = Raider2.objects.create(
            latitude=1,
            longitude=1,
            enklave=enklave
        )
        response = client.get('/raider/get/?raider_id={0}'.format(raider2.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['id'], raider2.id)
        self.assertEqual(response.data['bearing'], raider2.bearing)
        self.assertEqual(response.data['enklave_id'], enklave.id)








