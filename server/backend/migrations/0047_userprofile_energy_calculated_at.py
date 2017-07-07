# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0046_auto_20160317_1542'),
    ]

    operations = [
        migrations.AddField(
            model_name='userprofile',
            name='energy_calculated_at',
            field=models.DateTimeField(default=None, null=True, blank=True),
        ),
    ]
