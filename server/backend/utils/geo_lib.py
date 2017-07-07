__author__ = 'radu'
from rest_framework.exceptions import ValidationError
from backend.models import UserLocation
from backend.utils import processing


def validate_proximity(enklave, user):
    if enklave.id == 16066:
        return

    user_location = validate_and_get_user_location(user)
    distance = processing.calc_dist(
        user_location.longitude,
        user_location.latitude,
        enklave.longitude,
        enklave.latitude)

    # TODO link to config?
    if distance > 0.15:
        raise ValidationError({"detail": "User is not close enough to the selected enklave"})


def validate_proximity_raider(user, raider_latitude, raider_longitude):
    user_location = validate_and_get_user_location(user)
    if not raider_latitude or not raider_longitude:
        raise ValidationError({"detail": "Raider does not have a current location"})

    distance = processing.calc_dist(
        user_location.longitude,
        user_location.latitude,
        raider_longitude,
        raider_latitude)

    # TODO link to config?
    if distance > 0.15:
        raise ValidationError({"detail": "User is not close enough to the selected raider"})


def validate_and_get_user_location(user):
    user_location = UserLocation.objects.filter(user=user).first()
    if not user_location:
        raise ValidationError({"detail": "User has not sent location data"})

    return user_location


def validate_combat_proximity(enklave, user):
    if enklave.id == 16066:
        return

    user_location = validate_and_get_user_location(user)
    distance = processing.calc_dist(
        user_location.longitude,
        user_location.latitude,
        enklave.longitude,
        enklave.latitude)

    # TODO link to config?
    if distance > 0.06:
        raise ValidationError({"detail": "User is not close enough to the selected enklave"})