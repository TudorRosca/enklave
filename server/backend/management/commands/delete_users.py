__author__ = 'radu'
from django.core.management.base import BaseCommand, CommandError
from django.db import connections
from backend.models import *


class Command(BaseCommand):
    help = 'Import Config'

    def handle(self, *args, **options):
        User.objects.exclude(username__in=['admin', 'adrian', 'radu11', 'radu', 'radu1']).delete()
