__author__ = 'radu'
from django.core.management.base import BaseCommand, CommandError
from django.db import connections
from backend.models import *


class Command(BaseCommand):
    help = 'Import Config'

    def handle(self, *args, **options):
        print 'Import Config'
        country = Country.objects.first()
        if country:
            print "The app already has values for the Country!"
            return

        select_query = """select * from country"""
        cursor = connections['default_readonly'].cursor()
        cursor.execute(select_query)
        rows = cursor.fetchall()

        country_list = []
        for row in rows:
            country_list.append(AppConfig(name=row[1]))
            print row

        Country.objects.bulk_create(country_list)
        print "done"