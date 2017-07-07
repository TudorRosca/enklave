# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0024_faction'),
    ]

    operations = [
        migrations.AlterField(
            model_name='faction',
            name='logo',
            field=models.URLField(max_length=100),
        ),
    ]
