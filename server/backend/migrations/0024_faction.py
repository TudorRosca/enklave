# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0023_userprofile_energy'),
    ]

    operations = [
        migrations.CreateModel(
            name='Faction',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('name', models.CharField(max_length=100)),
                ('description', models.CharField(max_length=255)),
                ('display_order', models.SmallIntegerField(choices=[(1, b'1'), (2, b'2'), (3, b'3')])),
                ('color', models.CharField(max_length=12)),
                ('logo', models.URLField(max_length=40)),
            ],
        ),
    ]
