# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0059_auto_20160408_1009'),
    ]

    operations = [
        migrations.AlterField(
            model_name='turret',
            name='enklave',
            field=models.ForeignKey(db_constraint=False, default=None, blank=True, to='backend.Enklave', null=True),
        ),
    ]
