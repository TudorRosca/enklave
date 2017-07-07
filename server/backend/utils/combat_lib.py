__author__ = 'radu'
from backend.models import Raider2, RaiderPosition, RaiderCombat, RaiderCombatRaider, RaiderCombatUser
from backend.models import Shield, Turret, Brick, EnklaveCombatUser, Faction
import random
import math
import geopy
from geopy.distance import VincentyDistance
# from math import radians, cos, sin, asin, sqrt
from datetime import timedelta
from django.utils import timezone
from django.utils.dateformat import format
from random import randint
from backend.utils.redis_lib import RedisLib
redis_lib = RedisLib()

ACTION_SHIELD_HIT = 1
ACTION_TURRET_HIT = 2
ACTION_BRICK_HIT = 3
ACTION_ENKLAVE_DESTROYED = 4


radius = 100
num_hours = 12
hits_at_diff = 2


def generate_raider_object(enklave):
    """
    The object is to be used to bulk create all the raiders in the raid management command

    :param enklave:
    :return: Raider2 Type Object
    """

    bearing = 2 * math.pi * random.random()  # angle
    origin = geopy.Point(enklave.latitude, enklave.longitude)
    destination = VincentyDistance(kilometers=radius).destination(origin, bearing)

    lat2, lon2 = destination.latitude, destination.longitude

    raider = Raider2(
        enklave=enklave,
        level=1,
        energy=1,
        latitude=lat2,
        longitude=lon2,
        bearing=bearing,
        hits_at=timezone.now() + timedelta(hours=(num_hours - 1 + hits_at_diff))
    )

    return raider


def generate_raider_position_objects(raider):
    raider_positions = []
    for idx in range(0, num_hours+2):
        distance_moved = float(radius/num_hours)

        if idx == num_hours+1:
            new_radius = .05
        else:
            new_radius = radius - idx * distance_moved
        origin = geopy.Point(raider.enklave.latitude, raider.enklave.longitude)
        destination = VincentyDistance(kilometers=new_radius).destination(origin, raider.bearing)

        if idx == num_hours-1:
            ends_at = raider.hits_at
        else:
            ends_at = raider.created_at + (idx + 1) * timedelta(hours=1)

        raider_positions.append(
            RaiderPosition(
                raider=raider,
                latitude=destination.latitude,
                longitude=destination.longitude,
                starts_at=raider.created_at + idx * timedelta(hours=1),
                ends_at=ends_at
            )
        )

    return raider_positions


def get_enklaves_in_combat():
    enklaves_in_combat = RaiderCombat.objects \
        .filter(ended_at__isnull=True, enklave__isnull=False) \
        .values('enklave_id', 'id')

    return enklaves_in_combat


def get_raider_that_arrived_for_combat():
    raider_hits = Raider2.objects.filter(
        hits_at__lte=timezone.now()
    ).prefetch_related('enklave')

    return raider_hits


def raiders_hit_action():
    raider_hits = get_raider_that_arrived_for_combat()
    for raider_hit in raider_hits:
        raider_combat, created = RaiderCombat.objects.get_or_create(enklave=raider_hit.enklave)
        RaiderCombatRaider.objects.create(raider_combat=raider_combat, raider=raider_hit)
        raider_hit.hits_at = None
        raider_hit.save()


def user_join_raider_combat(user, raider_combat):
    raider_combat_user, created = RaiderCombatUser.objects.get_or_create(user=user, raider_combat=raider_combat)
    return raider_combat_user


def get_raider_combat_raiders(raider_combat):
    raider_combat_raiders = RaiderCombatRaider.objects \
        .filter(raider_combat=raider_combat, combatant_ptr__date_left__isnull=True)

    return raider_combat_raiders


def get_raider_combat_users(raider_combat):
    raider_combat_users = RaiderCombatUser.objects \
        .filter(raider_combat=raider_combat, combatant_ptr__date_left__isnull=True)

    return raider_combat_users


def get_raider_combat_status(raider_combat):
    raider_combat_users = get_raider_combat_users(raider_combat)
    raider_combat_raiders = get_raider_combat_raiders(raider_combat)

    raider_combat_user_list = []
    for raider_combat_user in raider_combat_users:
        raider_combat_user_list.append(raider_combat_user.to_json())

    raider_combat_raider_list = []
    for raider_combat_raider in raider_combat_raiders:
        raider_combat_raider_list.append(raider_combat_raider.to_json())

    return {
        'raiders': raider_combat_raider_list,
        'users': raider_combat_user_list
    }


