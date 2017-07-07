__author__ = 'radu'

from rest_framework.decorators import api_view, parser_classes
from rest_framework.response import Response
from rest_framework import status
from backend.models import Enklave, EnklaveCombatUser, EnklaveCombat, Raider2, RaiderCombat, \
    RaiderCombatRaider, RaiderCombatUser, Turret, Shield, Brick, EnklaveSubscriber, Faction, \
    UserLocation
from backend.utils import validators, helpers, processing, geo_lib, combat_lib, config_lib
from rest_framework.parsers import JSONParser
from rest_framework.exceptions import ValidationError
from backend.utils.redis_lib import RedisLib
from django.utils import timezone
from rest_framework.exceptions import NotFound
from django.shortcuts import get_object_or_404
from datetime import timedelta
from django.db.models.signals import post_save, pre_save, post_delete
from django.dispatch import receiver


def verify_enklave_combat(enklave):
    existing_enklave_combat = EnklaveCombat.objects.filter(
        enklave=enklave,
        ended_at__isnull=True
    ).first()

    if existing_enklave_combat:
        raise ValidationError({"detail": "enklave already in combat"})


def verify_raider_combat(raider):
    existing_raider_combat = RaiderCombatRaider.objects.filter(
        raider=raider,
        combatant_ptr__date_left__isnull=True
    ).first()

    if existing_raider_combat:
        raise ValidationError({"detail": "Raider is already in combat"})


def verify_user_combat(user):
    existing_user_combat = EnklaveCombatUser.objects.filter(
        user=user,
        date_left__isnull=True
    ).first()

    if existing_user_combat:
        raise ValidationError({"detail": "user already in combat"})


@api_view(['GET'])
def get_if_in_combat(request):
    """
    Get if user is in combat
    Headers: Authorization: Bearer {access_token}

    Returns null or user combat data
    """

    user = request.user
    combat_user = EnklaveCombatUser.objects.filter(user=user, date_left__isnull=True).first()
    if not combat_user:
        return Response("user is not in combat")

    return Response(combat_user.to_json())


@api_view(['POST'])
@parser_classes((JSONParser,))
def start_enklave_combat(request):
    """
    Start or join enklave combat
    Headers: Authorization: Bearer {access_token}

    Post Data:
    Request Example:
    application/json
    {
        "enklave_id": 2
    }

    Note: user must be in range for this action
    """
    user = request.user
    validators.validate_attack_enklave(request)
    enklave = Enklave.items.get_or_404(request.data['enklave_id'])

    if not enklave.faction:
        raise ValidationError({'detail': 'You cannot attack a faction-less enklave'})

    if not user.userprofile.faction:
        raise ValidationError({'detail': 'Why does this user not have a faction?'})

    if enklave.safe_until and enklave.safe_until > timezone.now():
        raise ValidationError({'detail': 'The enklave is still under protection'})

    geo_lib.validate_combat_proximity(enklave, user)
    verify_user_combat(user)

    enklave_combat = EnklaveCombat.objects.filter(
        enklave=enklave,
        ended_at__isnull=True
    ).first()

    existing_enklave_combat = EnklaveCombatUser.objects.filter(user=user, enklave_combat=enklave_combat)
    if existing_enklave_combat:
        raise ValidationError({'detail': 'You already joined this combat once'})

    if not enklave_combat:
        if user.userprofile.faction == enklave.faction:
            raise ValidationError({'detail': 'Why is the user attacking his own enklave?'})

        enklave.shield = 500
        enklave.save()

        enklave_combat = EnklaveCombat.objects.create(
            started_by=user,
            enklave=enklave
        )

    enklave_combat_user = EnklaveCombatUser.objects.create(
        enklave_combat=enklave_combat,
        user=user,
        type=2 if user.userprofile.faction != enklave.faction else 1
    )

    user.userprofile.current_action_completed_at = None
    user.usercrafting.save()

    user_ids = EnklaveSubscriber.objects \
        .filter(enklave=enklave_combat.enklave).values_list('user_id', flat=True)

    user_ids2 = EnklaveCombatUser.objects \
        .filter(enklave_combat=enklave_combat).values_list('user_id', flat=True)

    user_ids = list(set(list(user_ids) + list(user_ids2)))
    redis_lib = RedisLib()

    attack_config = config_lib.get_attack_config_for_user(user)
    energy_config_data = config_lib.get_energy_config_for_user(user)

    redis_lib.publish_join(user, enklave_combat, enklave_combat_user, user.userprofile.faction, attack_config,
                           energy_config_data, user_ids)

    attack_config = config_lib.get_attack_config_for_user(user)
    energy_config = config_lib.get_energy_config_for_user(user)

    return Response(
        data={
            'attack_config': attack_config,
            'energy_config': energy_config,
            'time_recharging': 5,
            'enklave_combat_id': enklave_combat.id,
            'enklave_combatant_id': enklave_combat_user.id
        },
        status=status.HTTP_201_CREATED)


