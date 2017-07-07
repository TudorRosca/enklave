from django.db import models
from django.contrib.auth.models import User
from django.db.models.signals import post_save, pre_save, post_delete
from django.dispatch import receiver
from rest_framework.exceptions import NotFound
from django_earthdistance.models import EarthDistanceQuerySet
from backend.utils import processing
from django.utils import timezone
import uuid
from math import cos, fabs
import logging
logger = logging.getLogger('api_exceptions')


class ObjectManager(models.Manager):

    def get_or_404(self, object_id):
        try:
            object_item = self.model.objects.get(pk=object_id)
        except self.model.DoesNotExist:
            raise NotFound('{1} not found: {0}'.format(object_id, self.model.__name__))

        return object_item


class Country(models.Model):
    name = models.CharField(max_length=255)

    def __unicode__(self):
        return self.name


class Faction(models.Model):
    name = models.CharField(max_length=100)
    description = models.CharField(max_length=255)

    display_options = (
        (1, '1'),
        (2, '2'),
        (3, '3'),
    )

    display_order = models.SmallIntegerField(choices=display_options)
    color = models.CharField(max_length=12)
    logo = models.URLField(max_length=100)

    objects = ObjectManager()

    def __str__(self):
        return self.name

    def to_json(self):
        return {
            'id': self.id,
            'name': self.name,
            'description': self.description,
            'color': self.color,
            'logo': self.logo
        }


class UserProfile(models.Model):
    user = models.OneToOneField(User, db_constraint=False)
    phone_number = models.CharField(max_length=40, blank=True, null=True)
    arn = models.CharField(max_length=512, blank=True, null=True)
    profile_image = models.CharField(max_length=40, blank=True, null=True)
    ejabberd_token = models.CharField(max_length=40, blank=True, null=True)
    date_of_birth = models.DateTimeField(default=None, blank=True, null=True)

    faction = models.ForeignKey(Faction, db_constraint=False, default=None, null=True, blank=True)
    locked = models.BooleanField(default=False)
    has_app_access = models.BooleanField(default=False)
    country = models.ForeignKey(Country, db_constraint=False, default=None, null=True, blank=True)

    level = models.IntegerField(default=1)

    experience = models.IntegerField(default=0, blank=True, null=True)
    energy = models.IntegerField(default=0, blank=True, null=True)
    energy_calculated_at = models.DateTimeField(default=None, blank=True, null=True)
    scrap = models.IntegerField(default=0, blank=True, null=True)
    scrap_calculated_at = models.DateTimeField(default=None, blank=True, null=True)
    distance_walked = models.FloatField(default=0, blank=True, null=True)
    merit = models.IntegerField(default=0, blank=True, null=True)

    def __str__(self):
        return "%s's profile" % self.user

    def to_profile(self):
        data = {
            'id': self.user.id,
            'username': self.user.username, 'first_name': self.user.first_name, 'last_name': self.user.last_name,
            'email': self.user.email, 'password': 'secret', 'date_joined': self.user.date_joined,
            'phone_number': self.phone_number,
            'experience': self.experience or 0,
            'energy': self.energy or 0,
            'faction': self.faction.id if self.faction else None,
            'level': self.level,
            'scrap': self.scrap or 0,
            'distance_walked': self.distance_walked or 0
        }

        return data


@receiver(post_save, sender=User)
def create_user_profile(sender, instance, created, **kwargs):
    if created:
        UserProfile.objects.get_or_create(user=instance)
        UserSettings.objects.get_or_create(user=instance)
        UserCrafting.objects.get_or_create(user=instance)


class UserDevice(models.Model):
    user = models.ForeignKey(User, db_constraint=False)
    device_id = models.CharField(max_length=100)
    app_version = models.CharField(max_length=100)
    arn = models.CharField(max_length=512, blank=True, null=True)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)

    objects = ObjectManager()

    def to_json(self):
        data = {
            'id': self.id,
            'user': {
                'id': self.user.id,
                'username': self.user.username
            },
            'device_id': self.device_id,
            'app_version': self.app_version,
            'arn': self.arn
        }

        return data


