__author__ = 'radu'
from rest_framework.decorators import api_view, permission_classes, parser_classes
from rest_framework.response import Response
from rest_framework import status
from django.contrib.auth.models import User
from rest_framework.permissions import AllowAny
from oauth2_provider.models import Application
from rest_framework import serializers
from django.contrib.auth import get_user_model
from rest_framework.parsers import JSONParser
from backend.utils import helpers, validators
from backend.models import UserProfile
from oauth2_provider.models import AccessToken, RefreshToken
from django.utils import timezone
import datetime
from oauthlib.common import generate_token


# @api_view(['POST'])
# @permission_classes([AllowAny])
# def get_application(request):
#     app = Application.objects.create(
#         client_type=Application.CLIENT_CONFIDENTIAL,
#         authorization_grant_type=Application.GRANT_CLIENT_CREDENTIALS,
#     )
#
#     print app.client_id
#     print app.client_secret
#
#     return Response(status=status.HTTP_200_OK)


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()


@api_view(['POST'])
@permission_classes([AllowAny])
@parser_classes((JSONParser,))
def create_user(request):
    """
    Register User
    Posted Data:
        username (optional, if not sent the email will be used. it will not be returned by the backend)
        email
        password
        first_name - optional
        last_name - optional
    """
    validators.validate_create_user(request)
    user = User(email=request.data['email'], username=request.data['username'])

    if 'first_name' in request.data:
        user.first_name = request.data['first_name']

    if 'last_name' in request.data:
        user.last_name = request.data['last_name']

    user.set_password(request.data['password'])
    user.save()
    user.password = 'secret'

    user_profile = UserProfile.objects.get(user=user)
    user_data = user_profile.to_profile()

    app = Application.objects.first()

    token = generate_token()
    rf_tok = generate_token()

    access_token = AccessToken.objects.create(
        user=user,
        application=app,
        token=token,
        expires=timezone.now() + datetime.timedelta(days=10))

    refresh_token = RefreshToken.objects.create(
        user=user, application=app, token=rf_tok, access_token=access_token
    )

    user_data['access_token'] = access_token.token
    user_data['refresh_token'] = refresh_token.token
    user_data['expires_in'] = 36000

    return Response(data=user_data, status=status.HTTP_201_CREATED)
