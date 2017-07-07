__author__ = 'radu'
from rest_framework.decorators import api_view, parser_classes, permission_classes
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import AllowAny
from backend.utils import validators, helpers
from rest_framework.parsers import JSONParser
from rest_framework.exceptions import NotFound
from django.contrib.auth.models import User
from backend.models import Faction
import logging
logger = logging.getLogger('api_exceptions')
from django.utils import timezone
from datetime import timedelta
from backend.utils.ses_lib import SesLib
import hashlib
from django.conf import settings
from django.shortcuts import render


@api_view(['POST'])
@parser_classes((JSONParser,))
def join_faction(request):
    """
    Join Faction
    Posted Data:
    application/json
    {
        faction_id: required
    }
    """

    # TODO test if has faction already and if has permission to change

    validators.validate_join_faction(request)
    faction = Faction.objects.get_or_404(request.data['faction_id'])

    user = request.user
    user.userprofile.faction = faction
    user.userprofile.save()

    return Response()


@api_view(['POST'])
@parser_classes((JSONParser,))
def leave_faction(request):
    """
    Leave Faction
    """

    user = request.user
    user.userprofile.faction = None
    user.userprofile.save()

    return Response()


@api_view(['GET'])
def get_factions(request):
    """
    Get Factions
    """

    factions = Faction.objects.all().order_by('display_order')
    faction_array = []
    for faction in factions:
        faction_array.append(faction.to_json())

    return Response(faction_array)