class UserNotification(models.Model):
    user = models.ForeignKey(User)
    sender = models.ForeignKey(
        User,
        related_name='%(class)s_sender',
        db_constraint=False,
        default=None,
        null=True,
        blank=True
    )

    title = models.CharField(max_length=255, default="", blank=True)
    message = models.CharField(max_length=255)

    type = models.CharField(max_length=50)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)


from django.db import models


class ChatRoom(models.Model):
    name = models.CharField(max_length=200)

    def __unicode__(self):
        return self.name


from django.db.models.signals import post_save
from backend.utils.redis_lib import RedisLib
import json


@receiver(post_save, sender=UserNotification)
def create_user_notification(sender, instance, created, **kwargs):
    if created:
        redis_lib = RedisLib()
        notification_data = {
            "msg_type": "notification",
            "user_id": instance.user.id,
            "message": instance.message
        }
        redis_lib.publish("test_realtime".format(instance.user.id), json.dumps(notification_data))


class UserConnection(models.Model):
    user = models.ForeignKey(User, db_constraint=False)
    is_connected = models.BooleanField(default=None)
    last_connected_at = models.DateTimeField(default=None, blank=True, null=True)

    changed_at = models.DateTimeField(auto_now=True)


class Message(models.Model):
    from_user = models.ForeignKey(User, db_constraint=False, related_name="from_user")
    to_user = models.ForeignKey(User, db_constraint=False, related_name="to_user")

    txt = models.CharField(max_length=255)
    date_viewed = models.DateTimeField(default=None, blank=True, null=True)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)

    def to_json(self):
        return {
            "id": self.id,
            "txt": self.txt,
            "from_user": self.from_user.username,
            "to_user": self.to_user.username,
            "date_viewed": self.date_viewed.strftime('%Y-%m-%dT%H:%M:%S.%fZ') if self.date_viewed else None,
            "created_at": self.created_at.strftime('%Y-%m-%dT%H:%M:%S.%fZ')
        }


@receiver(post_save, sender=Message)
def send_single_message(sender, instance, created, **kwargs):
    redis_lib = RedisLib()
    if created:
        message_data = {
            "user_id": instance.to_user.id,
            "payload": {
                "msg_type": "message",
                "message": instance.txt,
                "from_user": instance.from_user.first_name,
                "to_user": instance.to_user.username,
                "created_at": instance.created_at.strftime('%Y-%m-%dT%H:%M:%S.%fZ'),
                "date_viewed": instance.date_viewed.strftime('%Y-%m-%dT%H:%M:%S.%fZ') if instance.date_viewed else None,
                "id": instance.id
            }
        }

        redis_lib.publish("enklave_line", json.dumps(message_data))
    else:
        if instance.date_viewed:
            message_data = {
                "user_id": instance.from_user.id,
                "payload": {
                    "msg_type": "message_viewed",
                    "date_viewed": instance.date_viewed.strftime('%Y-%m-%dT%H:%M:%S.%fZ') if instance.date_viewed
                    else None,
                    "id": instance.id
                }
            }

            redis_lib.publish("enklave_line", json.dumps(message_data))


class LocationMessage(models.Model):
    user = models.ForeignKey(User, db_constraint=False)
    latitude = models.FloatField()
    longitude = models.FloatField()
    txt = models.CharField(max_length=255)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)

    def to_json(self):
        return {
            "id": self.id,
            "txt": self.txt,
            "from_user": self.user.first_name,
            "latitude": self.latitude,
            "longitude": self.longitude,
            "created_at": self.created_at.strftime('%Y-%m-%dT%H:%M:%S.%fZ')
        }


class UserLocation(models.Model):
    user = models.ForeignKey(User, db_constraint=False)
    latitude = models.FloatField()
    longitude = models.FloatField()
    prev_latitude = models.FloatField(default=None, blank=True, null=True)
    prev_longitude = models.FloatField(default=None, blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)

    objects = EarthDistanceQuerySet.as_manager()

    def __init__(self, *args, **kwargs):
        super(UserLocation, self).__init__(*args, **kwargs)
        self.__original_latitude = self.latitude
        self.__original_longitude = self.longitude

@receiver(pre_save, sender=UserLocation)
def pre_change_location_calc_dist(sender, instance, **kwargs):
    if instance.prev_latitude is None or instance.prev_longitude is None:
        instance.prev_latitude = instance.latitude
        instance.prev_longitude = instance.longitude


