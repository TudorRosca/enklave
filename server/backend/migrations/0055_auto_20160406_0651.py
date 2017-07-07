# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0054_usercrafting_next_cell_convert_at'),
    ]

    operations = [
        migrations.AlterField(
            model_name='enklave',
            name='latitude',
            field=models.FloatField(db_index=True),
        ),
        migrations.AlterField(
            model_name='enklave',
            name='longitude',
            field=models.FloatField(db_index=True),
        ),
    ]
