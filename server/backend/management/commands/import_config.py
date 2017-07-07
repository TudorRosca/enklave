__author__ = 'radu'
from django.core.management.base import BaseCommand, CommandError
from django.db import connections
from backend.models import *


class Command(BaseCommand):
    help = 'Import Config'

    def handle(self, *args, **options):
        app_config = AppConfig.objects.first()
        if app_config:
            print "The app already has values for the AppConfig!"
            return

        select_query = """select * from app_config"""
        cursor = connections['default_readonly'].cursor()
        cursor.execute(select_query)
        rows = cursor.fetchall()

        app_config_list = []
        for row in rows:
            app_config_list.append(AppConfig(name=row[1], value=row[2], description=row[3]))
            print row

        AppConfig.objects.bulk_create(app_config_list)
