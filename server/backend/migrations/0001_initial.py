# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='UserProfile',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('phone_number', models.CharField(max_length=40, null=True, blank=True)),
                ('arn', models.CharField(max_length=512, null=True, blank=True)),
                ('profile_image', models.CharField(max_length=40, null=True, blank=True)),
                ('ejabberd_token', models.CharField(max_length=40, null=True, blank=True)),
                ('date_of_birth', models.DateTimeField(default=None, null=True, blank=True)),
                ('user', models.ForeignKey(to=settings.AUTH_USER_MODEL, db_constraint=False)),
            ],
        ),
    ]
