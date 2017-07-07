# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0051_auto_20160323_1048'),
    ]

    operations = [
        migrations.AddField(
            model_name='enklave',
            name='shield',
            field=models.IntegerField(default=0),
        ),
    ]
