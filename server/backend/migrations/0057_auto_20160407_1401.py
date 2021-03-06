# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('backend', '0056_auto_20160406_0656'),
    ]

    operations = [
        migrations.CreateModel(
            name='Turret',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('type', models.IntegerField(default=1, choices=[(1, b'Small'), (2, b'Medium')])),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('updated_at', models.DateTimeField(auto_now=True)),
                ('used_at', models.DateTimeField(default=None, null=True, blank=True)),
                ('enklave', models.ForeignKey(db_constraint=False, default=True, blank=True, to='backend.Enklave', null=True)),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL, db_constraint=False)),
            ],
        ),
        migrations.AlterField(
            model_name='enklavecombatuser',
            name='enklave_combat',
            field=models.ForeignKey(to='backend.EnklaveCombat', db_constraint=False),
        ),
    ]
