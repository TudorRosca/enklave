# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0033_homebase'),
    ]

    operations = [
        migrations.AddField(
            model_name='enklave',
            name='bricks',
            field=models.IntegerField(default=0),
        ),
        migrations.AddField(
            model_name='enklave',
            name='cells',
            field=models.IntegerField(default=0),
        ),
        migrations.AddField(
            model_name='enklave',
            name='last_production_at',
            field=models.DateTimeField(default=None, null=True, blank=True),
        ),
        migrations.AddField(
            model_name='enklave',
            name='scrap',
            field=models.IntegerField(default=0),
        ),
    ]