@receiver(post_save, sender=UserLocation)
def process_on_location_change(sender, instance, created, **kwargs):
    user_combat = EnklaveCombatUser.objects \
        .filter(user=instance.user, date_left__isnull=True) \
        .prefetch_related('enklave_combat', 'enklave_combat__enklave') \
        .first()
    if user_combat:
        if user_combat.enklave_combat.enklave == 16066:
            return

        distance = processing.calc_dist(
            user_combat.enklave_combat.enklave.longitude,
            user_combat.enklave_combat.enklave.latitude,
            instance.longitude,
            instance.latitude
        )

        if distance > 0.06:
            #  TODO do this in models with save signals and send message via redis
            user_combat.date_left = timezone.now()
            user_combat.save()

            # user_ids = EnklaveSubscriber.objects \
            #     .filter(enklave=user_combat.enklave_combat.enklave).values_list('user_id', flat=True)
            #
            # user_ids2 = EnklaveCombatUser.objects \
            #     .filter(enklave_combat=user_combat.enklave_combat).values_list('user_id', flat=True)
            #
            # user_ids = list(set(list(user_ids) + list(user_ids2)))
            #
            # attack_config = config_lib.get_attack_config_for_user(user_combat.user)
            #
            # enklave_combat_status = combat_lib.test_if_enklave_combat_is_done(user_combat.enklave_combat, user_combat.user)
            #
            # redis_lib = RedisLib()
            # redis_lib.publish_leave(user_combat, user_combat.user, user_combat.user.userprofile.faction, user_ids,
            #                         attack_config,
            #                         enklave_combat_status)
            #
            # combat_lib.test_if_enklave_combat_is_done(user_combat.enklave_combat, user_combat.user)


@receiver(post_save, sender=EnklaveCombatUser)
def create_enklave_combat_user(sender, instance, created, **kwargs):
    if not created:
        if instance.date_left:
            user_ids = EnklaveSubscriber.objects \
                .filter(enklave=instance.enklave_combat.enklave).values_list('user_id', flat=True)

            user_ids2 = EnklaveCombatUser.objects \
                .filter(enklave_combat=instance.enklave_combat).values_list('user_id', flat=True)

            user_ids = list(set(list(user_ids) + list(user_ids2)))

            attack_config = config_lib.get_attack_config_for_user(instance.user)

            enklave_combat_status = combat_lib.test_if_enklave_combat_is_done(instance.enklave_combat, instance.user)

            redis_lib = RedisLib()
            redis_lib.publish_leave(instance, instance.user, instance.user.userprofile.faction, user_ids, attack_config,
                                    enklave_combat_status)

            combat_lib.test_if_enklave_combat_is_done(instance.enklave_combat, instance.user)


@api_view(['GET'])
def get_enklave_combat_status(request):
    """
    Get Enklave Combat Data
    Headers: Authorization: Bearer {access_token}
    Get parameter:
        enklave_id
    """
    validators.validate_exists_get_integer(request, 'enklave_id')
    enklave = Enklave.items.get_or_404(request.GET.get('enklave_id'))

    enklave_combat = EnklaveCombat.objects \
        .filter(enklave=enklave, ended_at__isnull=True) \
        .first()

    if not enklave_combat:
        return Response({"detail": "enklave is currently not under attack"})

    enklave_combat_users = EnklaveCombatUser.objects\
        .prefetch_related('user', 'user__userprofile', 'user__userprofile__faction')\
        .filter(enklave_combat=enklave_combat)

    combat_config = config_lib.get_combat_config()
    energy_settings = config_lib.get_energy_storage_config()

    combat_user_data_list = []
    for combat_user in enklave_combat_users:
        combat_user_data = combat_user.to_json()
        combat_user_data["recharge_time"] = 5
        combat_user_data["faction"] = combat_user.user.userprofile.faction.id \
            if combat_user.user.userprofile.faction else None
        combat_user_data["energy"] = combat_user.user.userprofile.energy
        combat_user_data["combat_config"] = config_lib.get_attack_config_for_user_multiple(
            combat_user.user, combat_config)
        combat_user_data['energy_config'] = config_lib.get_energy_config_for_user_multiple(
            combat_user.user, energy_settings
        )

        combat_user_data_list.append(combat_user_data)

    return Response(data={"enklave_combat": enklave_combat.to_json(), "combatants": combat_user_data_list})


