__author__ = 'radu'

from django.utils.translation import gettext as _
from rest_framework.exceptions import ValidationError, PermissionDenied
import json
from django.contrib.auth.models import User
from django import forms
from django.core.exceptions import ValidationError as DefaultValidationError
import strict_rfc3339
import jsonschema
from uuid import UUID


def validate_schema(json_schema, request_json):
    try:
        jsonschema.validate(request_json, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError({"detail": e.message})
    except jsonschema.SchemaError as e:
        raise ValidationError({"detail": e.message})


def validate_details_enklave(request):
    validate_exists_get(request, 'enklave_id')
    enklave_id = request.GET.get('enklave_id')
    try:
        int(enklave_id)
    except ValueError:
        raise ValidationError({"detail": "Field not an integer: enklave_id"})


def validate_get_enklaves(request):
    validate_exists_get(request, 'lat')
    validate_exists_get(request, 'lon')

    lat = request.GET.get('lat')
    lon = request.GET.get('lon')

    try:
        lat = float(lat)
    except ValueError:
        raise ValidationError({"detail": "lat is not a correct float"})

    try:
        lon = float(lon)
    except ValueError:
        raise ValidationError({"detail": "lon is not a correct float"})

    if lat < -90 or lat > 90:
        raise ValidationError({"detail": "lat must be between -90 and 90"})

    if lon < -180 or lon > 180:
        raise ValidationError({"detail": "lat must be between -180 and 180"})


def validate_coordinates_socket(coordinate_string):
    coordinates = coordinate_string.split(",")
    if len(coordinates) != 2:
        return False

    lat, lon = coordinate_string.split(",")
    try:
        lat = float(lat)
    except ValueError:
        return False

    try:
        lon = float(lon)
    except ValueError:
        return False

    if lat < -90 or lat > 90:
        return False

    if lon < -180 or lon > 180:
        return False


def validate_create_user(request):
    if 'username' not in request.data:
        request.data['username'] = request.data['email']

    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "username": {
          "required":true,
          "type":"string",
          "minLength": 2,
          "maxLength": 255
        },
        "email": {
          "required":true,
          "type":"string",
          "format": "email"
        },
        "password": {
          "required":true,
          "type":"string",
          "minLength": 2,
          "maxLength": 255
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)

    users = User.objects.filter(email=request.data['email'])
    if len(users) > 0:
        raise ValidationError(_('Field must be unique: email'))

    users = User.objects.filter(username=request.data['username'])
    if len(users) > 0:
        raise ValidationError(_('Field must be unique: username'))


def validate_install_turret(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "enklave_id": {
          "required":true,
          "type":"integer"
        },
        "turret_id": {
          "required":true,
          "type":"integer"
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_install_shield(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "enklave_id": {
          "required":true,
          "type":"integer"
        },
        "shield_id": {
          "required":true,
          "type":"integer"
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_place_brick(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "enklave_id": {
          "required":true,
          "type":"integer"
        },
        "brick_id": {
          "required":false,
          "type":"integer"
        }
      }
    }
    """

    validate_schema(json_schema, request.data)


def validate_use_cell(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "cell_id": {
          "required":true,
          "type":"integer"
        }
      }
    }
    """

    validate_schema(json_schema, request.data)


def validate_create_enklave(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "latitude": {
          "required":true,
          "type":"number",
          "minimum": -90,
          "maximum": 90
        },
        "longitude": {
          "required":true,
          "type":"number",
          "minimum": -180,
          "maximum": 180
        },
        "name": {
          "required":false,
          "type":"string",
          "minLength": 2,
          "maxLength": 150
        },
        "description":{
          "required":false,
          "type":"string",
          "minLength": 2,
          "maxLength": 255
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_subscribe_to_enklave_combat(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "enklave_id": {
          "required":true,
          "type":"integer"
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_attack_enklave_hit_user(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "enklave_combatant_id": {
          "required":true,
          "type":"integer"
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_attack_enklave_hit_enklave(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "enklave_combat_id": {
          "required":true,
          "type":"integer"
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_update_device(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "device_id": {
          "required":true,
          "type":"string",
          "maxLength": 255
        },
        "app_version": {
          "required":true,
          "type":"string",
          "maxLength": 20
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_attack_enklave(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "enklave_id": {
          "required":true,
          "type":"integer"
        }
      }
    }
    """

    validate_schema(json_schema, request.data)


def validate_attack_raider(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "raider_id": {
          "required":true,
          "type":"string"
        }
      }
    }
    """

    validate_schema(json_schema, request.data)

    try:
        UUID(request.data['raider_id'], version=4)
    except ValueError:
        raise ValidationError({"detail": "Fiend is not a correctly formatted uuid: raider_id"})


def validate_join_combat(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "enklave_id": {
          "required":true,
          "type":"integer"
        },
        "type": {
          "required":true,
          "type":"integer",
          "enum": [1,2]
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_hit_opponent(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "opponent_id": {
          "required":true,
          "type":"integer"
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_exists(request, field):
    field_data = request.POST.get(field)
    if not field_data:
        raise ValidationError({'detail': 'Field is required: %s' % field})


def validate_exists_get(request, field):
    field_data = request.GET.get(field)
    if not field_data:
        raise ValidationError({'detail': 'Field is required: %s' % field})


def validate_exists_get_uuid(request, field):
    validate_exists_get(request, field)
    try:
        UUID(request.GET.get(field), version=4)
    except ValueError:
        raise ValidationError({'detail': 'Field is not a valid uuid hex: %s' % field})


def validate_exists_get_integer(request, field):
    validate_exists_get(request, field)
    try:
        int(request.GET.get(field))
    except ValueError:
        raise ValidationError({'detail': 'Field is not a valid integer: %s' % field})


def validate_string(request, field):
    field_data = request.POST.get(field)
    if field_data:
        try:
            str(field_data)
        except ValueError:
            raise ValidationError({'detail': 'Field is not a valid string: %s' % field})


def validate_max_size(request, field, max_size):
    field_data = request.POST.get(field)
    if field_data:
        if len(field_data) > max_size:
            raise ValidationError({'detail': 'Field cannot have more than {0} letters: {1}'.format(
                max_size, field
            )})


def validate_min_size(request, field, min_size):
    field_data = request.POST.get(field)
    if field_data:
        if len(field_data) < min_size:
            raise ValidationError({'detail': 'Field cannot have less than {0} letters: {1}'.format(
                min_size, field
            )})


def validate_int(request, field):
    field_data = request.POST.get(field)
    if field_data:
        try:
            int(field_data)
        except ValueError:
            raise ValidationError({'detail': 'Field is not a valid integer: %s' % field})


def validate_json_list_string(request, field):
    field_data = request.POST.get(field)
    if not field_data:
        return

    try:
        field_data = json.loads(field_data)
        for field_data_item in field_data:
            str(field_data_item)
    except ValueError:
        raise ValidationError({'detail': 'Field is not a valid json list of strings: %s' % field})


def validate_json_list_int(request, field):
    field_data = request.POST.get(field)
    if not field_data:
        return

    try:
        field_data = json.loads(field_data)
        for field_data_item in field_data:
            int(field_data_item)
    except ValueError:
        raise ValidationError({'detail': 'Field is not a valid json list of integers: %s' % field})


def validate_email(request, field):
    field_data = request.POST.get(field)
    if not field_data:
        return

    form = forms.EmailField()
    try:
        form.clean(field_data)
    except DefaultValidationError:
        raise ValidationError({'detail': 'Field is not a valid email address: email_address'})


def validate_exists_json(request, field):
    if field not in request.data:
        raise ValidationError('Field is required: {0}'.format(field))


def validate_message(message):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "params": {
          "required":true,
          "type":"object",
          "properties": {
            "txt": {
                "type": "string",
                "required":true,
                "maxLength":255
            },
            "to_user": {
                "type": "string",
                "required":true,
                "maxLength":255
            }
          }
        }
      }
    }
    """

    try:
        jsonschema.validate(message, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        return e.message
    except jsonschema.SchemaError as e:
        return e.message


def validate_coord_scrap(message):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "params": {
          "required":true,
          "type":"object",
          "properties": {
            "lat": {
                "type": "number",
                "required":true,
                "minimum":-90,
                "maximum":90
            },
            "long": {
                "type": "number",
                "required":true,
                "minimum":-180,
                "maximum":180
            }
          }
        }
      }
    }
    """

    try:
        jsonschema.validate(message, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        return e.message
    except jsonschema.SchemaError as e:
        return e.message


def validate_message_received(message):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "params": {
          "required":true,
          "type":"object",
          "properties": {
            "message_ids": {
                "type": "array",
                "required":true,
                "items":{
                    "type": "integer",
                    "required": true
                }
            }
          }
        }
      }
    }
    """

    try:
        jsonschema.validate(message, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        return e.message
    except jsonschema.SchemaError as e:
        return e.message


def validate_location_message(message):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "params": {
          "required":true,
          "type":"object",
          "properties": {
            "txt": {
                "type": "string",
                "required":true,
                "maxLength":255
            }
          }
        }
      }
    }
    """

    try:
        jsonschema.validate(message, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        return e.message
    except jsonschema.SchemaError as e:
        return e.message


def validate_forgot_password_json(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "email": {
          "required":true,
          "type":"string",
          "format": "email"
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError({"detail": str(e.message)})
    except jsonschema.SchemaError as e:
        print e


def validate_join_faction(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "faction_id": {
          "required":true,
          "type":"integer"
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError({"detail": str(e.message)})
    except jsonschema.SchemaError as e:
        print e


def validate_send_one_to_one_message(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "txt": {
            "type": "string",
            "required":true,
            "maxLength":255
        },
        "to_user": {
            "type": "string",
            "required":true,
            "maxLength":255
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)


def validate_api_location_message(request):
    json_schema = """
    {
      "type":"object",
      "$schema": "http://json-schema.org/draft-03/schema",
      "required":true,
      "properties":{
        "txt": {
            "type": "string",
            "required":true,
            "maxLength":255
        }
      }
    }
    """

    try:
        jsonschema.validate(request.data, json.loads(json_schema), format_checker=jsonschema.FormatChecker())
    except jsonschema.ValidationError as e:
        raise ValidationError(e.message)
    except jsonschema.SchemaError as e:
        raise ValidationError(e.message)