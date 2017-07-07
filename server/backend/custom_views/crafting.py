__author__ = 'radu'
from rest_framework.decorators import api_view, parser_classes, permission_classes
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import AllowAny
from backend.utils import validators, helpers
from rest_framework.parsers import JSONParser
from rest_framework.exceptions import NotFound, ValidationError
from django.contrib.auth.models import User
from backend.models import UserCrafting
import logging
from django.db.models import Q
logger = logging.getLogger('api_exceptions')
from django.utils import timezone
from datetime import timedelta
from backend.utils import config_lib, crafting_lib, geo_lib
from backend.models import Turret, Shield, Enklave, Brick, Cell

CRAFT_BRICK = 1
CRAFT_CELL = 2
USE_CELL = 3
CRAFT_TURRET = 4


@api_view(['POST'])
@parser_classes((JSONParser,))
def craft_brick(request):
    """
    Craft Brick
    Headers: Authorization: Bearer {access_token}

    No parameters required

    Possible validation errors:
    User does not have enough scrap:
    {
        'detail': 'You do not have enough scrap for this action'
    }
    User is busy with another action
    {
        'detail': 'You are already performing an action'
    }
    TBA: user is in combat

    """

    user = request.user

    crafting_lib.update_user_energy(user)
    config_data = config_lib.get_craft_brick_configs()
    crafting_lib.update_user_crafting(user)

    crafting_lib.test_if_performing_action(user)
    crafting_lib.craft_brick(
        user,
        config_data['CRAFT_BRICK_SCRAP_COST'],
        config_data['CRAFT_BRICK_ENERGY_COST'],
        config_data['CRAFT_BRICK_TIME_COST']
    )

    return Response({
        'duration': config_data['CRAFT_BRICK_TIME_COST'],
        'ready_at': timezone.now() + timedelta(seconds=config_data['CRAFT_BRICK_TIME_COST'])
    })


@api_view(['POST'])
@parser_classes((JSONParser,))
def craft_cell(request):
    """
    Craft cell for user
    Headers: Authorization: Bearer {access_token}

    No parameters required

    Possible validation errors:
    User does not have enough scrap:
    {
        'detail': 'You do not have enough scrap for this action'
    }
    User does not have enough energy:
    {
        'detail': 'You do not have enough energy for this action'
    }
    User is busy with another action
    {
        'detail': 'You are already performing an action'
    }
    TBA: user is in combat
    """

    user = request.user

    crafting_lib.update_user_energy(user)
    crafting_lib.update_user_crafting(user)

    config_data = config_lib.get_craft_cell_configs()
    crafting_lib.test_if_performing_action(user)

    crafting_lib.craft_energy_cell(
        user,
        config_data['ENERGY_CELL_RECHARGE_VALUE'],
        config_data['PLAYER_ENERGY_CELL_USAGE_COST'],
        config_data['CELL_ENERGY_PERCENTAGE_COST'],
        config_data['CRAFTING_TIME_CELL']
    )

    brick_crafting_result = user.usercrafting.to_json()
    brick_crafting_result['CRAFTING_TIME_CELL'] = config_data['CRAFTING_TIME_CELL']

    return Response({
        'duration': config_data['CRAFTING_TIME_CELL'],
        'ready_at': timezone.now() + timedelta(seconds=config_data['CRAFTING_TIME_CELL'])
    })


@api_view(['POST'])
@parser_classes((JSONParser,))
def craft_turret(request):
    """
    Craft Turret
    Headers: Authorization: Bearer {access_token}

    No parameters required

    Possible validation errors:
    User does not have enough scrap:
    {
        'detail': 'You do not have enough scrap for this action'
    }
    User does not have enough energy:
    {
        'detail': 'You do not have enough energy for this action'
    }
    User is busy with another action
    {
        'detail': 'You are already performing an action'
    }
    TBA: user is in combat
    """

    user = request.user

    crafting_lib.update_user_energy(user)
    config_data = config_lib.get_craft_turret_configs()
    crafting_lib.update_user_crafting(user)

    crafting_lib.test_if_performing_action(user)
    crafting_lib.craft_turret(
        user,
        config_data['CRAFT_TURRET_SCRAP_COST'],
        config_data['CRAFT_TURRET_ENERGY_COST'],
        config_data['CRAFT_TURRET_TIME_COST']
    )

    return Response({
        'duration': config_data['CRAFT_TURRET_TIME_COST'],
        'ready_at': timezone.now() + timedelta(seconds=config_data['CRAFT_TURRET_TIME_COST'])
    })