@api_view(['POST'])
@parser_classes((JSONParser,))
def start_raider_combat(request):
    """
    Start raider combat
    Headers: Authorization: Bearer {access_token}

    Post Data:
    Request Example:
    application/json
    {
        "raider_id": abc213... (uuid hex)
    }

    Note: user must be in range for this action
    """
    user = request.user
    validators.validate_attack_raider(request)

    raider = Raider2.objects.filter(id=request.data['raider_id']) \
        .extra(select={"current_latitude": """
            select rdr1.latitude from backend_raiderposition rdr1
            where rdr1.raider_id=backend_raider2.id
                and rdr1.starts_at<'{0}' and rdr1.ends_at>='{0}'""".format(timezone.now())}) \
        .extra(select={"current_longitude": """
            select rdr1.longitude from backend_raiderposition rdr1
            where rdr1.raider_id=backend_raider2.id
                and rdr1.starts_at<'{0}' and rdr1.ends_at>='{0}'""".format(timezone.now())}).first()
    if not raider:
        raise NotFound("Raider not found")

    geo_lib.validate_proximity_raider(user, raider.current_latitude, raider.current_longitude)
    verify_raider_combat(raider)
    verify_user_combat(user)

    raider_combat = RaiderCombat.objects.create(started_by=user)
    raider_combat_raider = RaiderCombatRaider.objects.create(raider_combat=raider_combat, raider=raider)
    raider_combat_user = RaiderCombatUser.objects.create(raider_combat=raider_combat, user=user)

    # combat_actions = combat_lib.generate_raider_combat_actions(raider_combat)
    # combat_lib.store_attack_sequence(attack_sequence=combat_actions, raider_combat=raider_combat)

    data = {
        'raider_combat': raider_combat.to_json(),
        'combatants': [raider_combat_user.to_json(), raider_combat_raider.to_json()]
    }
    return Response(data=data, status=status.HTTP_201_CREATED)


@api_view(['GET'])
def update_raider_combat_status(request):
    """
    Get the raider's action for that time
    Get parameter: raider_combat (int)

    The raider will choose a target from the users in combat or the enklave components (brick, turret, shield)
    The raider will always chose the target with the lowest defence (energy)
    """

    # user = request.user
    validators.validate_exists_get_integer(request, 'raider_combat_id')
    raider_combat = RaiderCombat.objects.get_or_404(request.GET.get('raider_combat_id'))
    raider_user_combat_list = RaiderCombatUser.objects.\
        filter(raider_combat=raider_combat, combatant_ptr__date_left__isnull=True).\
        prefetch_related("user", "user__userprofile")

    target_user = combat_lib.get_user_with_min_energy(raider_user_combat_list)

    if raider_combat.enklave:
        turrets = Turret.objects.filter(crafteditem_ptr__enklave=raider_combat.enklave).order_by('energy')[0]
        bricks = Brick.objects.filter(crafteditem_ptr__enklave=raider_combat.enklave).order_by('energy')[0]
        shields = Shield.objects.filter(crafteditem_ptr__enklave=raider_combat.enklave).order_by('energy')[0]

    # TODO get target from list [target_user, turret, brick, shield]

    """
        If more than x seconds passed from the last combat update we need to make updates for multiple steps
        1 step = 3 - 5 seconds
        if 10 seconds passed from the last update we need to process 3 attack steps etc
    """
    data = {

    }

    return Response(data)



