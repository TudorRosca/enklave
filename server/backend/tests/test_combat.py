__author__ = 'radu'
from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import AccessToken, Application
from django.core.urlresolvers import reverse
from rest_framework import status
from rest_framework.test import APIClient
from backend.models import Enklave, EnklaveCombat, UserLocation, EnklaveCombatUser, RaiderCombat, Raider2, \
    RaiderCombatRaider, RaiderPosition, RaiderCombatUser, EnklaveSubscriber, Faction
import json
from datetime import timedelta
from django.utils import timezone
from backend.utils.redis_lib import RedisLib
from backend.utils import combat_lib
from django.utils.dateformat import format


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

        self.test_faction = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        self.test_user.userprofile.faction = self.test_faction
        self.test_user.userprofile.save()

        self.test_enklave = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=22.22,
            longitude=-22.22,
            shield=10
        )

    def test_start_enklave_combat_validation(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {
        }

        response = client.post('/combat/start/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "u'enklave_id' is a required property")

    def test_start_enklave_combat_bad_input(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {
            'enklave_id': 'abc'
        }

        response = client.post('/combat/start/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "u'abc' is not of type u'integer'")

    def test_start_enklave_combat_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        data = {
            'enklave_id': 1
        }

        response = client.post('/combat/start/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['detail'], "Enklave not found: 1")

    def test_start_enklave_combat_no_user_location(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        faction1 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=22.22,
            longitude=-22.22,
            shield=10,
            faction=faction1
        )

        data = {
            'enklave_id': test_enklave2.id
        }

        response = client.post('/combat/start/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "User has not sent location data")

    def test_start_enklave_combat_not_in_range(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        faction1 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=22.22,
            longitude=-22.22,
            shield=10,
            faction=faction1
        )

        data = {
            'enklave_id': test_enklave2.id
        }

        response = client.post('/combat/start/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "User is not close enough to the selected enklave")

    def test_start_enklave_combat_enklave_ok(self):

        faction1 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        faction2 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10,
            faction=faction1
        )

        test_user2 = User.objects.create_user("test_user2", "test_user2@user.com", "123456")

        tok = AccessToken.objects.create(
            user=test_user2, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        UserLocation.objects.create(
            user=test_user2,
            latitude=44,
            longitude=33
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        test_user2.userprofile.faction = faction2
        test_user2.userprofile.save()

        data = {
            'enklave_id': test_enklave2.id
        }

        self.assertEqual(EnklaveCombat.objects.filter(enklave=test_enklave2).count(), 0)
        response = client.post('/combat/start/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(EnklaveCombat.objects.filter(enklave=test_enklave2).count(), 1)

    def test_start_enklave_combat_enklave_already_exists_join(self):

        faction1 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        faction2 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10,
            faction=faction1
        )

        test_user2 = User.objects.create_user("test_user2", "test_user2@user.com", "123456")

        tok = AccessToken.objects.create(
            user=test_user2, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        UserLocation.objects.create(
            user=test_user2,
            latitude=44,
            longitude=33
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        test_user2.userprofile.faction = faction2
        test_user2.userprofile.save()

        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=test_enklave2
        )

        data = {
            'enklave_id': test_enklave2.id
        }

        self.assertEqual(EnklaveCombatUser.objects.filter(user=test_user2, enklave_combat=enklave_combat).count(), 0)
        response = client.post('/combat/start/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(EnklaveCombatUser.objects.filter(user=test_user2, enklave_combat=enklave_combat).count(), 1)

    def test_start_enklave_combat_user_already_in_combat(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        faction1 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10,
            faction=faction1
        )

        test_enklave3 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10
        )

        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=test_enklave3
        )

        EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=self.test_user,
            type=2
        )

        data = {
            'enklave_id': test_enklave2.id
        }

        response = client.post('/combat/start/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "user already in combat")

    def test_start_enklave_combat_good(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10
        )

        data = {
            'enklave_id': test_enklave2.id
        }

        enklave_combat = EnklaveCombat.objects.filter(enklave=test_enklave2, started_by=self.test_user).first()
        self.assertEqual(enklave_combat, None)
        response = client.post('/combat/start/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)


    def test_get_enklave_combat_status_validator(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        response = client.get('/combat/status/enklave/')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is required: enklave_id')

    def test_get_enklave_combat_status_bad_input(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        response = client.get('/combat/status/enklave/?enklave_id=aaa')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is not a valid integer: enklave_id')

    def test_get_enklave_combat_status_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        response = client.get('/combat/status/enklave/?enklave_id=1')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['detail'], 'Enklave not found: 1')

    def test_get_enklave_combat_status_not_under_attack(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10
        )

        response = client.get('/combat/status/enklave/?enklave_id={0}'.format(test_enklave2.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['detail'], 'enklave is currently not under attack')

    def test_get_enklave_combat_status_good(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10
        )

        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=test_enklave2
        )

        combat_user = EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=self.test_user,
            type=2
        )

        response = client.get('/combat/status/enklave/?enklave_id={0}'.format(test_enklave2.id))
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['combatants'][0]['user_id'], self.test_user.id)
        self.assertEqual(response.data['combatants'][0]['combatant_id'], combat_user.id)
        self.assertEqual(response.data['combatants'][0]['type_name'], 'Attacker')
        self.assertEqual(response.data['enklave_combat']['enklave_combat_id'], enklave_combat.id)
        self.assertEqual(response.data['enklave_combat']['started_at'], enklave_combat.started_at)

    def test_get_enklave_combat_ended(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10
        )

        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=test_enklave2,
            ended_at=timezone.now()
        )

        EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=self.test_user,
            type=2
        )

        response = client.get('/combat/status/enklave/?enklave_id={0}'.format(test_enklave2.id))
        self.assertEqual(response.data['detail'], 'enklave is currently not under attack')

    def test_start_raider_attack_validator(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {

        }

        response = client.post('/combat/start/raider/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "u'raider_id' is a required property")

    def test_start_raider_attack_bad_input(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {
            'raider_id': 1
        }

        response = client.post('/combat/start/raider/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "1 is not of type u'string'")

    def test_start_raider_attack_bad_input2(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {
            'raider_id': "abc"
        }

        response = client.post('/combat/start/raider/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "Fiend is not a correctly formatted uuid: raider_id")

    def test_start_raider_attack_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        data = {
            'raider_id': '89eb35868a8247a4a911758a62601cf7'
        }

        response = client.post('/combat/start/raider/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['detail'], "Raider not found")

    def test_start_raider_attack_raider_no_location_sent(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider = Raider2.objects.create(latitude=1, longitude=1, enklave=enklave)

        raider_combat = RaiderCombat.objects.create()
        RaiderCombatRaider.objects.create(
            raider=raider,
            raider_combat=raider_combat
        )

        data = {
            'raider_id': str(raider.id)
        }

        response = client.post('/combat/start/raider/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "User has not sent location data")

    def test_start_raider_attack_raider_raider_no_position(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider = Raider2.objects.create(latitude=1, longitude=1, enklave=enklave)

        raider_combat = RaiderCombat.objects.create()
        RaiderCombatRaider.objects.create(
            raider=raider,
            raider_combat=raider_combat
        )

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        data = {
            'raider_id': str(raider.id)
        }

        response = client.post('/combat/start/raider/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "Raider does not have a current location")

    def test_start_raider_attack_raider_not_in_range(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider = Raider2.objects.create(latitude=1, longitude=1, enklave=enklave)
        RaiderPosition.objects.create(
            raider=raider,
            latitude=22,
            longitude=22,
            starts_at=timezone.now() - timedelta(minutes=5),
            ends_at=timezone.now() + timedelta(minutes=5)
        )

        raider_combat = RaiderCombat.objects.create()
        RaiderCombatRaider.objects.create(
            raider=raider,
            raider_combat=raider_combat,
        )

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        data = {
            'raider_id': str(raider.id)
        }

        response = client.post('/combat/start/raider/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "User is not close enough to the selected raider")

    def test_start_raider_attack_raider_raider_in_combat(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider = Raider2.objects.create(latitude=1, longitude=1, enklave=enklave)
        RaiderPosition.objects.create(
            raider=raider,
            latitude=44,
            longitude=33,
            starts_at=timezone.now() - timedelta(minutes=5),
            ends_at=timezone.now() + timedelta(minutes=5)
        )

        raider_combat = RaiderCombat.objects.create()
        RaiderCombatRaider.objects.create(
            raider=raider,
            raider_combat=raider_combat,
        )

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        data = {
            'raider_id': str(raider.id)
        }

        response = client.post('/combat/start/raider/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], "Raider is already in combat")

    def test_start_raider_attack_raider_good(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider = Raider2.objects.create(latitude=1, longitude=1, enklave=enklave)
        RaiderPosition.objects.create(
            raider=raider,
            latitude=44,
            longitude=33,
            starts_at=timezone.now() - timedelta(minutes=5),
            ends_at=timezone.now() + timedelta(minutes=5)
        )

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        data = {
            'raider_id': str(raider.id)
        }

        response = client.post('/combat/start/raider/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(response.data['combatants'][0]['user']['id'], self.test_user.id)
        self.assertEqual(response.data['combatants'][0]['type'], 'user')
        self.assertEqual(response.data['combatants'][0]['date_left'], None)
        self.assertTrue('raider_combatant_id' in response.data['combatants'][0])
        self.assertEqual(response.data['combatants'][1]['raider']['id'], raider.id)

        self.assertEqual(RaiderCombat.objects.count(), 1)
        self.assertEqual(RaiderCombatRaider.objects.filter(raider=raider).count(), 1)
        self.assertEqual(RaiderCombatUser.objects.filter(user=self.test_user).count(), 1)

        redis_lib = RedisLib()
        redis_lib.remove_combat_sequence(RaiderCombat.objects.first())

    def test_update_combat_validation(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/combat/update/raider/')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is required: raider_combat_id')

    def test_update_combat_bad_input(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/combat/update/raider/?raider_combat_id=abc')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['detail'], 'Field is not a valid integer: raider_combat_id')

    def test_update_combat_for_time_not_found(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
        response = client.get('/combat/update/raider/?raider_combat_id=1')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['detail'], 'RaiderCombat not found: 1')

    def test_update_combat_for_time(self):
        tok = AccessToken.objects.create(
            user=self.test_user, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        raider = Raider2.objects.create(latitude=1, longitude=1, enklave=enklave)
        RaiderPosition.objects.create(
            raider=raider,
            latitude=44,
            longitude=33,
            starts_at=timezone.now() - timedelta(minutes=5),
            ends_at=timezone.now() + timedelta(minutes=5)
        )

        self.test_user.userprofile.energy = 100
        self.test_user.userprofile.save()

        UserLocation.objects.create(user=self.test_user, latitude=44, longitude=33)
        raider_combat = RaiderCombat.objects.create(started_by=self.test_user)

        raider_combat_user = RaiderCombatUser.objects.create(user=self.test_user, raider_combat=raider_combat)
        raider_combat_raider = RaiderCombatRaider.objects.create(raider=raider, raider_combat=raider_combat)

        response = client.get('/combat/update/raider/?raider_combat_id={0}'.format(raider_combat.id))
        # self.assertEqual(response.data['target']['user_combatant_id'], raider_combat_user.id)
        # self.assertEqual(response.data['attacker']['raider_combatant_id'], raider_combat_raider.id)
        # self.assertTrue('starts_at' in response.data)
        # self.assertTrue('ends_at' in response.data)

    # def test_start_raider_attack_user_in_combat(self):
    #     tok = AccessToken.objects.create(
    #         user=self.test_user, token='1234567890',
    #         application=self.application, scope='read write',
    #         expires=timezone.now() + timedelta(days=1)
    #     )
    #
    #     client = APIClient()
    #     client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
    #
    #     enklave = Enklave.objects.create(latitude=0, longitude=0)
    #     raider = Raider2.objects.create(latitude=1, longitude=1, enklave=enklave)
    #
    #     RaiderCombat.objects.create(raider=raider)
    #
    #     data = {
    #         'raider_id': '89eb35868a8247a4a911758a62601cf7'
    #     }
    #
    #     response = client.post('/combat/update/raider/', data=json.dumps(data), content_type='application/json')
    #     print response
    #     self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        # self.assertEqual(response.data['detail'], "Raider not found")

    # def test_start_combat(self):
    #     tok = AccessToken.objects.create(
    #         user=self.test_user, token='1234567890',
    #         application=self.application, scope='read write',
    #         expires=timezone.now() + timedelta(days=1)
    #     )
    #
    #     client = APIClient()
    #     client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
    #
    #     data = {
    #         'enklave_id': self.test_enklave.id
    #     }
    #
    #     response = client.post('/enklave/attack/', data=json.dumps(data), content_type='application/json')
    #     self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    #     user_location = UserLocation.objects.create(
    #         user=self.test_user,
    #         latitude=44,
    #         longitude=33
    #     )
    #     response = client.post('/enklave/attack/', data=json.dumps(data), content_type='application/json')
    #     self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    #     user_location.latitude = self.test_enklave.latitude
    #     user_location.longitude = self.test_enklave.longitude
    #     user_location.save()
    #
    #     response = client.post('/enklave/attack/', data=json.dumps(data), content_type='application/json')
    #     self.assertEqual(response.status_code, status.HTTP_201_CREATED)
    #     enklave_combat = EnklaveCombat.objects.filter(enklave=self.test_enklave).first()
    #     self.assertEqual(enklave_combat.started_by, self.test_user)
    #     self.assertEqual(enklave_combat.ended_at, None)
    #
    #     response = client.get('/enklave/status/?enklave_id={0}'.format(self.test_enklave.id))
    #     self.assertEqual(response.status_code, status.HTTP_200_OK)
    #     self.assertTrue('combatants' in response.data)
    #     self.assertTrue(response.data['combatants'][0]['combatant_user_id'], self.test_user.id)
    #     self.assertTrue(response.data['enklave_combat']['enklave_combat_id'], enklave_combat.id)
    #
    #     response = client.post('/enklave/hit/', data=json.dumps(data), content_type='application/json')
    #     self.assertEqual(response.status_code, status.HTTP_200_OK)
    #     enklave = Enklave.objects.get(pk=self.test_enklave.id)
    #     self.assertEqual(enklave.shield, 9)
    #
    #     test_user2 = User.objects.create_user("test_user2", "test2@user.com", "123456")
    #
    #     tok2 = AccessToken.objects.create(
    #         user=test_user2, token='1234567891',
    #         application=self.application, scope='read write',
    #         expires=timezone.now() + timedelta(days=1)
    #     )
    #
    #     client2 = APIClient()
    #     client2.credentials(HTTP_AUTHORIZATION='Bearer ' + tok2.token)
    #
    #     data = {
    #         'enklave_id': enklave_combat.enklave.id,
    #         'type': 2
    #     }
    #
    #     response = client2.post('/enklave/join/', data=json.dumps(data), content_type='application/json')
    #     self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    #
    #     user_location2 = UserLocation.objects.create(
    #         user=test_user2,
    #         latitude=44,
    #         longitude=33
    #     )
    #     response = client2.post('/enklave/join/', data=json.dumps(data), content_type='application/json')
    #     self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
    #
    #     user_location2.latitude = self.test_enklave.latitude
    #     user_location2.longitude = self.test_enklave.longitude
    #     user_location2.save()
    #
    #     response = client2.post('/enklave/join/', data=json.dumps(data), content_type='application/json')
    #     self.assertEqual(response.status_code, status.HTTP_201_CREATED)
    #     enklave_combat_user = EnklaveCombatUser.objects.filter(
    #         user=test_user2,
    #         enklave_combat=enklave_combat,
    #     ).first()
    #     self.assertNotEqual(enklave_combat_user, None)
    #     self.assertEqual(enklave_combat_user.date_left, None)
    #
    #     print response.data

    # def test_2_enklave_combat_status(self):
    #
    #     tok = AccessToken.objects.create(
    #         user=self.test_user, token='1234567890',
    #         application=self.application, scope='read write',
    #         expires=timezone.now() + timedelta(days=1)
    #     )
    #
    #     client = APIClient()
    #     client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)
    #
    #     response = client.get('/enklave/status/?enklave_id={0}'.format(self.test_enklave.id))
    #     self.assertEqual(response.status_code, status.HTTP_200_OK)
    #     print response.data

    def test_subscribe_enklave_combat(self):
        test_user4 = User.objects.create_user("test_user4", "test_user4@user.com", "123456")
        tok = AccessToken.objects.create(
            user=test_user4, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=enklave
        )

        data = {
            'enklave_combat_idd': enklave_combat.id
        }

        response = client.post('/combat/subscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_combat_id': 'abc'
        }

        response = client.post('/combat/subscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_id': 12321313
        }

        response = client.post('/combat/subscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

        data = {
            'enklave_id': enklave_combat.enklave.id
        }

        self.assertEqual(EnklaveSubscriber.objects.filter(
            user=test_user4,
            enklave=enklave_combat.enklave).count(), 0)

        response = client.post('/combat/subscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(EnklaveSubscriber.objects.filter(
            user=test_user4,
            enklave=enklave_combat.enklave).count(), 1)

        response = client.post('/combat/subscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(EnklaveSubscriber.objects.filter(
            user=test_user4,
            enklave=enklave_combat.enklave).count(), 1)

    def test_unsubscribe_enklave_combat(self):
        test_user4 = User.objects.create_user("test_user4", "test_user4@user.com", "123456")
        tok = AccessToken.objects.create(
            user=test_user4, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        enklave = Enklave.objects.create(latitude=0, longitude=0)
        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=enklave
        )

        EnklaveSubscriber.objects.create(
            user=test_user4,
            enklave=enklave_combat.enklave
        )

        data = {
            'enklave_idd': enklave_combat.id
        }

        response = client.post('/combat/unsubscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_id': 'abc'
        }

        response = client.post('/combat/unsubscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {
            'enklave_id': 12321313
        }

        response = client.post('/combat/unsubscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

        data = {
            'enklave_id': enklave_combat.enklave.id
        }

        self.assertEqual(EnklaveSubscriber.objects.filter(
            user=test_user4,
            enklave=enklave_combat.enklave).count(), 1)

        response = client.post('/combat/unsubscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)
        self.assertEqual(EnklaveSubscriber.objects.filter(
            user=test_user4,
            enklave=enklave_combat.enklave).count(), 0)

        response = client.post('/combat/unsubscribe/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

    def test_start_enklave_combat_attack_user(self):
        test_user4 = User.objects.create_user("test_user4", "test_user4@user.com", "123456")
        test_user5 = User.objects.create_user("test_user5", "test_user5@user.com", "123456")

        tok = AccessToken.objects.create(
            user=test_user4, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10
        )

        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=test_enklave2
        )

        enklave_combatant_id = EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=test_user4,
            type=2
        )

        enklave_combatant_id2 = EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=test_user5,
            type=1
        )

        test_user4.userprofile.energy = 50
        test_user4.userprofile.save()

        test_user5.userprofile.energy = 60
        test_user5.userprofile.save()

        data = {
            'enklave_combatant_id': enklave_combatant_id2.id
        }

        response = client.post('/combat/hit/user/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['enklave_combat_status'], None)

        enklave_combatant_id.last_hit_at = timezone.now() - timedelta(seconds=6)
        enklave_combatant_id.save()

        response = client.post('/combat/hit/user/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['enklave_combat_status'],
                         "No more defenders but enklave still has shields or bricks")

        # test_enklave2.shield = 0
        # test_enklave2.save()
        #
        # response = client.post('/combat/hit/user/', data=json.dumps(data), content_type='application/json')
        # print response
        # self.assertEqual(response.status_code, status.HTTP_200_OK)
        # self.assertEqual(response.data['enklave_combat_status'],
        #                  "No more defenders but enklave still has shields or bricks")

    def test_start_enklave_combat_attack_user_enklave_down(self):
        test_user4 = User.objects.create_user("test_user4", "test_user4@user.com", "123456")
        test_user5 = User.objects.create_user("test_user5", "test_user5@user.com", "123456")

        tok = AccessToken.objects.create(
            user=test_user4, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        faction1 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        faction2 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        test_user4.userprofile.faction = faction2
        test_user4.userprofile.save()

        test_user5.userprofile.faction = faction1
        test_user5.userprofile.save()

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10,
            faction=faction1
        )

        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=test_enklave2
        )

        enklave_combatant_id = EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=test_user4,
            type=2
        )

        enklave_combatant_id2 = EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=test_user5,
            type=1
        )

        test_user4.userprofile.energy = 50
        test_user4.userprofile.save()

        test_user5.userprofile.energy = 60
        test_user5.userprofile.save()

        test_enklave2.shield = 0
        test_enklave2.save()

        data = {
            'enklave_combatant_id': enklave_combatant_id2.id
        }

        response = client.post('/combat/hit/user/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['enklave_combat_status'], None)

        enklave_combatant_id.last_hit_at = timezone.now() - timedelta(seconds=6)
        enklave_combatant_id.save()

        response = client.post('/combat/hit/user/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        # self.assertEqual(response.data['enklave_combat_status'],
        #                  "Defenders won")

        # response = client.post('/combat/hit/user/', data=json.dumps(data), content_type='application/json')
        # print response
        # self.assertEqual(response.status_code, status.HTTP_200_OK)
        # self.assertEqual(response.data['enklave_combat_status'],
        #                  "No more defenders but enklave still has shields or bricks")

    def test_start_enklave_combat_attack_enklave(self):
        test_user4 = User.objects.create_user("test_user4", "test_user4@user.com", "123456")
        test_user5 = User.objects.create_user("test_user5", "test_user5@user.com", "123456")

        tok = AccessToken.objects.create(
            user=test_user4, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        UserLocation.objects.create(
            user=self.test_user,
            latitude=44,
            longitude=33
        )

        faction1 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        faction2 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        test_user4.userprofile.faction = faction2
        test_user4.userprofile.save()

        test_user5.userprofile.faction = faction1
        test_user5.userprofile.save()

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10,
            faction=faction1
        )

        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=test_enklave2
        )

        enklave_combatant_id = EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=test_user4,
            type=2
        )

        enklave_combatant_id2 = EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=test_user5,
            type=1
        )

        test_user4.userprofile.energy = 50
        test_user4.userprofile.save()

        test_user5.userprofile.energy = 60
        test_user5.userprofile.save()

        test_enklave2.shield = 0
        test_enklave2.save()

        data = {
            'enklave_combat_id': enklave_combat.id
        }

        response = client.post('/combat/hit/enklave/', data=json.dumps(data), content_type='application/json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['enklave_combat_status'],
                         'enklave has no more bricks and shields but it still has defenders')

        enklave_combatant_id2.date_left = timezone.now()
        enklave_combatant_id2.save()

        # response = client.post('/combat/hit/enklave/', data=json.dumps(data), content_type='application/json')
        # print response
        # self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_get_combat_user_status(self):
        test_user4 = User.objects.create_user("test_user4", "test_user4@user.com", "123456")

        tok = AccessToken.objects.create(
            user=test_user4, token='1234567890',
            application=self.application, scope='read write',
            expires=timezone.now() + timedelta(days=1)
        )

        client = APIClient()
        client.credentials(HTTP_AUTHORIZATION='Bearer ' + tok.token)

        response = client.get('/combat/user/status/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data, "user is not in combat")

        faction1 = Faction.objects.create(
            name='test',
            description='test desc',
            color='black',
            logo='abc.jpg',
            display_order=1
        )

        test_enklave2 = Enklave.objects.create(
            name="test_enklave",
            description="test description",
            latitude=44,
            longitude=33,
            shield=10,
            faction=faction1
        )

        enklave_combat = EnklaveCombat.objects.create(
            started_by=self.test_user,
            enklave=test_enklave2
        )

        EnklaveCombatUser.objects.create(
            enklave_combat=enklave_combat,
            user=test_user4,
            type=1
        )

        response = client.get('/combat/user/status/')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertNotEqual(response.data, "user is not in combat")