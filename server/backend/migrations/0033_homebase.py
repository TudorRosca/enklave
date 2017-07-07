# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('backend', '0032_auto_20160316_1123'),
    ]

    operations = [
        migrations.CreateModel(
            name='HomeBase',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('latitude', models.FloatField()),
                ('longitude', models.FloatField()),
                ('scrap', models.IntegerField()),
                ('bricks', models.IntegerField()),
                ('cells', models.IntegerField()),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('location_updated_at', models.DateTimeField(default=None, null=True, blank=True)),
                ('updated_at', models.DateTimeField(auto_now=True)),
                ('deleted_at', models.DateTimeField(default=None, null=True, blank=True)),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL, db_constraint=False)),
            ],
        ),
    ]
