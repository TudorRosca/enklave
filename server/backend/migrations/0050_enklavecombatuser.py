# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('backend', '0049_enklavecombat'),
    ]

    operations = [
        migrations.CreateModel(
            name='EnklaveCombatUser',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('date_joined', models.DateTimeField(auto_now_add=True)),
                ('date_left', models.DateTimeField(default=None, null=True, blank=True)),
                ('last_hit_at', models.DateTimeField(default=None, null=True, blank=True)),
                ('type', models.IntegerField(default=2, choices=[(1, b'Defender'), (2, b'Attacker')])),
                ('enklave_combat', models.ForeignKey(to='backend.EnklaveCombat')),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL, db_constraint=False)),
            ],
        ),
    ]
