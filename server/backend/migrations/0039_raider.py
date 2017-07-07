# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0038_resetpasswordtoken'),
    ]

    operations = [
        # migrations.CreateModel(
        #     name='Raider',
        #     fields=[
        #         ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
        #         ('latitude', models.FloatField()),
        #         ('longitude', models.FloatField()),
        #         ('bearing', models.SmallIntegerField(default=100)),
        #         ('level', models.IntegerField(default=0)),
        #         ('energy', models.IntegerField(default=0)),
        #         ('status', models.SmallIntegerField(default=0)),
        #         ('created_at', models.DateTimeField(auto_now_add=True)),
        #         ('updated_at', models.DateTimeField(auto_now=True)),
        #         ('deleted_at', models.DateTimeField(default=None, null=True, blank=True)),
        #         ('enklave', models.ForeignKey(to='backend.Enklave', db_constraint=False)),
        #     ],
        # ),
    ]
