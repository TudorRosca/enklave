__author__ = 'radu'
from backend.utils import config_lib
from rest_framework.exceptions import ValidationError
from django.utils import timezone
from datetime import timedelta
from backend.models import Turret, Shield, UserLocation, Brick, Cell, EnklaveCombatUser
from backend.utils import processing, config_lib

CRAFT_BRICK = 1
CRAFT_CELL = 2
USE_CELL = 3
CRAFT_TURRET = 4
CRAFT_SHIELD = 5
INSTALL_TURRET = 6
INSTALL_SHIELD = 7
PLACE_BRICK = 8


def test_if_performing_action(user):
    if user.usercrafting.current_action_completed_at and user.usercrafting.current_action_completed_at > timezone.now():
        raise ValidationError({"detail": "You are already performing an action"})


def update_user_energy(user):

    if EnklaveCombatUser.objects.filter(user=user, date_left__isnull=True).first():
        return

    energy_config = config_lib.get_energy_config_for_user(user)

    if not user.userprofile.energy_calculated_at:
        seconds_passed = (timezone.now() - user.date_joined).total_seconds()
    else:
        seconds_passed = (timezone.now() - user.userprofile.energy_calculated_at).total_seconds()

    energy_increment = seconds_passed * energy_config['PLAYER_RECHARGE_PROGRESSION']

    total_energy = energy_increment + user.userprofile.energy
    if total_energy <= energy_config['PLAYER_ENERGY_PROGRESSION']:
        user.userprofile.energy = total_energy
    else:
        user.userprofile.energy = energy_config['PLAYER_ENERGY_PROGRESSION']
    user.userprofile.save()


def spend_user_energy(user, energy_cost):
    user.userprofile.energy -= energy_cost
    user.userprofile.energy_calculated_at = timezone.now()
    user.userprofile.save()


def craft_energy_cell(user, cell_size, scrap_cost, energy_cost_pct, time_cost):
    if user.userprofile.scrap < scrap_cost:
        raise ValidationError({"detail": "You do not have enough scrap for this action"})

    if user.userprofile.energy < int(cell_size + (cell_size / 100) * energy_cost_pct):
        raise ValidationError({"detail": "You do not have enough energy for this action"})

    user.usercrafting.current_action_completed_at = \
        timezone.now() + timedelta(seconds=time_cost)
    user.usercrafting.current_action_type = CRAFT_CELL
    user.usercrafting.save()

    user.userprofile.energy -= int(cell_size + (cell_size / 100) * energy_cost_pct)
    user.userprofile.energy_calculated_at = timezone.now()
    user.userprofile.scrap -= scrap_cost
    user.userprofile.save()


def craft_brick(user, scrap_cost, energy_cost, time_cost):
    if user.userprofile.scrap < scrap_cost:
        raise ValidationError({"detail": "You do not have enough scrap for this action"})

    if user.userprofile.energy < energy_cost:
        raise ValidationError({"detail": "You do not have enough energy for this action"})

    user.usercrafting.current_action_completed_at = \
        timezone.now() + timedelta(seconds=time_cost)
    user.usercrafting.current_action_type = CRAFT_BRICK

    user.usercrafting.save()

    user.userprofile.energy -= energy_cost
    user.userprofile.energy_calculated_at = timezone.now()
    user.userprofile.scrap -= scrap_cost
    user.userprofile.save()


def craft_turret(user, scrap_cost, energy_cost, time_cost):
    if user.userprofile.scrap < scrap_cost:
        raise ValidationError({"detail": "You do not have enough scrap for this action"})

    if user.userprofile.energy < energy_cost:
        raise ValidationError({"detail": "You do not have enough energy for this action"})

    user.usercrafting.current_action_completed_at = \
        timezone.now() + timedelta(seconds=time_cost)
    user.usercrafting.current_action_type = CRAFT_TURRET

    user.usercrafting.save()

    user.userprofile.energy -= energy_cost
    user.userprofile.energy_calculated_at = timezone.now()
    user.userprofile.scrap -= scrap_cost
    user.userprofile.save()


def craft_shield(user, scrap_cost, energy_cost, time_cost):
    if user.userprofile.scrap < scrap_cost:
        raise ValidationError({"detail": "You do not have enough scrap for this action"})

    if user.userprofile.energy < energy_cost:
        raise ValidationError({"detail": "You do not have enough energy for this action"})

    user.usercrafting.current_action_completed_at = \
        timezone.now() + timedelta(seconds=time_cost)
    user.usercrafting.current_action_type = CRAFT_SHIELD

    user.usercrafting.save()

    user.userprofile.energy -= energy_cost
    user.userprofile.energy_calculated_at = timezone.now()
    user.userprofile.scrap -= scrap_cost
    user.userprofile.save()


