__author__ = 'radu'

from rest_framework.decorators import api_view, parser_classes
from rest_framework.response import Response
from backend.utils.redis_lib import RedisLib
from django.utils import timezone
from datetime import timedelta
import hashlib
import string
import random
from rest_framework.parsers import JSONParser


@api_view(['POST'])
@parser_classes((JSONParser,))
def get_ticket(request):
    """
    Get Websocket Ticket
    Headers: Authorization: Bearer {access_token}

    Response Example:
    {
      "ip": "127.0.0.1",
      "ticket": "6d900f21358859c15de34a1d9aafd2cb",
      "user_id": 6,
      "expires_at": "2016-02-19 13:52:22.599460+00:00"
    }
    """

    user = request.user

    m = hashlib.md5()
    random_tok = ''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(5))
    m.update(user.username + '_ticket_' + str(timezone.now()) + random_tok)
    ticket = m.hexdigest()

    data = {
        "user_id": user.id,
        "expires_at": str(timezone.now() + timedelta(days=1)),
        "ip": get_client_ip(request),
        "ticket": ticket
    }

    redis_lib = RedisLib()
    redis_lib.set_ticket(ticket, data)

    return Response(data)


def get_client_ip(request):
    x_forwarded_for = request.META.get('HTTP_X_FORWARDED_FOR')
    if x_forwarded_for:
        ip = x_forwarded_for.split(',')[0]
    else:
        ip = request.META.get('REMOTE_ADDR')
    return ip