@api_view(['POST'])
@parser_classes((JSONParser,))
def subscribe_to_enklave_combat(request):
    """
    Subscribe to enklave
    Headers: Authorization: Bearer {access_token}

    Post Data:
    Request Example:
    application/json
    {
        "enklave_id": 11
    }

    Note: user must be in range for this action
    """

    user = request.user
    validators.validate_subscribe_to_enklave_combat(request)

    enklave = get_object_or_404(Enklave, pk=request.data['enklave_id'])

    enklave_subscriber, created = EnklaveSubscriber\
        .objects.get_or_create(user=user, enklave=enklave)

    return Response(data=enklave_subscriber.to_json())


@api_view(['POST'])
@parser_classes((JSONParser,))
def unsubscribe_to_enklave_combat(request):
    """
    Unsubscribe to enklave
    Headers: Authorization: Bearer {access_token}

    Post Data:
    Request Example:
    application/json
    {
        "enklave_id": 11
    }
    """

    user = request.user
    validators.validate_subscribe_to_enklave_combat(request)

    enklave = get_object_or_404(Enklave, pk=request.data['enklave_id'])

    enklave_subscriber = EnklaveSubscriber.objects\
        .filter(user=user, enklave=enklave).first()

    if not enklave_subscriber:
        raise NotFound()

    enklave_subscriber.delete()

    return Response(status=status.HTTP_204_NO_CONTENT)


@api_view(['POST'])
@parser_classes((JSONParser,))
def attack_enklave_hit_user(request):
    """
    Hit user in an enklave attack
    Headers: Authorization: Bearer {access_token}

    Post Data:
    Request Example:
    application/json
    {
        "enklave_combatant_id": 11
    }
    """

    user = request.user
    validators.validate_attack_enklave_hit_user(request)

    enklave_combatant = get_object_or_404(
        EnklaveCombatUser,
        pk=request.data['enklave_combatant_id'],
        date_left__isnull=True
    )

    if enklave_combatant.user == user:
        raise ValidationError({'detail': 'C\'mon man, don\'t hit yourself'})

    enklave_combatant_user = EnklaveCombatUser.objects\
        .filter(user=user, enklave_combat=enklave_combatant.enklave_combat, date_left__isnull=True).first()

    if not enklave_combatant_user:
        raise ValidationError({'detail': 'You\'re not in the same combat, man'})

    attack_config = config_lib.get_attack_config_for_user(user)
    if user.userprofile.energy < attack_config['PLAYER_SHOT_COST_PROGRESSION']:
        raise ValidationError({'detail': 'user does not have enough energy'})

    if enklave_combatant_user.last_hit_at and \
       enklave_combatant_user.last_hit_at >= timezone.now() - timedelta(seconds=5):

        raise ValidationError({'detail': 'you\'re still recharging'})

    enklave_combatant_user.last_hit_at = timezone.now()
    enklave_combatant_user.save()

    user.userprofile.energy -= attack_config['PLAYER_SHOT_COST_PROGRESSION']
    user.userprofile.save()

    attack_power = attack_config['PLAYER_DAMAGE_PROGRESSION']

    enklave_combatant.user.userprofile.energy -= attack_power
    enklave_combatant.user.userprofile.save()

    user_ids = EnklaveSubscriber.objects\
        .filter(enklave=enklave_combatant.enklave_combat.enklave).values_list('user_id', flat=True)

    user_ids2 = EnklaveCombatUser.objects\
        .filter(enklave_combat=enklave_combatant.enklave_combat).values_list('user_id', flat=True)

    user_ids = list(set(list(user_ids) + list(user_ids2)))

    user_dead = False
    if enklave_combatant.user.userprofile.energy <= 0:
        enklave_combatant.date_left = timezone.now()
        enklave_combatant.save()

        user_dead = True

    enklave_combat_status = combat_lib.test_if_enklave_combat_is_done(enklave_combatant.enklave_combat, user)

    redis_lib = RedisLib()
    redis_lib.publish_hit(enklave_combatant, user, enklave_combatant_user,
                          user.userprofile.faction, user_ids, attack_config,
                          enklave_combat_status)

    return Response(data={
        'hit_value': attack_power,
        'user_out': user_dead,
        'user_energy': enklave_combatant.user.userprofile.energy,
        'enklave_combat_status': enklave_combat_status
    })