@api_view(['POST'])
@parser_classes((JSONParser,))
def craft_shield(request):
    """
    Craft Shield
    Headers: Authorization: Bearer {access_token}

    No parameters required

    Possible validation errors:
    User does not have enough scrap:
    {
        'detail': 'You do not have enough scrap for this action'
    }
    User does not have enough energy:
    {
        'detail': 'You do not have enough energy for this action'
    }
    User is busy with another action
    {
        'detail': 'You are already performing an action'
    }
    TBA: user is in combat
    """

    user = request.user

    crafting_lib.update_user_energy(user)
    config_data = config_lib.get_craft_shield_configs()
    crafting_lib.update_user_crafting(user)

    crafting_lib.test_if_performing_action(user)
    crafting_lib.craft_shield(
        user,
        config_data['CRAFT_SHIELD_SCRAP_COST'],
        config_data['CRAFT_SHIELD_ENERGY_COST'],
        config_data['CRAFT_SHIELD_TIME_COST']
    )

    return Response({
        'duration': config_data['CRAFT_SHIELD_TIME_COST'],
        'ready_at': timezone.now() + timedelta(seconds=config_data['CRAFT_SHIELD_TIME_COST'])
    })


@api_view(['POST'])
@parser_classes((JSONParser,))
def install_turret(request):
    """
    Install Turret on an Enklave
    Headers: Authorization: Bearer {access_token}

    Request Example:
    {
        'turret_id': 11, (must belong to the user and not belong to any enklave)
        'enklave_id': 103 (must belong to the faction of the user, must be in range of the user)
    }
    """
    user = request.user
    crafting_lib.update_user_energy(user)
    crafting_lib.update_user_crafting(user)

    validators.validate_install_turret(request)

    turret = Turret.objects.get_or_404(request.data['turret_id'])
    enklave = Enklave.items.get_or_404(request.data['enklave_id'])
    if not enklave.faction:
        raise ValidationError({"detail": "Enklave does not belong to any faction"})

    if not user.userprofile.faction:
        raise ValidationError({"detail": "User does not belong to any faction"})

    if not enklave.faction == user.userprofile.faction:
        raise ValidationError({"detail": "Enklave does not belong to user faction"})

    if not turret.user == user:
        raise ValidationError({"detail": "Turret does not belong to user"})

    if turret.enklave is not None:
        raise ValidationError({"detail": "Turret is already assigned to an enklave"})

    geo_lib.validate_proximity(enklave, user)
    config_data = config_lib.get_install_turret_configs()
    crafting_lib.test_if_performing_action(user)
    crafting_lib.install_turret_process_cost(
        user,
        config_data['INSTALL_TURRET_TIME_COST'],
        config_data['INSTALL_TURRET_ENERGY_COST']
    )

    turret.enklave = enklave
    turret.used_at = timezone.now() + timedelta(seconds=config_data['INSTALL_TURRET_TIME_COST'])
    turret.save()

    return Response({
        'duration': config_data['INSTALL_TURRET_TIME_COST'],
        'ready_at': timezone.now() + timedelta(seconds=config_data['INSTALL_TURRET_TIME_COST'])
    })


@api_view(['POST'])
@parser_classes((JSONParser,))
def install_shield(request):
    """
    Install Shield on an Enklave
    Headers: Authorization: Bearer {access_token}

    Request Example:
    {
        'shield_id': 11, (must belong to the user and not belong to any enklave)
        'enklave_id': 103 (must belong to the faction of the user, must be in range of the user)
    }
    """
    user = request.user
    crafting_lib.update_user_energy(user)
    crafting_lib.update_user_crafting(user)

    validators.validate_install_shield(request)

    shield = Shield.objects.get_or_404(request.data['shield_id'])
    enklave = Enklave.items.get_or_404(request.data['enklave_id'])
    if not enklave.faction:
        raise ValidationError({"detail": "Enklave does not belong to any faction"})

    if not user.userprofile.faction:
        raise ValidationError({"detail": "User does not belong to any faction"})

    if not enklave.faction == user.userprofile.faction:
        raise ValidationError({"detail": "Enklave does not belong to user faction"})

    if not shield.user == user:
        raise ValidationError({"detail": "Shield does not belong to user"})

    if shield.enklave is not None:
        raise ValidationError({"detail": "Shield is already assigned to an enklave"})

    geo_lib.validate_proximity(enklave, user)
    config_data = config_lib.get_install_shield_configs()
    crafting_lib.test_if_performing_action(user)
    crafting_lib.install_shield_process_cost(
        user,
        config_data['INSTALL_SHIELD_TIME_COST'],
        config_data['INSTALL_SHIELD_ENERGY_COST']
    )

    shield.enklave = enklave
    shield.used_at = timezone.now() + timedelta(seconds=config_data['INSTALL_SHIELD_TIME_COST'])
    shield.save()

    return Response({
        'duration': config_data['INSTALL_SHIELD_TIME_COST'],
        'ready_at': timezone.now() + timedelta(seconds=config_data['INSTALL_SHIELD_TIME_COST'])
    })


