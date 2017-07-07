# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0022_userlocation'),
    ]

    operations = [
        migrations.AddField(
            model_name='userprofile',
            name='energy',
            field=models.IntegerField(default=0, null=True, blank=True),
        ),
    ]