@api_view(['POST'])
@parser_classes((JSONParser,))
def attack_enklave_hit_enklave(request):
    """
    Hit user in an enklave attack
    Headers: Authorization: Bearer {access_token}

    Post Data:
    Request Example:
    application/json
    {
        "enklave_combat_id": 11
    }
    """

    user = request.user
    validators.validate_attack_enklave_hit_enklave(request)

    enklave_combat = get_object_or_404(
        EnklaveCombat,
        pk=request.data['enklave_combat_id']
    )

    if enklave_combat.enklave.faction == user.userprofile.faction:
        raise ValidationError({'detail': 'C\'mon man, don\'t hit your own enklave'})

    enklave_combatant_user = EnklaveCombatUser.objects \
        .filter(user=user, enklave_combat=enklave_combat, date_left__isnull=True).first()

    if not enklave_combatant_user:
        raise ValidationError({'detail': 'You\'re not in the same combat'})

    attack_config = config_lib.get_attack_config_for_user(user)
    if user.userprofile.energy < attack_config['PLAYER_SHOT_COST_PROGRESSION']:
        raise ValidationError({'detail': 'user does not have enough energy'})

    if enklave_combatant_user.last_hit_at and \
       enklave_combatant_user.last_hit_at >= timezone.now() - timedelta(seconds=5):

        raise ValidationError({'detail': 'you\'re still recharging'})

    enklave_combatant_user.last_hit_at = timezone.now()
    enklave_combatant_user.save()

    user.userprofile.energy -= attack_config['PLAYER_SHOT_COST_PROGRESSION']
    user.userprofile.save()

    attack_power = attack_config['PLAYER_DAMAGE_PROGRESSION']

    bricks_count = None

    attack_status = None
    if enklave_combat.enklave.shield > 0:
        enklave_combat.enklave.shield -= attack_power
        enklave_combat.enklave.save()
        attack_status = {
            'message': 'shield_hit',
            'shield_energy_lost': attack_power,
            'shield_energy_remaining': enklave_combat.enklave.shield
        }
    else:
        brick = Brick.objects.filter(enklave=enklave_combat.enklave, energy__gt=0).first()
        if brick:
            brick.energy -= attack_power
            brick.save()
            bricks_count = Brick.objects.filter(enklave=enklave_combat.enklave, energy__gt=0).count()
            attack_status = {
                'message': 'brick_hit',
                'brick_energy_lost': attack_power,
                'bricks_count': bricks_count,
                'brick_energy_remaining': brick.energy
            }

    enklave_combat_status = None
    enklave_conquered = False

    if not attack_status or bricks_count == 0:
        defenders_count = EnklaveCombatUser.objects \
            .filter(enklave_combat=enklave_combat, date_left__isnull=True, type=1).count()

        if defenders_count > 0:
            attack_status = {
                'message': 'enklave has no more bricks and shields but it still has defenders'
            }
            enklave_combat_status = 'enklave has no more bricks and shields but it still has defenders'

        else:
            attack_status = {
                'message': 'enklave is down, it has been conquered by the attackers'
            }

            enklave_combat.enklave.faction = None
            enklave_combat.enklave.safe_until = timezone.now() + timedelta(minutes=3)
            enklave_combat.enklave.save()
            enklave_combat_status = "Attackers won"
            enklave_combat.ended_at = timezone.now()
            combat_lib.restart_test_enklave(enklave_combat.enklave)

            enklave_conquered = True
            enklave_combat.notes = "Attackers won at {0}".format(timezone.now())
            enklave_combat.save()
            EnklaveCombatUser.objects.filter(
                enklave_combat=enklave_combat,
                date_left__isnull=True).update(
                date_left=timezone.now()
            )

    user_ids = EnklaveSubscriber.objects \
        .filter(enklave=enklave_combat.enklave).values_list('user_id', flat=True)

    user_ids2 = EnklaveCombatUser.objects \
        .filter(enklave_combat=enklave_combat).values_list('user_id', flat=True)

    user_ids = list(set(list(user_ids) + list(user_ids2)))

    redis_lib = RedisLib()
    redis_lib.publish_enklave_hit(enklave_combat, user, enklave_combatant_user, user.userprofile.faction,
                                  user_ids, attack_config, enklave_combat_status, attack_status, enklave_conquered)

    return Response(data={
        'hit_value': attack_power,
        'attack_status': attack_status,
        'enklave_combat_status': enklave_combat_status
    })



