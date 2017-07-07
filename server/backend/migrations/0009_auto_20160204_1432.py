# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('backend', '0008_auto_20160204_1429'),
    ]

    operations = [
        # migrations.AddField(
        #     model_name='notification',
        #     name='user',
        #     field=models.ForeignKey(to=settings.AUTH_USER_MODEL, null=True),
        # ),
        # migrations.AddField(
        #     model_name='notification',
        #     name='verb',
        #     field=models.CharField(default=b'achieved', max_length=20, null=True),
        # ),
    ]
