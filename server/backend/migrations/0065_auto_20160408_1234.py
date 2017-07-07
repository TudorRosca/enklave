# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0064_auto_20160408_1158'),
    ]

    operations = [
        migrations.AlterField(
            model_name='usercrafting',
            name='current_action_type',
            field=models.IntegerField(default=1, choices=[(1, b'Craft Brick'), (2, b'Craft Cell'), (3, b'Use Cell'), (4, b'Craft Turret'), (4, b'Craft Shield')]),
        ),
    ]
