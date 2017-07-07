__author__ = 'radu'

from rest_framework.decorators import api_view, parser_classes, permission_classes
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import AllowAny
from backend.utils import validators, helpers
from rest_framework.parsers import JSONParser
from rest_framework.exceptions import NotFound
from django.contrib.auth.models import User
from backend.models import ResetPasswordToken
import logging
logger = logging.getLogger('api_exceptions')
from django.utils import timezone
from datetime import timedelta
from backend.utils.ses_lib import SesLib
import hashlib
from django.conf import settings
from django.shortcuts import render
from backend.utils import crafting_lib, config_lib
from backend.models import Brick, Cell, Turret, Shield


@api_view(['GET'])
def get_session_user_data(request):
    """
    Get session user data
    Headers: Authorization: Bearer {access_token}
    """

    user = request.user
    crafting_lib.update_user_energy(user)
    crafting_lib.update_user_crafting(user)

    user_data = user.userprofile.to_profile()

    energy_config_data = config_lib.get_energy_config_for_user(user)
    user_data['energy_config'] = energy_config_data
    brick_config_data = config_lib.get_craft_brick_configs()
    user_data['brick_config'] = brick_config_data
    max_xp_for_user = config_lib.get_max_xp_for_user(user)
    user_data['xp_for_level'] = max_xp_for_user
    user_data['turret_config'] = config_lib.get_craft_turret_configs()
    user_data['shield_config'] = config_lib.get_craft_shield_configs()
    user_data['cell_configs'] = config_lib.get_craft_cell_configs()
    user_data['place_brick_config'] = config_lib.get_place_brick_config()
    user_data['attack_config'] = config_lib.get_attack_config_for_user(user)

    user_data['nr_bricks'] = user.usercrafting.nr_bricks or 0
    user_data['nr_cells'] = user.usercrafting.nr_cells or 0

    user_data['nr_bricks'] = Brick.objects.filter(
        crafteditem_ptr__user=user,
        crafteditem_ptr__enklave__isnull=True,
        crafteditem_ptr__used_at__isnull=True
    ).count()

    user_data['nr_cells'] = Cell.objects.filter(
        crafteditem_ptr__user=user,
        crafteditem_ptr__enklave__isnull=True,
        crafteditem_ptr__used_at__isnull=True
    ).count()

    user_data['nr_turrets'] = Turret.objects.filter(
        crafteditem_ptr__user=user,
        crafteditem_ptr__enklave__isnull=True,
        crafteditem_ptr__used_at__isnull=True
    ).count()

    user_data['nr_shields'] = Shield.objects.filter(
        crafteditem_ptr__user=user,
        crafteditem_ptr__enklave__isnull=True,
        crafteditem_ptr__used_at__isnull=True
    ).count()

    return Response(data=user_data)


@api_view(['GET'])
def get_user_public_data(request):
    """
    Get user data by id
    Headers: Authorization: Bearer {access_token}
    Get Parameter:
        user_id
    """

    validators.validate_exists_get_integer(request, 'user_id')

    try:
        user = User.objects.get(pk=request.GET.get('user_id'))
    except User.DoesNotExist:
        raise NotFound('User not found')

    user_data = helpers.format_user_public_data(user, user.userprofile)

    return Response(data=user_data)


@api_view(['GET'])
def get_user_stats(request):
    """
    Get session user stats
    Headers: Authorization: Bearer {access_token}
    Get Parameter:
        user_id
    """

    user = request.user

    user_data = helpers.format_user_stats(user, user.userprofile)

    return Response(data=user_data)


@api_view(['POST'])
@permission_classes([AllowAny])
def forgot_password(request):
    """
    Forgot user password <br>
    Send message to email address with the password reset token <br>
    Posted Data:
    application/json
    {
        email: required
    }
    """
    validators.validate_forgot_password_json(request)

    try:
        user = User.objects.get(email=request.data['email'])
    except User.DoesNotExist:
        raise NotFound("Email Address not found")

    if not user.is_active:
        raise NotFound("Email inactive")

    m = hashlib.md5()
    m.update(user.username + '_forgotpw_' + str(user.date_joined) + str(timezone.now()))
    token = m.hexdigest()

    renew_token = ResetPasswordToken(user=user, token=token)
    renew_token.expire_date = timezone.now() + timezone.timedelta(days=1)
    renew_token.save()

    url = settings.API_URL + 'reset_password?token=%s' % renew_token.token
    email = SesLib(to=request.data['email'], subject='Password Reset Request for Enklave')

    ctx = {
        'link': url
    }
    email.html('email.html', ctx)
    if settings.TESTING:
        return Response()
    else:  # pragma: no cover
        try:
            email.send()
        except Exception as e:
            logger.exception('\n%s send emai error %s' % (timezone.now(), e))
            return Response(data={'error': 'can not send to that email'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

        return Response()


def reset_password(request):
    if request.POST:
        errors = []
        password = request.POST.get('password')
        if not password:
            errors.append('You must set the password field')
        password_repeat = request.POST.get('password_repeat')
        if not password_repeat:
            errors.append('You must set the repeat password field')
        token_code = request.POST.get('token_code')
        token = ResetPasswordToken.objects.filter(token=token_code, expire_date__gte=timezone.now()).first()
        if token is None:
            return render(request, 'bad_token.html', {})

        username = token.user.username
        if len(errors) > 0:
            return render(request, 'reset_password.html', {
                'errors': errors, 'token_code': token_code, 'username': username})

        if password != password_repeat:
            errors.append("The two passwords didn't match!")
        if len(password.strip()) <= 0:
            errors.append('The password cannot be empty!')
        if len(password.strip()) < 3:
            errors.append('The password was too short!')
        if len(errors) > 0:
            return render(request, 'reset_password.html', {
                'errors': errors, 'token_code': token_code, 'username': username})

        user = token.user
        user.set_password(password)
        user.save()

        token.delete()

        return render(request, 'reset_successful.html', {})

    if 'token' not in request.GET:
        return render(request, 'no_token.html', {})

    token_code = request.GET['token']
    token = ResetPasswordToken.objects.filter(token=token_code, expire_date__gte=timezone.now()).first()
    if token is None:
        return render(request, 'bad_token.html', {})

    username = token.user.username

    return render(request, 'reset_password.html', {'token_code': token_code, 'username': username})