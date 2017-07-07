"""enklave URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.8/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Add an import:  from blog import urls as blog_urls
    2. Add a URL to urlpatterns:  url(r'^blog/', include(blog_urls))
"""
from django.conf.urls import include, url
from django.contrib import admin
from backend.custom_views import authorize, device, user, socket, enklave, chats, raiders, factions, messages
from backend.custom_views import crafting, combat


urlpatterns = [
    url(r'^admin/', include(admin.site.urls)),
    url(r'', include('social.apps.django_app.urls', namespace='social')),
    url(r'^auth/', include('rest_framework_social_oauth2.urls')),
    url(r'^o/', include('oauth2_provider.urls', namespace='oauth2_provider')),
    url(r'^docs/', include('rest_framework_swagger.urls')),
    # url(r'^authorize/', authorize.get_application),

    url(r'^user/device/update/', device.update_device),
    url(r'^user/device/mine/', device.get_devices),
    url(r'^user/device/delete/', device.delete_device),
    url(r'^user/profile/', user.get_session_user_data),
    url(r'^user/get/', user.get_user_public_data),
    url(r'^user/stats/', user.get_user_stats),

    url(r'^enklave/create/', enklave.create_enklave),
    url(r'^enklave/get/', enklave.get_enklaves),
    url(r'^enklave/details/', enklave.get_enklave_by_id),
    url(r'^enklave/nearby/', enklave.get_nearby_enklaves),

    url(r'^combat/start/enklave/', combat.start_enklave_combat),
    url(r'^combat/status/enklave/', combat.get_enklave_combat_status),
    url(r'^combat/hit/user/', combat.attack_enklave_hit_user),
    url(r'^combat/hit/enklave/', combat.attack_enklave_hit_enklave),
    url(r'^combat/subscribe/enklave/', combat.subscribe_to_enklave_combat),
    url(r'^combat/unsubscribe/enklave/', combat.unsubscribe_to_enklave_combat),

    url(r'^combat/start/raider/', combat.start_raider_combat),
    url(r'^combat/update/raider/', combat.update_raider_combat_status),
    url(r'^combat/user/status/', combat.get_if_in_combat),
    # url(r'^enklave/hit/', enklave.hit_enklave),
    # url(r'^enklave/hit_opponent/', enklave.hit_opponent),
    # url(r'^enklave/join/', enklave.join_combat),


    url(r'^raider/nearby/', raiders.get_nearby_raiders),
    url(r'^raider/enklave/', raiders.get_raiders_for_enklave),
    url(r'^raider/get/', raiders.get_raiders_by_id),

    url(r'^faction/join/', factions.join_faction),
    url(r'^faction/leave/', factions.leave_faction),
    url(r'^faction/all/', factions.get_factions),

    url(r'^message/single/send/', messages.send_one_to_one_message),
    url(r'^message/location/send/', messages.send_location_message),
    url(r'^message/faction/send/', messages.send_faction_message),

    url(r'^message/single/history/', messages.get_message_history),

    url(r'^crafting/brick/build/', crafting.craft_brick),
    url(r'^crafting/cell/build/', crafting.craft_cell),
    url(r'^crafting/cell/use/', crafting.use_cell),
    url(r'^crafting/turret/build/', crafting.craft_turret),
    url(r'^crafting/turret/install/', crafting.install_turret),
    url(r'^crafting/shield/build/', crafting.craft_shield),
    url(r'^crafting/shield/install/', crafting.install_shield),
    url(r'^crafting/brick/place/', crafting.place_brick),

    url(r'^socket/ticket/get/', socket.get_ticket),
    url(r'^socket/page/', chats.index),

    url(r'^forgot_password/', user.forgot_password),
    url(r'^reset_password/', user.reset_password, name='reset_password'),


    url(r'^users/register', authorize.create_user, name='register'),
    url(r'^silk/', include('silk.urls', namespace='silk')),
]