@receiver(post_save, sender=UserLocation)
def post_change_location_calc_dist(sender, instance, created, **kwargs):
    if created:
        return

    if instance.prev_latitude == instance.latitude and instance.prev_longitude == instance.longitude:
        return

    dist = processing.calc_dist(
        instance.prev_longitude,
        instance.prev_latitude,
        instance.longitude,
        instance.latitude
    )
    if instance.user.userprofile.distance_walked:
        instance.user.userprofile.distance_walked += dist
    else:
        instance.user.userprofile.distance_walked = dist
    instance.user.userprofile.save()


@receiver(post_save, sender=LocationMessage)
def send_location_message(sender, instance, created, **kwargs):
    if not created:
        return

    distance_max = 1000000

    distance_lat = (1 / 110.574) * (distance_max / 2)   # degree to kilometer times 1.5 divided by 2
    distance_lon = fabs((1 / (111.320*cos(instance.latitude))) * (distance_max / 2))

    lat_range = (instance.latitude - distance_lat, instance.latitude + distance_lat)
    lon_range = (instance.longitude - distance_lon, instance.longitude + distance_lon)

    faction_id = instance.user.userprofile.faction_id if instance.user.userprofile.faction else None

    user_ids = UserLocation.objects\
        .filter(latitude__range=lat_range, longitude__range=lon_range) \
        .values_list('user_id', flat=True)

    redis_lib = RedisLib()
    message_data = {
        "user_ids": list(set(user_ids)),
        "payload": {
            "msg_type": "location_message",
            "message": instance.txt,
            "faction_id": faction_id,
            "from_user": instance.user.first_name,
            "created_at": instance.created_at.strftime('%Y-%m-%dT%H:%M:%S.%fZ'),
            "id": instance.id
        }
    }

    logger.info(message_data)

    redis_lib.publish("enklave_line", json.dumps(message_data))


class AppConfig(models.Model):
    name = models.CharField(max_length=255)
    value = models.TextField()
    description = models.CharField(max_length=255, default=None, null=True, blank=True)

    def __unicode__(self):
        return self.name


class Enklave(models.Model):
    user = models.ForeignKey(User, db_constraint=False, default=None, null=True, blank=True)
    latitude = models.FloatField(db_index=True)
    longitude = models.FloatField(db_index=True)
    confirmed_by = models.ForeignKey(User, db_constraint=False, related_name='moderator', default=None, null=True,
                                     blank=True)

    name = models.CharField(max_length=255, null=True, blank=True)
    description = models.CharField(max_length=255, null=True, blank=True)
    confirmed_at = models.DateTimeField(default=None, blank=True, null=True)

    scrap = models.IntegerField(default=0)
    level = models.IntegerField(default=0)
    bricks = models.IntegerField(default=0)
    cells = models.IntegerField(default=0)
    shield = models.IntegerField(default=0)
    last_production_at = models.DateTimeField(default=None, blank=True, null=True)

    destroyed_at = models.DateTimeField(default=None, blank=True, null=True)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)
    safe_until = models.DateTimeField(default=None, blank=True, null=True)

    enabled = models.BooleanField(default=True)
    faction = models.ForeignKey(Faction, default=None, null=True, blank=True)

    objects = EarthDistanceQuerySet.as_manager()
    items = ObjectManager()

    def __unicode__(self):
        return str(self.id)

    def to_json(self):
        data = {
            'id': self.id,
            'created_by': {
                'id': self.user.id,
                'username': self.user.username,
                } if self.user else None,
            'confirmed_by': {
                'id': self.confirmed_by.id,
                'username': self.confirmed_by.username,
                } if self.confirmed_by else None,
            'latitude': self.latitude,
            'longitude': self.longitude,
            'faction': self.faction.id if self.faction else None,
            'created_at': self.created_at,
            'updated_at': self.updated_at,
            'deleted_at': self.deleted_at,
            'name': self.name,
            'nr_bricks': self.bricks,
            'description': self.description,
            'confirmed': True if self.confirmed_at else False,
            'confirmed_at': self.confirmed_at
        }

        return data

    def details(self):
        enklave_combat = EnklaveCombat.objects.filter(enklave=self, ended_at__isnull=True).first()
        if enklave_combat:
            status = 'InCombat'
        else:
            status = 'NotInCombat'

        return {
            'id': self.id,
            'created_by': {
                'id': self.user.id,
                'username': self.user.username,
                } if self.user else None,
            'confirmed_by': {
                'id': self.confirmed_by.id,
                'username': self.confirmed_by.username,
                } if self.confirmed_by else None,
            'latitude': self.latitude,
            'longitude': self.longitude,
            'faction': self.faction.id if self.faction else None,
            'created_at': self.created_at,
            'updated_at': self.updated_at,
            'deleted_at': self.deleted_at,
            'safe_until': self.safe_until,
            'name': self.name,
            'description': self.description,
            'confirmed': True if self.confirmed_at else False,
            'confirmed_at': self.confirmed_at,
            'status': status,
            # 'bricks': self.bricks,
            'cells': self.cells,
            'shield': self.shield,
            'rooms': 0,
            'extensions': 0,
        }


