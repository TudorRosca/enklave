__author__ = 'radu'
from django.core.management.base import BaseCommand, CommandError
from django.db import connections
from backend.models import *


def dictfetchall(cursor):
    "Return all rows from a cursor as a dict"
    columns = [col[0] for col in cursor.description]
    return [
        dict(zip(columns, row))
        for row in cursor.fetchall()
    ]


class Command(BaseCommand):
    help = 'Import Users'

    def handle(self, *args, **options):
        print 'Import Users'
        users = User.objects.count()
        if users > 20:
            print "The app already has values for the Users!"
            return

        select_query = """select * from user"""
        cursor = connections['default_readonly'].cursor()
        cursor.execute(select_query)
        rows = dictfetchall(cursor)

        for row in rows:
            print row

            user = User.objects.create(username=row['username'], email=row['email'], first_name=row['first_name'],
                                       last_name=row['last_name'])

            user_profile = UserProfile.objects.filter(user=user).first()
            if user_profile:
                user_profile.faction_id = row['faction_id'] or 1
                user_profile.locked = row['locked'] or False
                user_profile.has_app_access = row['has_app_access'] or False
                user_profile.save()

        #     enklave = Enklave.objects.create(
        #         name=row['name'],
        #         latitude=row['latitude'],
        #         longitude=row['longitude'],
        #         scrap=row['scrap'] or 0,
        #         bricks=row['bricks'] or 0,
        #         level=row['level'] or 0,
        #         last_production_at=row['last_production'],
        #         enabled=False if row['disabled'] else True,
        #         created_at=row['created_at'],
        #         updated_at=row['updated_at'],
        #         faction_id=row['faction_id'],
        #     )
        #
        #     if row['original_filename']:
        #         EnklaveImage.objects.create(image_url=row['original_filename'], enklave=enklave)
        #
        # print "done"