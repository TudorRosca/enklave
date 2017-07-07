# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0072_enklave_destroyed_at'),
    ]

    operations = [
        migrations.AddField(
            model_name='userlocation',
            name='prev_latitude',
            field=models.FloatField(default=None, null=True, blank=True),
        ),
        migrations.AddField(
            model_name='userlocation',
            name='prev_longitude',
            field=models.FloatField(default=None, null=True, blank=True),
        ),
    ]
