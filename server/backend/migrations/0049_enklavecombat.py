# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('backend', '0048_userprofile_level'),
    ]

    operations = [
        migrations.CreateModel(
            name='EnklaveCombat',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('started_at', models.DateTimeField(auto_now_add=True)),
                ('ended_at', models.DateTimeField(default=None, null=True, blank=True)),
                ('notes', models.CharField(max_length=255)),
                ('enklave', models.ForeignKey(to='backend.Enklave', db_constraint=False)),
                ('started_by', models.ForeignKey(to=settings.AUTH_USER_MODEL, db_constraint=False)),
            ],
        ),
    ]
