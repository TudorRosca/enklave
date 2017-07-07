__author__ = 'radu'
from django.core.management.base import BaseCommand, CommandError
from django.db import connections
from backend.models import *
from django.contrib.auth.models import User


class Command(BaseCommand):
    help = 'Update User Data'

    def handle(self, *args, **options):
        print 'Update User Data'
        users = User.objects.all()
        for user in users:
            UserProfile.objects.get_or_create(user=user)
            UserSettings.objects.get_or_create(user=user)
            UserCrafting.objects.get_or_create(user=user)
        print "done"