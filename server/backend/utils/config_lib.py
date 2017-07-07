__author__ = 'radu'
from backend.models import AppConfig
# TODO default values in case of mis-configured app
import json


def defaults(code):
    # this is scrap not energy
    if code == 'CONVERSION_RATE_BRICK':
        return 20

    if code == 'BRICK_ENERGY_COST':
        return 20

    # this is scrap not energy
    if code == 'PLAYER_ENERGY_CELL_USAGE_COST':
        return 25

    # e.g. if a cell has 1500 energy than it will cost 1500 + 750 energy to build one
    if code == 'CELL_ENERGY_PERCENTAGE_COST':
        return 50

    if code == 'CRAFTING_TIME_BRICK':
        return 10

    if code == 'PLAYER_ENERGY_PROGRESSION':
        return [2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000, 8500, 9000, 9500, 10000,
                10500, 11000, 11500, 12000]

    if code == 'PLAYER_RECHARGE_PROGRESSION':
        return [20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10]

    if code == 'PLAYER_DAMAGE_PROGRESSION':
        return [50, 55, 57, 59, 61, 63, 65, 67, 69, 71, 73, 75, 77, 79, 81, 83, 85, 87, 89, 91]

    if code == 'PLAYER_SHOT_COST_PROGRESSION':
        return [20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 52, 54, 56, 58]

    if code == 'PLAYER_XP_PROGRESSION':
        return [3000, 6000, 12000, 24000, 48000, 96000, 192000, 384000, 768000, 1536000, 3072000, 6144000,
                12288000, 24576000, 49152000, 98304000, 196608000, 393216000, 786432000]

    if code == 'PLAYER_CELL_RELOAD_TIME':
        return 12

    if code == 'ENERGY_CELL_RECHARGE_VALUE':
        return 1500

    if code == 'CRAFTING_TIME_CELL':
        return 15

    if code == 'CONVERSION_RATE_CELL':
        return 30

    if code == 'MASS_CELL':
        return 5

    if code == 'CRAFT_TURRET_ENERGY_COST':
        return 200

    if code == 'CRAFT_TURRET_SCRAP_COST':
        return 300

    if code == 'CRAFT_TURRET_TIME_COST':
        return 20

    if code == 'CRAFT_BRICK_ENERGY_COST':
        return 20

    if code == 'CRAFT_BRICK_SCRAP_COST':
        return 20

    if code == 'CRAFT_BRICK_TIME_COST':
        return 10

    if code == 'CRAFT_BRICK_XP_REWARD':
        return 20

    # max turrets per user that are not installed on an enklave
    if code == 'MAX_TURRETS_PER_USER':
        return 20

    if code == 'CRAFT_SHIELD_ENERGY_COST':
        return 200

    if code == 'CRAFT_SHIELD_SCRAP_COST':
        return 300

    if code == 'CRAFT_SHIELD_TIME_COST':
        return 20

    # max turrets per user that are not installed on an enklave
    if code == 'MAX_SHIELDS_PER_USER':
        return 20

    if code == 'INSTALL_TURRET_TIME_COST':
        return 20

    if code == 'INSTALL_TURRET_ENERGY_COST':
        return 100

    if code == 'INSTALL_SHIELD_TIME_COST':
        return 20

    if code == 'INSTALL_SHIELD_ENERGY_COST':
        return 100

    if code == 'PLACE_BRICK_TIME_COST':
        return 20

    if code == 'PLACE_BRICK_ENERGY_COST':
        return 20

    if code == 'GEN_SCRAP_PER_HOUR':
        return 1


def get_conversion_rate_brick_from_config():
    code = 'CONVERSION_RATE_BRICK'
    value = AppConfig.objects.filter(name=code).first()
    if not value and not value.value:
        # TODO log config error or testing
        return 20

    try:
        value_int = int(value.value)
    except ValueError:
        # TODO log config error bad value
        return 20

    return value_int


def get_craft_brick_configs():
    codes = ['CRAFT_BRICK_SCRAP_COST', 'CRAFT_BRICK_TIME_COST', 'CRAFT_BRICK_ENERGY_COST']
    return get_config_data(codes)


def get_energy_storage_config():
    codes = ['PLAYER_ENERGY_PROGRESSION', 'PLAYER_RECHARGE_PROGRESSION', 'CELL_ENERGY_PERCENTAGE_COST']
    return get_config_data(codes)


def get_energy_config_for_user(user):
    energy_settings = get_energy_storage_config()

    storage_level = energy_settings['PLAYER_ENERGY_PROGRESSION']
    recharge_level = energy_settings['PLAYER_RECHARGE_PROGRESSION']
    if user.userprofile.level <= 20:
        storage_level_value = storage_level[user.userprofile.level-1]
        recharge_level_value = recharge_level[user.userprofile.level-1]
    else:
        storage_level_value = storage_level[19]
        recharge_level_value = recharge_level[19]

    return {
        'PLAYER_ENERGY_PROGRESSION': storage_level_value,
        'PLAYER_RECHARGE_PROGRESSION': recharge_level_value
    }


