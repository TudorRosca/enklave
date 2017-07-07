# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('backend', '0019_auto_20160314_1308'),
    ]

    operations = [
        migrations.CreateModel(
            name='Message',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('txt', models.CharField(max_length=255)),
                ('date_viewed', models.DateTimeField(default=None, null=True, blank=True)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('updated_at', models.DateTimeField(auto_now=True)),
                ('deleted_at', models.DateTimeField(default=None, null=True, blank=True)),
                ('from_user', models.ForeignKey(related_name='from_user', to=settings.AUTH_USER_MODEL, db_constraint=False)),
                ('to_user', models.ForeignKey(related_name='to_user', to=settings.AUTH_USER_MODEL, db_constraint=False)),
            ],
        ),
    ]
