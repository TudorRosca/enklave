# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0062_auto_20160408_1130'),
    ]

    operations = [
        migrations.AlterField(
            model_name='crafteditem',
            name='user',
            field=models.ForeignKey(related_name='backend_crafteditem_ownership', to=settings.AUTH_USER_MODEL, db_constraint=False),
        ),
    ]
