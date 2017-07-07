# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0036_usersettings'),
    ]

    operations = [
        migrations.AddField(
            model_name='userprofile',
            name='country',
            field=models.ForeignKey(db_constraint=False, default=None, blank=True, to='backend.Country', null=True),
        ),
        migrations.AddField(
            model_name='userprofile',
            name='faction',
            field=models.ForeignKey(db_constraint=False, default=None, blank=True, to='backend.Faction', null=True),
        ),
        migrations.AddField(
            model_name='userprofile',
            name='has_app_access',
            field=models.BooleanField(default=False),
        ),
        migrations.AddField(
            model_name='userprofile',
            name='locked',
            field=models.BooleanField(default=False),
        ),
    ]