def user_leaves_raider_combat(user, raider_combat, left_at=None):
    RaiderCombatUser.objects.filter(user=user, raider_combat=raider_combat)\
        .update(date_left=left_at if left_at else timezone.now())


def generate_raider_combat_actions(raider_combat, generated_at=None):
    raider_combat_users = get_raider_combat_users(raider_combat)
    raider_combat_raiders = get_raider_combat_raiders(raider_combat)

    turn_start = generated_at if generated_at else timezone.now() + timedelta(seconds=5)
    turns = 60 / 5  # every 5 seconds

    turn_attack = []
    for turn_idx in range(0, turns):
        for raider_combat_raider in raider_combat_raiders:
            turn_attack.append({
                'starts_at': int(format(turn_start + timedelta(seconds=5 * turn_idx), 'U')),
                'ends_at': int(format(turn_start + timedelta(seconds=5 * (turn_idx + 1)), 'U')),
                'target': select_raider_target(raider_combat_users, raider_combat),
                'attacker': 'raider_cr_{0}'.format(raider_combat_raider.id)
            })

    return turn_attack


def select_raider_target(raider_combat_users, raider_combat):
    if not raider_combat_users:
        #  There are no user players so attack the enklave
        return 'enklave_{0}'.format(raider_combat.enklave.id)

    num_options = len(raider_combat_users) + 1
    selection = randint(1, num_options)
    if selection == num_options:
        if raider_combat.enklave:
            return 'enklave_{0}'.format(raider_combat.enklave.id)
        selection -= 1

    return 'user_cu_{0}'.format(raider_combat_users[selection - 1].id)


def get_action_for_time(action_sequence, gen_time=None):
    gen_time_stamp = format(gen_time if gen_time else timezone.now(), 'U')
    for item in action_sequence:
        if int(item['starts_at']) <= int(gen_time_stamp) < int(item['ends_at']):
            return item

    return None


def store_attack_sequence(attack_sequence, raider_combat):
    redis_lib.store_attack_sequence(raider_combat, attack_sequence)


def get_stored_attack_sequence(raider_combat):
    return redis_lib.get_attack_sequence(raider_combat)


def update_raider_combat_action(raider_combat, gen_time=None):
    action_sequence = redis_lib.get_attack_sequence(raider_combat)
    process_raider_combat_actions(raider_combat, action_sequence, gen_time)


def process_raider_combat_actions(raider_combat, actions_sequence, gen_time=None):
    gen_time_stamp = int(format(gen_time if gen_time else timezone.now(), 'U'))
    if raider_combat.last_processed_at:
        last_processed_at = int(format(raider_combat.last_processed_at, 'U'))
    else:
        last_processed_at = int(format(raider_combat.started_at, 'U'))

    get_raider_combat_users(raider_combat)
    get_raider_combat_raiders(raider_combat)

    shields = list(Shield.objects.filter(enklave=raider_combat.enklave, crafteditem_ptr__energy__gt=0))
    turrets = list(Turret.objects.filter(enklave=raider_combat.enklave, crafteditem_ptr__energy__gt=0))
    bricks = list(Brick.objects.filter(enklave=raider_combat.enklave, crafteditem_ptr__energy__gt=0))

    items = []
    for item in actions_sequence:
        if not(last_processed_at <= int(item['starts_at']) and int(item['ends_at']) < gen_time_stamp):
            continue

        if 'enklave' in item['target']:
            action_type, item = hit_enklave(raider_combat.enklave, shields, turrets, bricks)
            if action_type == ACTION_ENKLAVE_DESTROYED:
                return items
            items.append(item)
            # items.append(hit_enklave(raider_combat.enklave, shields, turrets, bricks))
        elif 'raider_cr' in item['target']:
            print 'hit raider'
        elif 'user_cu' in item['target']:
            print 'hit user'

    for item in items:
        item.save()

    return items


def get_item_from_target(target_txt):
    if 'enklave' in target_txt:
        return {
            'enklave_id': int(target_txt.replace('enklave_', ''))
        }

    if 'user_cu_' in target_txt:
        return {
            'user_combatant_id': int(target_txt.replace('user_cu_', ''))
        }

    if 'raider_cr_' in target_txt:
        return {
            'raider_combatant_id': int(target_txt.replace('raider_cr_', ''))
        }

    return None


