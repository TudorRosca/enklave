# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0076_enklavesubscriber'),
    ]

    operations = [
        migrations.AlterField(
            model_name='crafteditem',
            name='energy',
            field=models.IntegerField(default=200),
        ),
    ]
