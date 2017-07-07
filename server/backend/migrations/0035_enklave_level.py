# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0034_auto_20160316_1144'),
    ]

    operations = [
        migrations.AddField(
            model_name='enklave',
            name='level',
            field=models.IntegerField(default=0),
        ),
    ]
