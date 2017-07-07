from django.contrib import admin

from django.contrib import admin
from backend.models import *


class UserProfileAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'arn', 'profile_image', 'experience', 'scrap', 'distance_walked',
                    'merit', 'faction', 'energy')


class UserDeviceAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'device_id', 'app_version', 'arn')


class MessageAdmin(admin.ModelAdmin):
    list_display = ('id', 'from_user', 'to_user', 'txt', 'date_viewed', "created_at", "updated_at", "deleted_at")


class EnklaveAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'name', 'shield', 'description',
                    'scrap', 'level', 'bricks', 'cells',
                    'latitude', "longitude", "confirmed_by", "confirmed_at",
                    "created_at", "updated_at", "deleted_at")


class UserConnectionAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'is_connected', "last_connected_at")


class UserLocationAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'latitude', "longitude", "created_at", "updated_at")


class LocationMessageAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'latitude', 'longitude', 'txt', "created_at", "updated_at", "deleted_at")


class FactionAdmin(admin.ModelAdmin):
    list_display = ('id', 'name', 'description', 'color', 'display_order', "logo")


class AppConfigAdmin(admin.ModelAdmin):
    list_display = ('id', 'name', 'value', 'description')


class CountryAdmin(admin.ModelAdmin):
    list_display = ('id', 'name')


class EnklaveImageAdmin(admin.ModelAdmin):
    list_display = ('id', 'enklave', 'image_url', "created_at", "updated_at", "deleted_at")


class HomeBaseAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'latitude', 'longitude', 'scrap', 'bricks', 'cells',
                    "created_at", "location_updated_at", "updated_at", "deleted_at")


class RaiderAdmin(admin.ModelAdmin):
    list_display = ('id', 'enklave', 'latitude', 'longitude', 'level', 'energy', 'bearing', "hits_at",
                    "created_at", "status", "updated_at", "deleted_at")


class RaiderPositionAdmin(admin.ModelAdmin):
    list_display = ('id', 'raider', 'latitude', 'longitude', 'starts_at', 'ends_at')


class FactionMessageAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'faction', 'txt', "created_at", "updated_at", "deleted_at")


class UserCraftingAdmin(admin.ModelAdmin):
    list_display = ('id', 'user', 'nr_bricks', 'next_brick_at', 'next_cell_at', 'nr_cells', 'next_cell_convert_at')


class EnklaveCombatAdmin(admin.ModelAdmin):
    list_display = ('id', 'enklave', 'started_by', 'started_at', 'ended_at', 'notes')


class EnklaveSubscriberAdmin(admin.ModelAdmin):
    list_display = ('id', 'enklave', 'user', "created_at", "updated_at", "deleted_at")


class EnklaveCombatUserAdmin(admin.ModelAdmin):
    list_display = ('id', 'enklave_combat', 'user', 'date_joined', 'date_left', 'last_hit_at', 'type')


class BrickAdmin(admin.ModelAdmin):
    list_display = ('id', 'enklave', 'user', 'energy', 'used_at')


admin.site.register(UserProfile, UserProfileAdmin)
admin.site.register(UserDevice, UserDeviceAdmin)
admin.site.register(Enklave, EnklaveAdmin)
admin.site.register(UserNotification)
admin.site.register(Message, MessageAdmin)
admin.site.register(UserConnection, UserConnectionAdmin)
admin.site.register(UserLocation, UserLocationAdmin)
admin.site.register(LocationMessage, LocationMessageAdmin)
admin.site.register(Faction, FactionAdmin)
admin.site.register(Country, CountryAdmin)
admin.site.register(EnklaveImage, EnklaveImageAdmin)
admin.site.register(HomeBase, HomeBaseAdmin)
admin.site.register(Raider2, RaiderAdmin)
admin.site.register(RaiderPosition, RaiderPositionAdmin)
admin.site.register(AppConfig, AppConfigAdmin)
admin.site.register(FactionMessage, FactionMessageAdmin)
admin.site.register(UserCrafting, UserCraftingAdmin)
admin.site.register(EnklaveCombat, EnklaveCombatAdmin)
admin.site.register(EnklaveCombatUser, EnklaveCombatUserAdmin)
admin.site.register(EnklaveSubscriber, EnklaveSubscriberAdmin)
admin.site.register(Brick, BrickAdmin)


admin.site.site_header = 'Enklave Admin'
