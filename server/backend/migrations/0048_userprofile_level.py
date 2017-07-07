# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0047_userprofile_energy_calculated_at'),
    ]

    operations = [
        migrations.AddField(
            model_name='userprofile',
            name='level',
            field=models.IntegerField(default=1),
        ),
    ]
