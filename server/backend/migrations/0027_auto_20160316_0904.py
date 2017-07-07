# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0026_appconfig'),
    ]

    operations = [
        migrations.AlterField(
            model_name='appconfig',
            name='description',
            field=models.CharField(default=None, max_length=255, null=True, blank=True),
        ),
    ]
