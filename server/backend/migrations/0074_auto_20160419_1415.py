# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0073_auto_20160419_1402'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userprofile',
            name='distance_walked',
            field=models.FloatField(default=0, null=True, blank=True),
        ),
    ]