class EnklaveImage(models.Model):
    enklave = models.ForeignKey(Enklave, db_constraint=False)
    image_url = models.URLField(max_length=255)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)


class HomeBase(models.Model):
    user = models.ForeignKey(User, db_constraint=False)
    latitude = models.FloatField()
    longitude = models.FloatField()
    scrap = models.IntegerField()
    bricks = models.IntegerField()
    cells = models.IntegerField()

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    location_updated_at = models.DateTimeField(default=None, blank=True, null=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)


class UserSettings(models.Model):
    user = models.OneToOneField(User, db_constraint=False)
    chat_receive_radius = models.IntegerField(default=20)
    updated_at = models.DateTimeField(auto_now=True)


class ResetPasswordToken(models.Model):
    user = models.ForeignKey(User, db_constraint=False)
    token = models.CharField(max_length=40)
    expire_date = models.DateTimeField(default=None, blank=True, null=True)


class Raider2(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    enklave = models.ForeignKey(Enklave, db_constraint=False)
    latitude = models.FloatField()
    longitude = models.FloatField()
    bearing = models.SmallIntegerField(default=100)
    level = models.IntegerField(default=0)
    energy = models.IntegerField(default=0)
    status = models.SmallIntegerField(default=0)

    hits_at = models.DateTimeField(default=None, blank=True, null=True)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)

    def to_json(self):
        return {
            'id': self.id,
            'enklave_id': self.enklave.id,
            'latitude_start': self.latitude,
            'longitude_start': self.longitude,
            'bearing': self.bearing,
            'level': self.level,
            'energy': self.energy,
            'status': self.status,
            'hits_at': self.hits_at,
        }


class RaiderPosition(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    raider = models.ForeignKey(Raider2, db_constraint=False)
    latitude = models.FloatField()
    longitude = models.FloatField()

    starts_at = models.DateTimeField(db_index=True)
    ends_at = models.DateTimeField(db_index=True)

    objects = EarthDistanceQuerySet.as_manager()


class FactionMessage(models.Model):
    user = models.ForeignKey(User, db_constraint=False)
    faction = models.ForeignKey(Faction, db_constraint=False)
    txt = models.CharField(max_length=255)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)

    def to_json(self):
        return {
            "id": self.id,
            "txt": self.txt,
            "from_user": self.user.first_name,
            "faction": self.faction.id,
            "created_at": self.created_at.strftime('%Y-%m-%dT%H:%M:%S.%fZ')
        }



@receiver(post_save, sender=FactionMessage)
def send_faction_message(sender, instance, created, **kwargs):
    if not created:
        return

    user_ids = UserProfile.objects.filter(faction=instance.faction).values_list('user_id', flat=True)

    redis_lib = RedisLib()
    message_data = {
        "user_ids": list(set(user_ids)),
        "payload": {
            "msg_type": "faction_message",
            "faction_id": instance.faction.id,
            "message": instance.txt,
            "from_user": instance.user.first_name,
            "created_at": instance.created_at.strftime('%Y-%m-%dT%H:%M:%S.%fZ'),
            "id": instance.id
        }
    }

    redis_lib.publish("enklave_line", json.dumps(message_data))


