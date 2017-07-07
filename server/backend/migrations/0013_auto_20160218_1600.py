# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0012_notification'),
    ]

    operations = [
        migrations.AlterField(
            model_name='usernotification',
            name='sender',
            field=models.ForeignKey(related_name='usernotification_sender', db_constraint=False, default=None, blank=True, to=settings.AUTH_USER_MODEL, null=True),
        ),
    ]
