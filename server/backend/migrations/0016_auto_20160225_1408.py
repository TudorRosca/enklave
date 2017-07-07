# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0015_auto_20160225_1357'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userconnection',
            name='user',
            field=models.ForeignKey(to=settings.AUTH_USER_MODEL, db_constraint=False),
        ),
    ]
