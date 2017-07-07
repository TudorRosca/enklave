# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0052_enklave_shield'),
    ]

    operations = [
        migrations.AddField(
            model_name='usercrafting',
            name='next_cell_at',
            field=models.DateTimeField(default=None, null=True, blank=True),
        ),
        migrations.AddField(
            model_name='usercrafting',
            name='nr_cells',
            field=models.IntegerField(default=0),
        ),
    ]
