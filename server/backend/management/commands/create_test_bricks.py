__author__ = 'radu'
from django.core.management.base import BaseCommand, CommandError
from django.db import connections
from backend.models import *


class Command(BaseCommand):
    help = 'Create Test Bricks'

    def handle(self, *args, **options):
        print 'Create Test Bricks'
        try:
            enklave = Enklave.objects.get(pk=16066)
        except Enklave.DoesNotExist:
            print 'Not Found'
            return

        for i in range(0, 80):
            Brick.objects.create(enklave=enklave, user=enklave.user)