class UserCrafting(models.Model):
    user = models.OneToOneField(User, db_constraint=False)
    nr_bricks = models.IntegerField(default=0)
    next_brick_at = models.DateTimeField(default=None, blank=True, null=True)
    nr_cells = models.IntegerField(default=0)
    next_cell_at = models.DateTimeField(default=None, blank=True, null=True)
    next_cell_convert_at = models.DateTimeField(default=None, blank=True, null=True)

    current_action_completed_at = models.DateTimeField(default=None, blank=True, null=True)

    choice = (
        (1, 'Craft Brick'),
        (2, 'Craft Cell'),
        (3, 'Use Cell'),
        (4, 'Craft Turret'),
        (5, 'Craft Shield'),
        (6, 'Install Turret'),
        (7, 'Install Shield'),
        (8, 'Place Brick'),
    )

    # TODO update options

    current_action_type = models.IntegerField(choices=choice, default=1)
    current_action_enklave = models.ForeignKey(Enklave, db_constraint=False, null=True, blank=True, default=None)

    def to_json(self):
        data = {
            'id': self.id,
            'user': {
                'id': self.user.id,
                'username': self.user.username
            },
            'nr_bricks': self.nr_bricks,
            'next_brick_at': self.next_brick_at,
            'nr_cells': self.nr_cells,
            'next_cell_at': self.next_cell_at,
            'next_cell_convert_at': self.next_cell_convert_at,
        }

        return data


class EnklaveCombat(models.Model):
    enklave = models.ForeignKey(Enklave, db_constraint=False)
    started_by = models.ForeignKey(User, db_constraint=False)
    started_at = models.DateTimeField(auto_now_add=True, blank=True)
    ended_at = models.DateTimeField(default=None, blank=True, null=True)
    notes = models.CharField(max_length=255, default=None, null=True, blank=True)

    objects = ObjectManager()

    def to_json(self):
        return {
            'enklave_combat_id': self.id,
            'started_at': self.started_at,
            'ended_at': self.ended_at
        }

    def __unicode__(self):
        return str(self.id)


class EnklaveCombatUser(models.Model):
    enklave_combat = models.ForeignKey(EnklaveCombat, db_constraint=False)
    user = models.ForeignKey(User, db_constraint=False)
    date_joined = models.DateTimeField(auto_now_add=True, blank=True)
    date_left = models.DateTimeField(default=None, blank=True, null=True)
    last_hit_at = models.DateTimeField(default=None, blank=True, null=True)

    choice = (
        (1, 'Defender'),
        (2, 'Attacker')
    )

    type = models.IntegerField(choices=choice, default=2)

    objects = ObjectManager()

    def to_json(self):
        return {
            'combatant_id': self.id,
            'enklave_id': self.enklave_combat.enklave.id,
            'user_id': self.user.id,
            'user_username': self.user.first_name,
            'date_joined': self.date_joined,
            'date_left': self.date_left,
            'type_name': self.choice[int(self.type)-1][1],
            'type_id': self.type
        }

    def __unicode__(self):
        return str(self.id)


class EnklaveCombatSubscriber(models.Model):
    enklave_combat = models.ForeignKey(EnklaveCombat, db_constraint=False)
    user = models.ForeignKey(User, db_constraint=False)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)

    def to_json(self):
        return {
            "id": self.id,
            'enklave_combat': self.enklave_combat.id,
            "user": self.user.username,
            "created_at": self.created_at.strftime('%Y-%m-%dT%H:%M:%S.%fZ')
        }


class EnklaveSubscriber(models.Model):
    enklave = models.ForeignKey(Enklave, db_constraint=False)
    user = models.ForeignKey(User, db_constraint=False)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    deleted_at = models.DateTimeField(default=None, blank=True, null=True)

    def to_json(self):
        return {
            "id": self.id,
            'enklave': self.enklave.id,
            "user": self.user.username,
            "created_at": self.created_at.strftime('%Y-%m-%dT%H:%M:%S.%fZ')
        }