@api_view(['POST'])
@parser_classes((JSONParser,))
def place_brick(request):
    """
    Place Brick on an Enklave
    Headers: Authorization: Bearer {access_token}

    Request Example:
    {
        'brick_id': 11 (optional, if not set a random brick belonging to the use will be selected)
        'enklave_id': 103 (must belong to the faction of the user, must be in range of the user)
    }
    """
    user = request.user
    crafting_lib.update_user_energy(user)
    crafting_lib.update_user_crafting(user)

    validators.validate_place_brick(request)
    if 'brick_id' not in request.data:
        brick = Brick.objects.filter(user=user, enklave__isnull=True).first()
        if not brick:
            raise ValidationError({"detail": "User has no available bricks"})
    else:
        brick = Brick.objects.get_or_404(request.data['brick_id'])

    enklave = Enklave.items.get_or_404(request.data['enklave_id'])
    # if not enklave.faction:
    #     raise ValidationError({"detail": "Enklave does not belong to any faction"})

    if not user.userprofile.faction:
        raise ValidationError({"detail": "User does not belong to any faction"})

    if not enklave.faction == user.userprofile.faction and enklave.faction is not None:
        raise ValidationError({"detail": "Enklave does not belong to user faction"})

    if not brick.user == user:
        raise ValidationError({"detail": "Brick does not belong to user"})

    if brick.enklave is not None:
        raise ValidationError({"detail": "Brick is already assigned to an enklave"})

    geo_lib.validate_proximity(enklave, user)
    config_data = config_lib.get_place_brick_config()
    crafting_lib.test_if_performing_action(user)
    crafting_lib.place_brick_process_cost(
        user,
        enklave,
        config_data['PLACE_BRICK_TIME_COST'],
        config_data['PLACE_BRICK_ENERGY_COST']
    )

    brick.enklave = enklave
    brick.used_at = timezone.now() + timedelta(seconds=config_data['PLACE_BRICK_TIME_COST'])
    brick.save()

    has_occupied = False
    if not enklave.faction and Brick.objects.filter(enklave=enklave, energy__gt=0).count() >= 9:
        enklave.faction = user.userprofile.faction
        enklave.save()
        has_occupied = True

    return Response({
        'duration': config_data['PLACE_BRICK_TIME_COST'],
        'ready_at': timezone.now() + timedelta(seconds=config_data['PLACE_BRICK_TIME_COST']),
        'has_occupied': has_occupied
    })


@api_view(['POST'])
@parser_classes((JSONParser,))
def use_cell(request):
    """
    Use cell for user (convert to energy)
    Headers: Authorization: Bearer {access_token}

    {
        'cell_id': 11
    }

    """

    user = request.user
    crafting_lib.update_user_energy(user)
    crafting_lib.update_user_crafting(user)
    validators.validate_use_cell(request)
    config_data = config_lib.get_craft_cell_configs()

    cell = Cell.objects.get_or_404(request.data['cell_id'])

    if not cell.user == user:
        raise ValidationError({"detail": "Cell does not belong to user"})

    if cell.enklave is not None:
        raise ValidationError({"detail": "Cell is already assigned to an enklave"})

    crafting_lib.test_if_performing_action(user)

    user.usercrafting.current_action_completed_at = \
        timezone.now() + timedelta(seconds=config_data['PLAYER_CELL_RELOAD_TIME'])
    user.usercrafting.current_action_type = USE_CELL
    user.usercrafting.save()

    cell.delete()
    crafting_lib.gain_user_energy(user, config_data['ENERGY_CELL_RECHARGE_VALUE'])

    brick_crafting_result = user.usercrafting.to_json()
    brick_crafting_result['PLAYER_CELL_RELOAD_TIME'] = config_data['PLAYER_CELL_RELOAD_TIME']

    return Response({
        'duration': config_data['PLAYER_CELL_RELOAD_TIME'],
        'ready_at': timezone.now() + timedelta(seconds=config_data['PLAYER_CELL_RELOAD_TIME'])
    })