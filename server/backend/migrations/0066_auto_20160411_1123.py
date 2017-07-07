# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0065_auto_20160408_1234'),
    ]

    operations = [
        migrations.AddField(
            model_name='usercrafting',
            name='current_action_enklave',
            field=models.ForeignKey(db_constraint=False, default=None, blank=True, to='backend.Enklave', null=True),
        ),
        migrations.AlterField(
            model_name='usercrafting',
            name='current_action_type',
            field=models.IntegerField(default=1, choices=[(1, b'Craft Brick'), (2, b'Craft Cell'), (3, b'Use Cell'), (4, b'Craft Turret'), (5, b'Craft Shield')]),
        ),
    ]
