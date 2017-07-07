# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0050_enklavecombatuser'),
    ]

    operations = [
        migrations.AlterField(
            model_name='enklavecombat',
            name='notes',
            field=models.CharField(default=None, max_length=255, null=True, blank=True),
        ),
    ]