class CraftedItem(models.Model):
    user = models.ForeignKey(User, db_constraint=False, related_name="%(class)s_ownership")
    enklave = models.ForeignKey(Enklave, db_constraint=False, null=True, default=None, blank=True)
    energy = models.IntegerField(default=350)
    choice = (
        (1, 'Small'),
        (2, 'Medium')
    )

    type = models.IntegerField(choices=choice, default=1)

    created_at = models.DateTimeField(auto_now_add=True, blank=True)
    updated_at = models.DateTimeField(auto_now=True)
    used_at = models.DateTimeField(default=None, blank=True, null=True)

    def to_json(self):
        return {
            'id': self.id,
            'user_id': self.user.id,
            'enklave_id': self.enklave.id if self.enklave else None,
            'type': self.type,
            'type_str': self.choice[int(self.type)],

            'created_at': self.created_at,
            'updated_at': self.updated_at,
            }


class Turret(CraftedItem):

    objects = ObjectManager()

    def __str__(self):
        return 'turret {0} - {1}'.format(self.id, self.user.id)


# @receiver(post_save, sender=Turret)
# def craft_turret_get_xp(sender, instance, created, **kwargs):
#     if not created:
#         return
#
#     instance.user.userprofile.experience += 10
#     instance.user.userprofile.save()


class Shield(CraftedItem):

    objects = ObjectManager()

    def __str__(self):
        return 'shield {0} - {1}'.format(self.id, self.user.id)


# @receiver(post_save, sender=Shield)
# def craft_shield_get_xp(sender, instance, created, **kwargs):
#     if not created:
#         return
#
#     instance.user.userprofile.experience += 10
#     instance.user.userprofile.save()


class Brick(CraftedItem):

    objects = ObjectManager()

    def __str__(self):
        return 'brick {0} - {1}'.format(self.id, self.user.id)


# @receiver(post_save, sender=Brick)
# def craft_brick_get_xp(sender, instance, created, **kwargs):
#     if not created:
#         return
#
#     instance.user.userprofile.experience += 10
#     instance.user.userprofile.save()


class Cell(CraftedItem):

    objects = ObjectManager()

    def __str__(self):
        return 'cell {0} - {1}'.format(self.id, self.user.id)


# @receiver(post_save, sender=Cell)
# def craft_cell_get_xp(sender, instance, created, **kwargs):
#     if not created:
#         return
#
#     instance.user.userprofile.experience += 10
#     instance.user.userprofile.save()


class RaiderCombat(models.Model):
    enklave = models.ForeignKey(Enklave, db_constraint=False, default=None, null=True, blank=True)
    started_by = models.ForeignKey(User, db_constraint=False, blank=True, null=True, default=None)
    started_at = models.DateTimeField(auto_now_add=True, blank=True)
    ended_at = models.DateTimeField(default=None, blank=True, null=True)
    notes = models.CharField(max_length=255, default=None, null=True, blank=True)
    last_processed_at = models.DateTimeField(default=None, blank=True, null=True)

    objects = ObjectManager()

    def to_json(self):
        return {
            'raider_combat_id': self.id,
            'started_at': self.started_at,
            'ended_at': self.ended_at
        }

    def __unicode__(self):
        return str(self.id)


class Combatant(models.Model):
    date_joined = models.DateTimeField(auto_now_add=True, blank=True)
    date_left = models.DateTimeField(default=None, blank=True, null=True)
    last_hit_at = models.DateTimeField(default=None, blank=True, null=True)

    objects = ObjectManager()

    def to_json(self):
        return {
            'raider_combatant_id': self.id,
            'date_joined': self.date_joined,
            'date_left': self.date_left
        }

    def __unicode__(self):
        return str(self.id)


class RaiderCombatUser(Combatant):
    raider_combat = models.ForeignKey(RaiderCombat, db_constraint=False)
    user = models.ForeignKey(User, db_constraint=False)

    objects = ObjectManager()

    def to_json(self):
        data = super(RaiderCombatUser, self).to_json()
        data['raider_combat_id'] = self.raider_combat.id
        data['user'] = {
            'id': self.user.id
        }
        data['type'] = 'user'

        return data


class RaiderCombatRaider(Combatant):
    raider_combat = models.ForeignKey(RaiderCombat, db_constraint=False)
    raider = models.ForeignKey(Raider2, db_constraint=False)

    objects = ObjectManager()

    def to_json(self):
        data = super(RaiderCombatRaider, self).to_json()
        data['raider_combat_id'] = self.raider_combat.id
        data['raider'] = {
            'id': self.raider.id
        }

        data['type'] = 'raider'

        return data

