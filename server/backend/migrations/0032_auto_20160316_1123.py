# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0031_auto_20160316_1117'),
    ]

    operations = [
        migrations.AlterField(
            model_name='enklaveimage',
            name='image_url',
            field=models.URLField(max_length=255),
        ),
    ]