def hit_enklave(enklave, shields, turrets, bricks):
    """
    :param enklave:
    :return: the action type and the item
    """
    # shields = list(shields)
    # turrets = list(turrets)
    # bricks = list(bricks)
    nr_shields = len(shields)
    nr_turrets = len(turrets)
    nr_bricks = len(bricks)

    if nr_shields > 0:  # Hit shield if it exists:
        shield = shields[randint(0, nr_shields - 1)]
        shield.energy -= 5  # default energy strike
        if shield.energy <= 0:
            shield.energy = 0
            shield.save()
            nr_shields -= 1
            shields.remove(shield)

        return ACTION_SHIELD_HIT, shield

    if nr_bricks:
        brick = bricks[randint(0, nr_bricks - 1)]
        brick.energy -= 5  # default energy strike
        print 'nrgy', brick.energy
        if brick.energy <= 0:
            brick.energy = 0
            brick.save()
            nr_bricks -= 1
            bricks.remove(brick)
        return ACTION_BRICK_HIT, brick

    if nr_turrets:
        turret = turrets[randint(0, nr_turrets - 1)]
        turret.energy -= 5  # default energy strike
        if turret.energy <= 0:
            turret.energy = 0
            turret.save()
            nr_turrets -= 1
            bricks.remove(turret)
        return ACTION_TURRET_HIT, turret

    # If the enklave doesn't have any extras it will be destroyed
    enklave.destroyed_at = timezone.now()
    enklave.save()
    return ACTION_ENKLAVE_DESTROYED, enklave


def get_user_with_min_energy(raider_combat_users):
    min_raider = raider_combat_users[0]
    for raider_combat_user in raider_combat_users:
        if raider_combat_user.user.userprofile.energy < min_raider.user.userprofile.energy:
            min_raider = raider_combat_user

    return min_raider


def test_if_enklave_combat_is_done(enklave_combat, user):
    enklave_combat.refresh_from_db()
    if enklave_combat.ended_at:
        return "Combat already ended"

    defenders_count = EnklaveCombatUser.objects\
        .filter(enklave_combat=enklave_combat, date_left__isnull=True, type=1).count()

    attackers_count = EnklaveCombatUser.objects \
        .filter(enklave_combat=enklave_combat, date_left__isnull=True, type=2).count()

    shield = Shield.objects.filter(enklave=enklave_combat.enklave, energy__gt=0).first()
    brick = Brick.objects.filter(enklave=enklave_combat.enklave, energy__gt=0).first()

    if defenders_count == 0:
        if attackers_count == 0:
            if not brick:
                enklave_combat.enklave.faction = None
            enklave_combat.enklave.safe_until = timezone.now() + timedelta(minutes=3)
            enklave_combat.enklave.save()

            enklave_combat_status = "Defenders won"
            enklave_combat.ended_at = timezone.now()
            enklave_combat.notes = "Defenders won at {0}".format(timezone.now())
            enklave_combat.save()
            restart_test_enklave(enklave_combat.enklave)

            return enklave_combat_status

        if shield or brick or enklave_combat.enklave.shield > 0:
            enklave_combat_status = "No more defenders but enklave still has shields or bricks"
        else:
            EnklaveCombatUser.objects.filter(
                enklave_combat=enklave_combat,
                date_left__isnull=True).update(
                date_left=timezone.now()
            )

            enklave_combat.enklave.faction = None
            enklave_combat.enklave.safe_until = timezone.now() + timedelta(minutes=3)

            enklave_combat.enklave.save()
            enklave_combat_status = "Attackers won"
            enklave_combat.ended_at = timezone.now()
            enklave_combat.notes = "Attackers won at {0}".format(timezone.now())
            enklave_combat.save()
            restart_test_enklave(enklave_combat.enklave)

        return enklave_combat_status

    if attackers_count == 0:
        EnklaveCombatUser.objects.filter(enklave_combat=enklave_combat, date_left__isnull=True).update(
            date_left=timezone.now())

        enklave_combat.enklave.safe_until = timezone.now() + timedelta(minutes=3)
        enklave_combat.enklave.save()

        enklave_combat_status = "Defenders won"
        enklave_combat.ended_at = timezone.now()
        enklave_combat.notes = "Defenders won at {0}".format(timezone.now())
        enklave_combat.save()
        restart_test_enklave(enklave_combat.enklave)

        return enklave_combat_status


# def get_item_from_combat(raider_list, users_list, raider_combat, redis_item):
#     if 'enklave' in redis_item:
#         id = redis_item.split('_')[1]
#         return red


def restart_test_enklave(enklave):
    if enklave.id != 16066:
        return

    factions = Faction.objects.all()
    enklave.faction = random.choice(factions)
    enklave.save()

    bricks = Brick.objects.filter(enklave=enklave)
    count = 0
    for brick in bricks:
        if count >= 80:
            break

        brick.energy = 350
        brick.save()







