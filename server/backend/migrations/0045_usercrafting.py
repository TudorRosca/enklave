# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('backend', '0044_factionmessage'),
    ]

    operations = [
        migrations.CreateModel(
            name='UserCrafting',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('nr_bricks', models.IntegerField(default=0)),
                ('next_brick_at', models.DateTimeField(default=None, null=True, blank=True)),
                ('user', models.OneToOneField(to=settings.AUTH_USER_MODEL, db_constraint=False)),
            ],
        ),
    ]
