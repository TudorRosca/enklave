# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0058_auto_20160407_1412'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userprofile',
            name='distance_walked',
            field=models.IntegerField(default=0, null=True, blank=True),
        ),
        migrations.AlterField(
            model_name='userprofile',
            name='experience',
            field=models.IntegerField(default=0, null=True, blank=True),
        ),
        migrations.AlterField(
            model_name='userprofile',
            name='merit',
            field=models.IntegerField(default=0, null=True, blank=True),
        ),
        migrations.AlterField(
            model_name='userprofile',
            name='scrap',
            field=models.IntegerField(default=0, null=True, blank=True),
        ),
    ]
