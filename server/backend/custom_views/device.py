__author__ = 'radu'

from rest_framework.decorators import api_view, parser_classes
from rest_framework.response import Response
from rest_framework import status
from backend.models import UserDevice
from backend.utils import validators, helpers
from rest_framework.parsers import JSONParser
from rest_framework.exceptions import NotFound


@api_view(['POST'])
@parser_classes((JSONParser,))
def update_device(request):
    """
    Register user device
    Headers: Authorization: Bearer {access_token}

    Post Data:
        device_id - the device id (imei)
        app_version - the version of the app on the device

    Request Example:
    {
        "device_id": "dasd3ad2easd",
        "app_version": "1.7.4"
    }
    """

    user = request.user
    validators.validate_update_device(request)

    device, created = UserDevice.objects.get_or_create(user=user, device_id=request.data['device_id'])
    device.app_version = request.data['app_version']
    device.save()

    return Response(device.to_json())


@api_view(['GET'])
def get_devices(request):
    """
    Get user devices
    Headers: Authorization: Bearer {access_token}
    Response Example:
    application/json
    [
      {
        "app_version": "1.7.4",
        "device_id": "dasd3ad2easd",
        "id": 1,
        "arn": null,
        "user": {
          "username": "radu",
          "id": 6
        }
      },
      ...
    ]
    """

    user = request.user
    device_list = []
    for device in user.userdevice_set.all():
        device_list.append(device.to_json())

    return Response(device_list)


@api_view(['DELETE'])
def delete_device(request):
    """
    Register user device
    Headers: Authorization: Bearer {access_token}

    Get Parameter:
        device_id
    """

    user = request.user
    validators.validate_exists_get(request, 'device_id')

    device = user.userdevice_set.filter(id=request.GET.get('device_id')).first()
    if not device:
        raise NotFound('Device not found')

    device.delete()

    return Response(status=status.HTTP_204_NO_CONTENT)