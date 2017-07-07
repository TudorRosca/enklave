# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0071_raidercombat_last_processed_at'),
    ]

    operations = [
        migrations.AddField(
            model_name='enklave',
            name='destroyed_at',
            field=models.DateTimeField(default=None, null=True, blank=True),
        ),
    ]
