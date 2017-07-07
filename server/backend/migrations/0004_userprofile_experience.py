# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0003_userdevice'),
    ]

    operations = [
        migrations.AddField(
            model_name='userprofile',
            name='experience',
            field=models.IntegerField(default=None, null=True, blank=True),
        ),
    ]
