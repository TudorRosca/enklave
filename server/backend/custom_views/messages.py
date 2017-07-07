__author__ = 'radu'
from rest_framework.decorators import api_view, parser_classes, permission_classes
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import AllowAny
from backend.utils import validators, helpers
from rest_framework.parsers import JSONParser
from rest_framework.exceptions import NotFound, ValidationError
from django.contrib.auth.models import User
from backend.models import Faction, Message, UserLocation, LocationMessage, FactionMessage
import logging
from django.db.models import Q
logger = logging.getLogger('api_exceptions')
from django.utils import timezone
from datetime import timedelta
import hashlib
from django.conf import settings
from django.shortcuts import render


@api_view(['POST'])
@parser_classes((JSONParser,))
def send_one_to_one_message(request):
    """
    Send message to user
    Posted Data:
    application/json
    {
        to_user: required - username string
        txt: required - max 255 characters
    }
    """

    validators.validate_send_one_to_one_message(request)

    to_user = User.objects.filter(username=request.data['to_user']).first()
    if not to_user:
        raise NotFound("User not found")

    user = request.user
    message_obj = Message.objects.create(to_user=to_user, from_user=user, txt=request.data['txt'])

    return Response(message_obj.to_json())


@api_view(['POST'])
@parser_classes((JSONParser,))
def send_location_message(request):
    """
    Send message to all in region
    {
        txt: required - max 255 characters
    }
    """

    validators.validate_api_location_message(request)
    user = request.user

    user_location = UserLocation.objects.filter(user=user).first()
    if not user_location:
        raise ValidationError({"detail": "User has not sent any location coordinates"})

    location_message_obj = LocationMessage.objects.create(
        txt=request.data['txt'],
        user=user,
        latitude=user_location.latitude,
        longitude=user_location.longitude
    )

    return Response(location_message_obj.to_json())


@api_view(['POST'])
@parser_classes((JSONParser,))
def send_faction_message(request):
    """
    Send message to all in faction
    {
        txt: required - max 255 characters
    }
    """

    validators.validate_api_location_message(request)
    user = request.user

    if not user.userprofile.faction:
        raise ValidationError({"detail": "User has not joined a faction"})

    faction_message_obj = FactionMessage.objects.create(
        txt=request.data['txt'],
        user=user,
        faction=user.userprofile.faction
    )

    return Response(faction_message_obj.to_json())


@api_view(['GET'])
def get_message_history(request):
    """
    Get the message history with a user
    Get Parameters:
        to_user: required - username string
        page: optional
    """

    validators.validate_exists_get(request, 'to_user')
    to_user = User.objects.filter(username=request.GET.get('to_user')).first()
    if not to_user:
        raise NotFound("User not found")

    user = request.user
    offset, limit = helpers.get_pagination_from_request(request)

    messages = Message.objects.filter(
        Q(from_user=user, to_user=to_user) | Q(from_user=to_user, to_user=user)
    ).order_by('-created_at')[offset:limit]

    message_list = []
    for message in messages:
        message_list.append(message.to_json())

    return Response(message_list)


# @api_view(['GET'])
# def get_location_message_history(request):
#     """
#     Get the message history with a user
#     Get Parameters:
#         to_user: required - username string
#         page: optional
#     """
#
#     validators.validate_exists_get(request, 'to_user')
#     to_user = User.objects.filter(username=request.GET.get('to_user')).first()
#     if not to_user:
#         raise NotFound("User not found")
#
#     user = request.user
#     offset, limit = helpers.get_pagination_from_request(request)
#
#     messages = Message.objects.filter(
#         Q(from_user=user, to_user=to_user) | Q(from_user=to_user, to_user=user)
#     ).order_by('-created_at')[offset:limit]
#
#     message_list = []
#     for message in messages:
#         message_list.append(message.to_json())
#
#     return Response(message_list)