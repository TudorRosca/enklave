# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('backend', '0069_cell'),
    ]

    operations = [
        migrations.CreateModel(
            name='Combatant',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('date_joined', models.DateTimeField(auto_now_add=True)),
                ('date_left', models.DateTimeField(default=None, null=True, blank=True)),
                ('last_hit_at', models.DateTimeField(default=None, null=True, blank=True)),
            ],
        ),
        migrations.CreateModel(
            name='RaiderCombat',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('started_at', models.DateTimeField(auto_now_add=True)),
                ('ended_at', models.DateTimeField(default=None, null=True, blank=True)),
                ('notes', models.CharField(default=None, max_length=255, null=True, blank=True)),
                ('enklave', models.ForeignKey(db_constraint=False, default=None, blank=True, to='backend.Enklave', null=True)),
                ('started_by', models.ForeignKey(db_constraint=False, default=None, blank=True, to=settings.AUTH_USER_MODEL, null=True)),
            ],
        ),
        migrations.CreateModel(
            name='RaiderCombatRaider',
            fields=[
                ('combatant_ptr', models.OneToOneField(parent_link=True, auto_created=True, primary_key=True, serialize=False, to='backend.Combatant')),
                ('raider', models.ForeignKey(to='backend.Raider2', db_constraint=False)),
                ('raider_combat', models.ForeignKey(to='backend.RaiderCombat', db_constraint=False)),
            ],
            bases=('backend.combatant',),
        ),
        migrations.CreateModel(
            name='RaiderCombatUser',
            fields=[
                ('combatant_ptr', models.OneToOneField(parent_link=True, auto_created=True, primary_key=True, serialize=False, to='backend.Combatant')),
                ('raider_combat', models.ForeignKey(to='backend.RaiderCombat', db_constraint=False)),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL, db_constraint=False)),
            ],
            bases=('backend.combatant',),
        ),
    ]
