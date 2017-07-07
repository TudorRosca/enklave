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
    help = 'Import Enklave'

    def handle(self, *args, **options):
        print 'Import Enklave'
        enklave = Enklave.objects.count()
        if enklave > 10:
            print "The app already has values for the Enklaves!"
            return

        select_query = """select img.original_filename, e.*, p.scrap, p.level, p.bricks, p.last_production
        from enklave e left join upload_image img on img.id = e.image_id
        left join enklave_property p on p.enklave_id = e.id"""
        cursor = connections['default_readonly'].cursor()
        cursor.execute(select_query)
        rows = dictfetchall(cursor)

        for row in rows:

            enklave = Enklave.objects.create(
                name=row['name'],
                latitude=row['latitude'],
                longitude=row['longitude'],
                scrap=row['scrap'] or 0,
                bricks=row['bricks'] or 0,
                level=row['level'] or 0,
                last_production_at=row['last_production'],
                enabled=False if row['disabled'] else True,
                created_at=row['created_at'],
                updated_at=row['updated_at'],
                faction_id=row['faction_id'],
            )

            if row['original_filename']:
                EnklaveImage.objects.create(image_url=row['original_filename'], enklave=enklave)

        print "done"