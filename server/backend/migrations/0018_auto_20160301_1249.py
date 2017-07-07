# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0017_auto_20160226_0941'),
    ]

    operations = [
        migrations.RunSQL('create extension cube'),
        migrations.RunSQL('create extension earthdistance')
    ]
