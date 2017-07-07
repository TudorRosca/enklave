# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0042_raiderposition'),
    ]

    operations = [
        migrations.AddField(
            model_name='raider2',
            name='hits_at',
            field=models.DateTimeField(default=None, null=True, blank=True),
        ),
    ]
