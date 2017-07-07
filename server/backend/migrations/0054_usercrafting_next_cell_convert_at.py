# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0053_auto_20160404_0941'),
    ]

    operations = [
        migrations.AddField(
            model_name='usercrafting',
            name='next_cell_convert_at',
            field=models.DateTimeField(default=None, null=True, blank=True),
        ),
    ]
