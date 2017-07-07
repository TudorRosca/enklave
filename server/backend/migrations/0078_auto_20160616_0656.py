# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0077_auto_20160607_1326'),
    ]

    operations = [
        migrations.AlterField(
            model_name='crafteditem',
            name='energy',
            field=models.IntegerField(default=350),
        ),
    ]
