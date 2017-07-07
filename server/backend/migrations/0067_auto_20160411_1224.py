# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0066_auto_20160411_1123'),
    ]

    operations = [
        migrations.AddField(
            model_name='userprofile',
            name='scrap_calculated_at',
            field=models.DateTimeField(default=None, null=True, blank=True),
        ),
        migrations.AlterField(
            model_name='usercrafting',
            name='current_action_type',
            field=models.IntegerField(default=1, choices=[(1, b'Craft Brick'), (2, b'Craft Cell'), (3, b'Use Cell'), (4, b'Craft Turret'), (5, b'Craft Shield'), (6, b'Install Turret'), (7, b'Install Shield'), (8, b'Place Brick')]),
        ),
    ]
