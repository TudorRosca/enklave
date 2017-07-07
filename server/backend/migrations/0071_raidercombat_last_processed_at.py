# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0070_combatant_raidercombat_raidercombatraider_raidercombatuser'),
    ]

    operations = [
        migrations.AddField(
            model_name='raidercombat',
            name='last_processed_at',
            field=models.DateTimeField(default=None, null=True, blank=True),
        ),
    ]
