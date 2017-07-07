__author__ = 'radu'
from django.core.management.base import BaseCommand, CommandError
from backend.models import *
import random
import math
import geopy
from geopy.distance import VincentyDistance
from math import radians, cos, sin, asin, sqrt
from datetime import timedelta
from django.utils import timezone

radius = 100
num_hours = 12
hits_at_diff = 2


class Command(BaseCommand):
    help = 'Raid'

    def handle(self, *args, **options):
        raider_hits = Raider2.objects.filter(
            hits_at__lte=timezone.now()
        ).prefetch_related('enklave')

        enklave_list = []
        for raider_hit in raider_hits:
            # First hit turrets, then
            print 'hit'
            enklave_list.append(raider_hit.enklave)
            raider_hit.enklave.bricks -= 1  # maybe it's variable

        for enklave in enklave_list:
            if enklave.bricks < 0:
                enklave.bricks = 0
            enklave.save()

        enklaves = Enklave.objects.filter(bricks__gt=0)
        raider_list = []
        for enklave in enklaves:
            bearing = 2 * math.pi * random.random()  # angle
            origin = geopy.Point(enklave.latitude, enklave.longitude)
            destination = VincentyDistance(kilometers=radius).destination(origin, bearing)

            lat2, lon2 = destination.latitude, destination.longitude

            raider_list.append(
                Raider2(
                    enklave=enklave,
                    level=1,
                    energy=1,
                    latitude=lat2,
                    longitude=lon2,
                    bearing=bearing,
                    hits_at=timezone.now() + timedelta(hours=(num_hours - 1 + hits_at_diff))
                )
            )

        raiders = Raider2.objects.bulk_create(raider_list)
        raider_positions = []
        for raider in raiders:
            for idx in range(0, num_hours):
                distance_moved = float(radius/num_hours)
                new_radius = radius - idx * distance_moved
                origin = geopy.Point(raider.enklave.latitude, raider.enklave.longitude)
                destination = VincentyDistance(kilometers=new_radius).destination(origin, raider.bearing)

                if idx == num_hours-1:
                    ends_at = raider.hits_at
                else:
                    ends_at = raider.created_at + (idx + 1) * timedelta(hours=1)

                raider_positions.append(
                    RaiderPosition(
                        raider=raider,
                        latitude=destination.latitude,
                        longitude=destination.longitude,
                        starts_at=raider.created_at + idx * timedelta(hours=1),
                        ends_at=ends_at
                    )
                )

        RaiderPosition.objects.bulk_create(raider_positions)

        Raider2.objects.filter(hits_at__lte=timezone.now() - timedelta(hours=2)).delete()

        print "raid"


def calc_dist(lon1, lat1, lon2, lat2):
    """
    Calculate the great circle distance between two points
    on the earth (specified in decimal degrees)
    """
    # convert decimal degrees to radians
    lon1, lat1, lon2, lat2 = map(radians, [float(lon1), float(lat1), float(lon2), float(lat2)])
    # haversine formula
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
    c = 2 * asin(sqrt(a))
    km = 6367 * c
    return km

