# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0030_enklaveimage'),
    ]

    operations = [
        migrations.AlterField(
            model_name='enklave',
            name='user',
            field=models.ForeignKey(db_constraint=False, default=None, blank=True, to=settings.AUTH_USER_MODEL, null=True),
        ),
    ]
