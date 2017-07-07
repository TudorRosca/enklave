# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0078_auto_20160616_0656'),
    ]

    operations = [
        migrations.AddField(
            model_name='enklave',
            name='safe_until',
            field=models.DateTimeField(default=None, null=True, blank=True),
        ),
    ]
