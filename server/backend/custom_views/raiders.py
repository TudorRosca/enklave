__author__ = 'radu'
from rest_framework.decorators import api_view
from rest_framework.response import Response
from backend.models import RaiderPosition, Raider2, UserLocation
from backend.utils import validators
from django.utils import timezone
from rest_framework.exceptions import NotFound
from math import cos, fabs
from rest_framework.exceptions import ValidationError


@api_view(['GET'])
def get_nearby_raiders(request):
    """
    Get nearby Raiders
    Headers: Authorization: Bearer {access_token}
    """

    user = request.user
    user_location = UserLocation.objects.filter(user=user).first()
    if not user_location:
        raise ValidationError({"detail": "User has not sent location data"})

    distance_lat = (1 / 110.574) * (1.5 / 2)   # degree to kilometer times 1.5 divided by 2
    distance_lon = fabs((1 / (111.320*cos(user_location.latitude))) * (1.5 / 2))

    lat_range = (user_location.latitude - distance_lat, user_location.latitude + distance_lat)
    lon_range = (user_location.longitude - distance_lon, user_location.longitude + distance_lon)

    raider_positions = RaiderPosition.objects\
        .filter(
            latitude__range=lat_range,
            longitude__range=lon_range,
            starts_at__lt=timezone.now(),
            ends_at__gte=timezone.now()
        )\
        .prefetch_related('raider', 'raider__enklave')

    raider_positions_list = []

    for raider_position in raider_positions:
        raider_data = raider_position.raider.to_json()
        raider_data['current_latitude'] = raider_position.latitude
        raider_data['current_longitude'] = raider_position.longitude
        raider_positions_list.append(raider_data)

    return Response(data=raider_positions_list)


@api_view(['GET'])
def get_raiders_for_enklave(request):
    """
    Get nearby Raiders
    Headers: Authorization: Bearer {access_token}
    Get Parameter:
        enklave_id - int (mandatory)
    """

    validators.validate_exists_get_integer(request, 'enklave_id')

    raiders = Raider2.objects.filter(enklave_id=request.GET.get('enklave_id'))\
        .extra(select={"current_latitude": """
            select rdr1.latitude from backend_raiderposition rdr1
            where rdr1.raider_id=backend_raider2.id
                and rdr1.starts_at<'{0}' and rdr1.ends_at>='{0}'""".format(timezone.now())}) \
        .extra(select={"current_longitude": """
            select rdr1.longitude from backend_raiderposition rdr1
            where rdr1.raider_id=backend_raider2.id
                and rdr1.starts_at<'{0}' and rdr1.ends_at>='{0}'""".format(timezone.now())})

    raider_list = []
    for raider in raiders:
        raider_data = raider.to_json()
        raider_data['current_latitude'] = raider.current_latitude
        raider_data['current_longitude'] = raider.current_longitude
        raider_list.append(raider_data)

    return Response(data=raider_list)


@api_view(['GET'])
def get_raiders_by_id(request):
    """
    Get nearby Raiders
    Headers: Authorization: Bearer {access_token}
    Get Parameter:
        enklave_id - int (mandatory)
    """

    validators.validate_exists_get_uuid(request, 'raider_id')

    raider = Raider2.objects.filter(id=request.GET.get('raider_id')) \
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

    raider_data = raider.to_json()
    raider_data['current_latitude'] = raider.current_latitude
    raider_data['current_longitude'] = raider.current_longitude

    return Response(data=raider_data)
