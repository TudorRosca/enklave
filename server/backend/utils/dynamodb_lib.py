__author__ = 'radu'

import boto.dynamodb2
from boto.dynamodb2.fields import HashKey, RangeKey, KeysOnlyIndex, GlobalAllIndex
from boto.dynamodb2.table import Table
from boto.dynamodb2.types import NUMBER
from django.conf import settings


def add_coordinates(user, coordinate_data, created_at):
    if settings.TESTING:
        return

    # pragma: no cover
    conn = boto.dynamodb2.connect_to_region(
        'us-west-2',
        aws_access_key_id=settings.AWS_ACCESS_KEY_ID,
        aws_secret_access_key=settings.AWS_SECRET_ACCESS_KEY
    )

    coordinates = Table('coordinates', connection=conn)
    coordinates.put_item(data={
        'user_id': user.id,
        'coordinate_data': coordinate_data,
        'created_at': int(created_at)
    })