__author__ = 'radu'

from django.test import TestCase
from django.contrib.auth.models import User
from oauth2_provider.models import Application
from datetime import timedelta
from django.utils import timezone
from backend.utils import processing
from backend.utils.redis_lib import RedisLib
redis_lib = RedisLib()
from django.utils.dateformat import format


class ScrapTest(TestCase):
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

    def test_1_scrap_redis_set_and_get(self):
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1, timezone.now())
        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        self.assertEqual(processed_last_gen['lat'], 0)
        self.assertEqual(processed_last_gen['lon'], 1)
        self.assertEqual(str(processed_last_gen['timestamp']), str(format(timezone.now(), 'U')))

        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        has_scrap = processing.test_scrap_condition(processed_last_gen, 0, 1, timezone.now()+timedelta(minutes=10))
        self.assertEqual(has_scrap, False)

    def test_2_scrap_redis_set_and_get_walking(self):
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1, timezone.now())
        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        self.assertEqual(processed_last_gen['lat'], 0)
        self.assertEqual(processed_last_gen['lon'], 1)
        self.assertEqual(str(processed_last_gen['timestamp']), str(format(timezone.now(), 'U')))

        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        has_scrap = processing.test_scrap_condition(processed_last_gen, 0, 1.0007, timezone.now()+timedelta(minutes=10))
        self.assertEqual(has_scrap, True)

    def test_3_scrap_redis_set_and_get_cycling_not_enough(self):
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1, timezone.now())
        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        self.assertEqual(processed_last_gen['lat'], 0)
        self.assertEqual(processed_last_gen['lon'], 1)
        self.assertEqual(str(processed_last_gen['timestamp']), str(format(timezone.now(), 'U')))

        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        has_scrap = processing.test_scrap_condition(processed_last_gen, 0, 1.0023, timezone.now()+timedelta(minutes=1))
        self.assertEqual(has_scrap, False)

    def test_4_scrap_redis_set_and_get_cycling_ok(self):
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1, timezone.now())
        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        self.assertEqual(processed_last_gen['lat'], 0)
        self.assertEqual(processed_last_gen['lon'], 1)
        self.assertEqual(str(processed_last_gen['timestamp']), str(format(timezone.now(), 'U')))

        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        has_scrap = processing.test_scrap_condition(processed_last_gen, 0, 1.0055, timezone.now()+timedelta(minutes=2))
        self.assertEqual(has_scrap, True)

    def test_5_scrap_redis_set_and_get_driving_not_enough(self):
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1, timezone.now())
        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        self.assertEqual(processed_last_gen['lat'], 0)
        self.assertEqual(processed_last_gen['lon'], 1)
        self.assertEqual(str(processed_last_gen['timestamp']), str(format(timezone.now(), 'U')))

        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        has_scrap = processing.test_scrap_condition(processed_last_gen, 0, 1.0855, timezone.now()+timedelta(minutes=8))
        self.assertEqual(has_scrap, False)

    def test_6_scrap_redis_set_and_get_driving_ok(self):
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1, timezone.now())
        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        self.assertEqual(processed_last_gen['lat'], 0)
        self.assertEqual(processed_last_gen['lon'], 1)
        self.assertEqual(str(processed_last_gen['timestamp']), str(format(timezone.now(), 'U')))

        last_gen = redis_lib.get_last_generated_scrap_data(self.test_user)
        processed_last_gen = processing.process_last_gen_scrap(last_gen)
        has_scrap = processing.test_scrap_condition(processed_last_gen, 0, 1.11, timezone.now()+timedelta(minutes=12))
        self.assertEqual(has_scrap, True)

    def test_7_scrap_redis_set_and_get_archive_test(self):
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1, timezone.now())
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1.02, timezone.now()+timedelta(minutes=2))
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1.04, timezone.now()+timedelta(minutes=3))
        redis_lib.set_last_generated_scrap_data(self.test_user, 0, 1.05, timezone.now()+timedelta(minutes=5))
        raw_archive = redis_lib.get_scrap_archive(self.test_user)
        archive = processing.process_scrap_archive(raw_archive)
        self.assertEqual(processing.test_if_already_awarded_in_the_last_half_hour(
            archive, 0, 1, timezone.now()+timedelta(minutes=7)), True)

        self.assertEqual(processing.test_if_already_awarded_in_the_last_half_hour(
            archive, 0, 10, timezone.now()+timedelta(minutes=7)), False)

        self.assertEqual(processing.test_if_already_awarded_in_the_last_half_hour(
            archive, 0, 1, timezone.now()+timedelta(minutes=37)), False)

    def tearDown(self):
        redis_lib.remove_user_scrap_data(self.test_user)

    def test_processing(self):
        self._test_award_scrap()
        self._test_process_last_gen_scrap_none()
        self._test_process_last_gen_scrap()
        self._test_process_scrap_archive_none()
        self._test_scrap_condition_none()
        self._test_process_scrap_message()
        self._test_process_scrap_validators1()

    def _test_award_scrap(self):
        scrap_before = self.test_user.userprofile.scrap
        res = processing.award_scrap(self.test_user)
        self.assertEqual(res['added_scrap'], processing.AWARD_SCRAP_VALUE)

        self.test_user.userprofile.refresh_from_db()
        self.assertEqual(res['total_user_scrap'], self.test_user.userprofile.scrap)
        self.assertEqual(scrap_before + processing.AWARD_SCRAP_VALUE, self.test_user.userprofile.scrap)

        res = processing.award_scrap(self.test_user)
        self.assertEqual(res['added_scrap'], processing.AWARD_SCRAP_VALUE)

        self.test_user.userprofile.refresh_from_db()
        self.assertEqual(res['total_user_scrap'], self.test_user.userprofile.scrap)
        self.assertEqual(scrap_before + 2 * processing.AWARD_SCRAP_VALUE, self.test_user.userprofile.scrap)

    def _test_process_last_gen_scrap_none(self):
        res = processing.process_last_gen_scrap(None)
        self.assertEqual(res, None)

    def _test_process_last_gen_scrap(self):
        gen_time = '1111111'
        latitude = 1
        longitude = 1.1

        data = "{0}_{1},{2}".format(gen_time, latitude, longitude)
        res = processing.process_last_gen_scrap(data)
        self.assertEqual(res['timestamp'], 1111111)
        self.assertEqual(res['lat'], 1)
        self.assertEqual(res['lon'], 1.1)

    def _test_process_scrap_archive_none(self):
        res = processing.process_scrap_archive(None)
        self.assertEqual(res, False)

    def _test_scrap_condition_none(self):
        res = processing.test_scrap_condition(None, 1, 1, 1111)
        self.assertEqual(res, False)

    def _test_process_scrap_message(self):
        message = {
            'params': {
                'lat': 11,
                'long': 22
            }
        }

        lat, lon = processing.process_scrap_message(message)
        self.assertEqual(lat, 11)
        self.assertEqual(lon, 22)

    def _test_process_scrap_validators1(self):
        message = {
            'params': {
                'lat1': 11,
                'long': 22
            }
        }

        err = processing.process_scrap_message(message)
        self.assertEqual(err, False)







