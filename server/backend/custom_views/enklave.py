__author__ = 'radu'

from rest_framework.decorators import api_view, parser_classes
from rest_framework.response import Response
from rest_framework import status
from backend.models import Enklave, UserLocation, EnklaveCombatUser, EnklaveCombat, Brick, Cell, Shield
from backend.utils import validators, helpers, processing
from rest_framework.parsers import JSONParser
from rest_framework.exceptions import ValidationError
from backend.utils.redis_lib import RedisLib
from django.utils import timezone
from django_earthdistance.models import EarthDistance, LlToEarth
from math import cos, fabs
from django.db.models.expressions import RawSQL


@api_view(['POST'])
@parser_classes((JSONParser,))
def create_enklave(request):
    """
    Create Enklave
    Headers: Authorization: Bearer {access_token}

    Post Data:
    Request Example:
    application/json
    {
        "latitude": 45.23,
        "longitude": -23.54,
        "name": "home",
        "description":"my home"
    }
    """

    user = request.user
    validators.validate_create_enklave(request)

    enklave = Enklave.objects.create(
        user=user,
        latitude=float(request.data['latitude']),
        longitude=float(request.data['longitude']),
        name=request.data['name'] if 'name' in request.data else None,
        description=request.data['description'] if 'description' in request.data else None,
    )

    return Response(data=enklave.to_json(), status=status.HTTP_201_CREATED)


@api_view(['GET'])
def get_enklaves(request):
    """
    Get Enklaves created by user
    Headers: Authorization: Bearer {access_token}
    Get Parameter:
        page
    Response Example
    [
      {
        "description": "",
        "faction": null,
        "confirmed_at": null,
        "updated_at": "2016-03-23T13:29:54.817Z",
        "created_by": {
          "username": "radu",
          "id": 6
        },
        "deleted_at": null,
        "id": 2,
        "confirmed": false,
        "name": "",
        "created_at": "2016-03-16T14:25:49.350Z",
        "longitude": 35.0001,
        "confirmed_by": null,
        "latitude": 33
      },
      ...
    ]
    """

    user = request.user
    offset, limit = helpers.get_pagination_from_request(request)
    enklaves = Enklave.objects.filter(user=user, deleted_at__isnull=True).order_by("-created_at")[offset:limit]
    enklave_data_list = []

    for enklave in enklaves:
        enklave_data_list.append(enklave.to_json())

    return Response(data=enklave_data_list)


@api_view(['GET'])
def get_enklave_by_id(request):
    """
    Get Enklaves created by user
    Headers: Authorization: Bearer {access_token}
    Get Parameter:
        enklave_id
    Response Example
    {
      "status": "InCombat",
      "confirmed": false,
      "rooms": 0,
      "name": "Adidas",
      "faction": null,
      "bricks": 0,
      "confirmed_at": null,
      "created_at": "2016-03-16T14:17:59.937Z",
      "shield": 0,
      "updated_at": "2016-03-16T14:17:59.937Z",
      "created_by": {
        "username": "radu",
        "id": 6
      },
      "confirmed_by": null,
      "cells": 0,
      "extensions": 0,
      "longitude": -4,
      "latitude": 5,
      "deleted_at": null,
      "total_shield": 1000,
      "id": 1,
      "description": ""
    }
    """

    validators.validate_details_enklave(request)
    enklave = Enklave.items.get_or_404(request.GET.get('enklave_id'))

    enklave_details = enklave.details()
    enklave_details['nr_bricks'] = Brick.objects.filter(enklave=enklave, energy__gt=0).count()
    enklave_details['nr_cells'] = Cell.objects.filter(enklave=enklave, energy__gt=0).count()
    enklave_details['nr_shields'] = Shield.objects.filter(enklave=enklave, energy__gt=0).count()
    shields = Shield.objects.filter(enklave=enklave)
    shield_value = 0
    for shield in shields:
        shield_value += shield.energy

    enklave_details['total_shield'] = shield_value
    enklave_details['shield_full'] = 500
    enklave_details['brick_full'] = 350

    first_brick = Brick.objects.filter(enklave=enklave, energy__gt=0).first()
    enklave_details['brick_last'] = first_brick.energy if first_brick else None

    return Response(enklave_details)