def get_energy_config_for_user_multiple(user, energy_settings):
    storage_level = energy_settings['PLAYER_ENERGY_PROGRESSION']
    recharge_level = energy_settings['PLAYER_RECHARGE_PROGRESSION']
    if user.userprofile.level <= 20:
        storage_level_value = storage_level[user.userprofile.level-1]
        recharge_level_value = recharge_level[user.userprofile.level-1]
    else:
        storage_level_value = storage_level[19]
        recharge_level_value = recharge_level[19]

    return {
        'PLAYER_ENERGY_PROGRESSION': storage_level_value,
        'PLAYER_RECHARGE_PROGRESSION': recharge_level_value
    }


def get_max_xp_config():
    codes = ['PLAYER_XP_PROGRESSION']
    return get_config_data(codes)


def get_max_xp_for_user(user):
    max_xp_config = get_max_xp_config()

    max_xp_data = max_xp_config['PLAYER_XP_PROGRESSION']
    if user.userprofile.level <= 20:
        max_xp_level_value = max_xp_data[user.userprofile.level-1]
    else:
        max_xp_level_value = max_xp_data[19]

    return {
        'PLAYER_XP_PROGRESSION': max_xp_level_value
    }


def get_cell_configs():
    codes = ['CRAFTING_TIME_CELL', 'CONVERSION_RATE_CELL', 'MASS_CELL', 'ENERGY_CELL_RECHARGE_VALUE',
             'PLAYER_ENERGY_CELL_USAGE_COST', 'PLAYER_CELL_RELOAD_TIME', 'CELL_ENERGY_PERCENTAGE_COST']
    return get_config_data(codes)


def get_config_data(codes):
    configs = AppConfig.objects.filter(name__in=codes)

    config_data = {}
    for config in configs:
        try:
            value_json = json.loads(config.value)
        except Exception:
            value_json = defaults(config.name)

        config_data[config.name] = value_json

    for code in codes:
        if code not in config_data:
            config_data[code] = defaults(code)

    return config_data


def get_craft_turret_configs():
    codes = ['CRAFT_TURRET_SCRAP_COST', 'CRAFT_TURRET_ENERGY_COST', 'CRAFT_TURRET_TIME_COST']
    return get_config_data(codes)


def get_craft_shield_configs():
    codes = ['CRAFT_SHIELD_SCRAP_COST', 'CRAFT_SHIELD_ENERGY_COST', 'CRAFT_SHIELD_TIME_COST']
    return get_config_data(codes)


def get_install_turret_configs():
    codes = ['INSTALL_TURRET_TIME_COST', 'INSTALL_TURRET_ENERGY_COST']
    return get_config_data(codes)


def get_install_shield_configs():
    codes = ['INSTALL_SHIELD_TIME_COST', 'INSTALL_SHIELD_ENERGY_COST']
    return get_config_data(codes)


def get_place_brick_config():
    codes = ['PLACE_BRICK_TIME_COST', 'PLACE_BRICK_ENERGY_COST']
    return get_config_data(codes)


def get_craft_cell_configs():
    cell_configs = get_cell_configs()
    return cell_configs


def get_scrap_generate_config():
    codes = ['GEN_SCRAP_PER_HOUR']
    return get_config_data(codes)


def get_combat_config():
    codes = ['PLAYER_DAMAGE_PROGRESSION', 'PLAYER_SHOT_COST_PROGRESSION']
    return get_config_data(codes)


def get_attack_config_for_user(user):

    combat_settings = get_combat_config()

    damage_level = combat_settings['PLAYER_DAMAGE_PROGRESSION']
    shot_cost_level = combat_settings['PLAYER_SHOT_COST_PROGRESSION']
    if user.userprofile.level <= 20:
        damage_level_value = damage_level[user.userprofile.level-1]
        shot_cost_level_value = shot_cost_level[user.userprofile.level-1]
    else:
        damage_level_value = damage_level[19]
        shot_cost_level_value = shot_cost_level[19]

    return {
        'PLAYER_DAMAGE_PROGRESSION': damage_level_value,
        'PLAYER_SHOT_COST_PROGRESSION': shot_cost_level_value
    }


def get_attack_config_for_user_multiple(user, combat_settings):
    damage_level = combat_settings['PLAYER_DAMAGE_PROGRESSION']
    shot_cost_level = combat_settings['PLAYER_SHOT_COST_PROGRESSION']
    if user.userprofile.level <= 20:
        damage_level_value = damage_level[user.userprofile.level-1]
        shot_cost_level_value = shot_cost_level[user.userprofile.level-1]
    else:
        damage_level_value = damage_level[19]
        shot_cost_level_value = shot_cost_level[19]

    return {
        'PLAYER_DAMAGE_PROGRESSION': damage_level_value,
        'PLAYER_SHOT_COST_PROGRESSION': shot_cost_level_value
    }