# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0028_country'),
    ]

    operations = [
        migrations.AddField(
            model_name='enklave',
            name='enabled',
            field=models.BooleanField(default=True),
        ),
        migrations.AddField(
            model_name='enklave',
            name='faction',
            field=models.ForeignKey(default=None, blank=True, to='backend.Faction', null=True),
        ),
    ]