@api_view(['GET'])
def get_nearby_enklaves(request):
    """
    Get nearby Enklaves
    Headers: Authorization: Bearer {access_token}
    Note: lat, long no longer required
    """

    user = request.user
    user_location = UserLocation.objects.filter(user=user).first()
    if not user_location:
        raise ValidationError({"detail": "User has not sent location data"})

    distance_lat = (1 / 110.574) * (1.5 / 2)   # degree to kilometer times 1.5 divided by 2
    distance_lon = fabs((1 / (111.320*cos(user_location.latitude))) * (1.5 / 2))

    lat_range = (user_location.latitude - distance_lat, user_location.latitude + distance_lat)
    lon_range = (user_location.longitude - distance_lon, user_location.longitude + distance_lon)

    enklaves = Enklave.objects\
        .filter(latitude__range=lat_range, longitude__range=lon_range)\
        .annotate(
            distance=EarthDistance([
                LlToEarth([user_location.latitude, user_location.longitude]),
                LlToEarth(['latitude', 'longitude'])
            ])) \
        .filter(deleted_at__isnull=True, distance__lte=1200)\
        .prefetch_related('user', 'confirmed_by', 'faction')\
        .annotate(nr_bricks=RawSQL("""
        select count(*) from backend_brick b
            inner join backend_crafteditem c on c.id = b.crafteditem_ptr_id
                Where c.enklave_id = backend_enklave.id""", ()))
    enklave_data_list = []

    for enklave in enklaves:
        enklave_data = enklave.to_json()
        enklave_data['nr_bricks'] = enklave.nr_bricks
        enklave_data_list.append(enklave_data)

    return Response(data=enklave_data_list)


@api_view(['POST'])
@parser_classes((JSONParser,))
def hit_enklave(request):
    """
    Hit Enklave (Demo)
    Headers: Authorization: Bearer {access_token}

    Post Data:
    Request Example:
    application/json
    {
        "enklave_id": 2
    }
    """

    user = request.user
    user_location = verify_and_get_user_location(user)
    validators.validate_attack_enklave(request)
    enklave = Enklave.items.get_or_404(request.data['enklave_id'])
    verify_distance(user_location, enklave)
    enklave_combat_user = verify_enklave_combat_user(enklave, user)

    if enklave_combat_user.last_hit_at and (timezone.now() - enklave_combat_user.last_hit_at).total_seconds() < 5:
        raise ValidationError({"detail": "You need to wait 5 seconds from the last attack"})

    enklave_combat_user.last_hit_at = timezone.now()
    enklave_combat_user.save()

    enklave.shield -= 1
    if enklave.shield < 0:
        enklave.shield = 0

    if enklave.shield == 0:
        verify_enklave_status(enklave_combat_user.enklave_combat)

    enklave.save()

    return Response()


@api_view(['POST'])
@parser_classes((JSONParser,))
def hit_opponent(request):
    """
    Hit Opponent (Demo)
    Headers: Authorization: Bearer {access_token}

    Post Data:
    Request Example:
    application/json
    {
        "opponent_id": 2
    }
    """

    # TODO update energy -- what rules ?!

    user = request.user
    validators.validate_hit_opponent(request)
    enklave_combat_user = EnklaveCombatUser.objects.get_or_404(request.data['opponent_id'])
    if user == enklave_combat_user.user:
        raise ValidationError({"detail": "You cannot attack yourself"})

    user_combat_item = EnklaveCombatUser.objects.filter(
        user=user,
        enklave_combat_id=enklave_combat_user.enklave_combat.id,
        date_left__isnull=True).first()

    if not user_combat_item:
        raise ValidationError({"detail": "User is not in the same combat"})

    if user_combat_item.last_hit_at and (timezone.now() - user_combat_item.last_hit_at).total_seconds() < 5:
        raise ValidationError({"detail": "You need to wait 5 seconds from the last attack"})

    user_combat_item.last_hit_at = timezone.now()
    user_combat_item.save()

    if user_combat_item.type == enklave_combat_user.type:
        raise ValidationError({"detail": "User is on the same side"})

    # TODO update damage
    enklave_combat_user.user.userprofile.energy -= 20
    if enklave_combat_user.user.userprofile.energy < 0:
        enklave_combat_user.user.userprofile.energy = 0

    if enklave_combat_user.user.userprofile.energy == 0:
        enklave_combat_user.date_left = timezone.now()
        if user_combat_item.type == 1:
            verify_combat_status(enklave_combat_user.enklave_combat)
        else:
            verify_enklave_status(enklave_combat_user.enklave_combat)

    enklave_combat_user.user.userprofile.save()

    return Response(enklave_combat_user.user.username)


@api_view(['POST'])
@parser_classes((JSONParser,))
def join_combat(request):
    """
    Join combat
    Headers: Authorization: Bearer {access_token}
    Post Data:
    Request Example:
    application/json
    {
        "enklave_id": 2,
        "type": 1,2 (1 - Defender, 2 - Attacker)
    }
    """
    user = request.user
    user_location = verify_and_get_user_location(user)
    validators.validate_join_combat(request)
    enklave = Enklave.items.get_or_404(request.data['enklave_id'])
    verify_distance(user_location, enklave)
    existing_enklave_combat = verify_enklave_combat_user_for_join(enklave, user)

    EnklaveCombatUser.objects.create(
        enklave_combat=existing_enklave_combat,
        user=user,
        type=request.data['type']
    )

    return Response(status=status.HTTP_201_CREATED)


