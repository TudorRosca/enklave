# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
import uuid


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0040_auto_20160317_0808'),
    ]

    operations = [
        migrations.CreateModel(
            name='Raider2',
            fields=[
                ('id', models.UUIDField(default=uuid.uuid4, serialize=False, editable=False, primary_key=True)),
                ('latitude', models.FloatField()),
                ('longitude', models.FloatField()),
                ('bearing', models.SmallIntegerField(default=100)),
                ('level', models.IntegerField(default=0)),
                ('energy', models.IntegerField(default=0)),
                ('status', models.SmallIntegerField(default=0)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('updated_at', models.DateTimeField(auto_now=True)),
                ('deleted_at', models.DateTimeField(default=None, null=True, blank=True)),
                ('enklave', models.ForeignKey(to='backend.Enklave', db_constraint=False)),
            ],
        ),
    ]
