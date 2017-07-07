# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
import uuid


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0041_raider2'),
    ]

    operations = [
        migrations.CreateModel(
            name='RaiderPosition',
            fields=[
                ('id', models.UUIDField(default=uuid.uuid4, serialize=False, editable=False, primary_key=True)),
                ('latitude', models.FloatField()),
                ('longitude', models.FloatField()),
                ('starts_at', models.DateTimeField(db_index=True)),
                ('ends_at', models.DateTimeField(db_index=True)),
                ('raider', models.ForeignKey(to='backend.Raider2', db_constraint=False)),
            ],
        ),
    ]
