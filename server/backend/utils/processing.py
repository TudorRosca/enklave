__author__ = 'radu'
from math import radians, cos, sin, asin, sqrt
from django.utils import timezone
import logging
logger = logging.getLogger('api_exceptions')
from django.utils.dateformat import format
from backend.utils import validators

WALKING_SPEED_MAX = 12
CYCLING_SPEED_MAX = 25
DRIVING_SPEED_MAX = 100

WALKING_SCRAP_DISTANCE = .05
CYCLING_SCRAP_DISTANCE = .5
DRIVING_SCRAP_MAX = 10

AWARD_SCRAP_VALUE = 5


def calc_dist(lon1, lat1, lon2, lat2):
    """
    Calculate the great circle distance between two points
    on the earth (specified in decimal degrees)
    """
    # convert decimal degrees to radians
    lon1, lat1, lon2, lat2 = map(radians, [float(lon1), float(lat1), float(lon2), float(lat2)])
    # haversine formula
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
    c = 2 * asin(sqrt(a))
    km = 6367 * c
    return km


def award_scrap(user):
    if not user.userprofile:
        return False

    scrap = AWARD_SCRAP_VALUE
    if user.userprofile.scrap:
        user.userprofile.scrap += scrap
    else:
        user.userprofile.scrap = scrap

    user.userprofile.save()

    return {
        "added_scrap": scrap,
        "total_user_scrap": user.userprofile.scrap
    }


def process_last_gen_scrap(last_gen_scrap):
    if not last_gen_scrap:
        return None

    timestamp = int(last_gen_scrap.split('_')[0])
    lat = float((last_gen_scrap.split('_')[1]).split(',')[0])
    lon = float((last_gen_scrap.split('_')[1]).split(',')[1])
    return {
        "timestamp": timestamp,
        "lat": lat,
        "lon": lon
    }


def process_scrap_archive(raw_archive):
    if not raw_archive:
        return False

    item_list = []
    for item in raw_archive:
        item_list.append(process_last_gen_scrap(item))

    return item_list


def test_if_already_awarded_in_the_last_half_hour(scrap_archive, latitude, longitude, gen_time):
    for item in scrap_archive:
        if (int(format(gen_time, 'U')) - int(item['timestamp'])) > 30 * 60:
            # Already passed the half hour mark
            continue

        distance = calc_dist(longitude, latitude, item['lon'], item['lat'])
        if distance < 0.05:
            return True

    return False


def test_scrap_condition(last_scrap, latitude, longitude, gen_time):
    if not last_scrap:
        return False

    distance = calc_dist(last_scrap['lon'], last_scrap['lat'], longitude, latitude)

    duration_seconds = int(format(gen_time, 'U')) - int(last_scrap['timestamp'])
    duration_hours = float(float(duration_seconds) / 3600)
    speed = abs(distance / duration_hours)

    if distance > WALKING_SCRAP_DISTANCE and speed < WALKING_SPEED_MAX:
        return True
    elif distance > CYCLING_SCRAP_DISTANCE and speed < CYCLING_SPEED_MAX:
        return True
    elif distance > DRIVING_SCRAP_MAX and speed < DRIVING_SPEED_MAX:
        return True

    return False


def process_scrap_message(message):
    error = validators.validate_coord_scrap(message)
    if error:
        return False

    return message['params']['lat'], message['params']['long']