@api_view(['POST'])
@parser_classes((JSONParser,))
def leave_combat(request):
    """
    Join combat
    Headers: Authorization: Bearer {access_token}
    Post Data:
    Request Example:
    application/json
    {
        "enklave_id": 2,
    }
    """
    user = request.user
    validators.validate_attack_enklave(request)
    enklave = Enklave.items.get_or_404(request.data['enklave_id'])

    enklave_combat = EnklaveCombat.objects.filter(
        enklave=enklave,
        ended_at__isnull=True
    ).first()

    if not enklave_combat:
        raise ValidationError({"detail": "enklave not in combat"})

    enklave_user_combat = EnklaveCombatUser.objects.filter(
        user=user,
        enklave_combat=enklave_combat,
        date_left__isnull=True
    )

    if not enklave_user_combat:
        raise ValidationError({"detail": "user not in combat"})

    enklave_user_combat.date_left = timezone.now()
    enklave_user_combat.save()

    if enklave_user_combat.type == 2:
        verify_combat_status(enklave_combat)
    else:
        verify_enklave_status(enklave_combat)
    # TODO test combat status and notify

    return Response()


def verify_combat_status(enklave_combat):
    combatants = EnklaveCombatUser.objects.filter(enklave_combat=enklave_combat, type=2, date_left__isnull=True).count()
    if combatants == 0:
        enklave_combat.ended_at = timezone.now()
        enklave_combat.notes = "Combat ended because there were no more attackers"
        enklave_combat.save()
        # TODO push message to location | faction


def verify_enklave_status(enklave_combat):
    defenders = EnklaveCombatUser.objects.filter(enklave_combat=enklave_combat, type=1, date_left__isnull=True).count()
    if defenders == 0 and enklave_combat.enklave.shield == 0:
        enklave_combat.ended_at = timezone.now()
        enklave_combat.notes = "Combat ended because there were no more defenders and shield was down to 0"
        enklave_combat.enklave.faction = None
        enklave_combat.enklave.save()
        enklave_combat.save()
        EnklaveCombatUser.objects.filter(enklave_combat=enklave_combat).update(date_left=timezone.now())
        # TODO push message to location | faction


def verify_distance(user_location, enklave):
    distance = processing.calc_dist(
        enklave.longitude,
        enklave.latitude,
        user_location.longitude,
        user_location.latitude
    )

    if distance > 0.05:
        raise ValidationError({"detail": "User not in range"})


def verify_and_get_user_location(user):
    user_location = UserLocation.objects.filter(user=user).first()
    if not user_location:
        raise ValidationError({"detail": "User has not sent location data"})
    return user_location


def verify_enklave_combat(enklave):
    existing_enklave_combat = EnklaveCombat.objects.filter(
        enklave=enklave,
        ended_at__isnull=True
    ).first()

    if existing_enklave_combat:
        raise ValidationError({"detail": "enklave already in combat"})


def verify_user_combat(user):
    existing_user_combat = EnklaveCombatUser.objects.filter(
        user=user,
        date_left__isnull=True
    ).first()

    if existing_user_combat:
        raise ValidationError({"detail": "user already in combat"})


def verify_enklave_combat_user(enklave, user):
    existing_enklave_combat = verify_and_get_enklave_combat(enklave)

    existing_user_combat = EnklaveCombatUser.objects.filter(
        user=user,
        enklave_combat=existing_enklave_combat,
        date_left__isnull=True
    ).first()

    if not existing_user_combat:
        raise ValidationError({"detail": "User not currently in combat with that enklave"})

    return existing_user_combat


def verify_and_get_enklave_combat(enklave):
    existing_enklave_combat = EnklaveCombat.objects.filter(
        enklave=enklave,
        ended_at__isnull=True
    ).first()

    if not existing_enklave_combat:
        raise ValidationError({"detail": "enklave not in combat"})

    return existing_enklave_combat


def verify_enklave_combat_user_for_join(enklave, user):
    existing_enklave_combat = verify_and_get_enklave_combat(enklave)

    existing_user_combat = EnklaveCombatUser.objects.filter(
        user=user,
        enklave_combat=existing_enklave_combat
    ).first()

    if existing_user_combat:
        if existing_user_combat.date_left:
            raise ValidationError({"detail": "User left the combat with that enklave and cannot join again"})
        raise ValidationError({"detail": "User already in combat with that enklave"})

    return existing_enklave_combat