def install_turret_process_cost(user, time_cost, energy_cost):
    if user.userprofile.energy < energy_cost:
        raise ValidationError({"detail": "You do not have enough energy for this action"})

    user.usercrafting.current_action_completed_at = \
        timezone.now() + timedelta(seconds=time_cost)
    user.usercrafting.current_action_type = INSTALL_TURRET
    user.usercrafting.save()

    user.userprofile.energy -= energy_cost
    user.userprofile.energy_calculated_at = timezone.now()
    user.userprofile.save()


def install_shield_process_cost(user, time_cost, energy_cost):
    if user.userprofile.energy < energy_cost:
        raise ValidationError({"detail": "You do not have enough energy for this action"})

    user.usercrafting.current_action_completed_at = \
        timezone.now() + timedelta(seconds=time_cost)
    user.usercrafting.current_action_type = INSTALL_SHIELD
    user.usercrafting.save()

    user.userprofile.energy -= energy_cost
    user.userprofile.energy_calculated_at = timezone.now()
    user.userprofile.save()


def gain_user_energy(user, energy_gain):
    energy_config = config_lib.get_energy_config_for_user(user)

    energy_increment = energy_gain
    total_energy = energy_increment + user.userprofile.energy
    if total_energy <= energy_config['PLAYER_ENERGY_PROGRESSION']:
        user.userprofile.energy = total_energy
    else:
        user.userprofile.energy = energy_config['PLAYER_ENERGY_PROGRESSION']

    user.userprofile.energy_calculated_at = timezone.now()
    user.userprofile.save()


def validate_and_get_user_location(user):
    user_location = UserLocation.objects.filter(user=user).first()
    if not user_location:
        raise ValidationError({"detail": "User has not sent location data"})

    return user_location


# def validate_proximity(enklave, user):
#     user_location = validate_and_get_user_location(user)
#     distance = processing.calc_dist(
#         user_location.longitude,
#         user_location.latitude,
#         enklave.longitude,
#         enklave.latitude)
#
#     # TODO link to config?
#     if distance > 0.05:
#         raise ValidationError({"detail": "User is not close enough to the selected enklave"})


def place_brick_process_cost(user, enklave, time_cost, energy_cost):
    if user.userprofile.energy < energy_cost:
        raise ValidationError({"detail": "You do not have enough energy for this action"})

    user.usercrafting.current_action_completed_at = \
        timezone.now() + timedelta(seconds=time_cost)
    user.usercrafting.current_action_type = PLACE_BRICK
    user.usercrafting.nr_bricks -= 1
    user.usercrafting.current_action_enklave = enklave
    user.usercrafting.save()

    user.userprofile.energy -= energy_cost
    user.userprofile.energy_calculated_at = timezone.now()
    user.userprofile.save()


def update_user_crafting(user):
    if EnklaveCombatUser.objects.filter(user=user, date_left__isnull=True).first():
        return

    if user.usercrafting.current_action_completed_at and \
            user.usercrafting.current_action_completed_at <= timezone.now():

        if user.usercrafting.current_action_type == CRAFT_BRICK:
            Brick.objects.create(user=user)
            award_experience(user, 10)
        elif user.usercrafting.current_action_type == CRAFT_CELL:
            Cell.objects.create(user=user)
            award_experience(user, 10)
        elif user.usercrafting.current_action_type == CRAFT_TURRET:
            # TODO implement turret limit ~ Relation limit
            Turret.objects.create(user=user)
            award_experience(user, 10)
        elif user.usercrafting.current_action_type == CRAFT_SHIELD:
            # TODO implement turret limit ~ Relation limit
            Shield.objects.create(user=user)
            award_experience(user, 10)
        elif user.usercrafting.current_action_type == INSTALL_TURRET:
            award_experience(user, 20)
        elif user.usercrafting.current_action_type == INSTALL_SHIELD:
            award_experience(user, 20)
        elif user.usercrafting.current_action_type == PLACE_BRICK and user.usercrafting.current_action_enklave:
            award_experience(user, 20)

    if user.usercrafting.current_action_completed_at and \
            user.usercrafting.current_action_completed_at <= timezone.now():
        user.usercrafting.current_action_completed_at = None

    user.usercrafting.save()


def calculate_level_by_xp(experience, xp_config):
    level = 1
    for item in xp_config['PLAYER_XP_PROGRESSION']:
        if experience < item:
            return level
        level += 1

    return level


def award_experience(user, experience_points):
    if user.userprofile.experience:
        user.userprofile.experience += experience_points
    else:
        user.userprofile.experience = experience_points
    user.userprofile.save()
    update_user_level(user)


def update_user_level(user):
    xp_config = config_lib.get_max_xp_config()
    level = calculate_level_by_xp(user.userprofile.experience, xp_config)
    if user.userprofile.level != level:
        user.userprofile.level = level
        user.userprofile.save()