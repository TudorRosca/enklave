# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0016_auto_20160225_1408'),
    ]

    operations = [
        migrations.AddField(
            model_name='userprofile',
            name='distance_walked',
            field=models.IntegerField(default=None, null=True, blank=True),
        ),
        migrations.AddField(
            model_name='userprofile',
            name='merit',
            field=models.IntegerField(default=None, null=True, blank=True),
        ),
        migrations.AddField(
            model_name='userprofile',
            name='scrap',
            field=models.IntegerField(default=None, null=True, blank=True),
        ),
    ]
