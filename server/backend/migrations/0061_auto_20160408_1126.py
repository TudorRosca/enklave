# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('backend', '0060_auto_20160408_1116'),
    ]

    operations = [
        migrations.CreateModel(
            name='CraftedItem',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('type', models.IntegerField(default=1, choices=[(1, b'Small'), (2, b'Medium')])),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('updated_at', models.DateTimeField(auto_now=True)),
                ('used_at', models.DateTimeField(default=None, null=True, blank=True)),
            ],
        ),
        migrations.CreateModel(
            name='Shield',
            fields=[
                ('crafteditem_ptr', models.OneToOneField(parent_link=True, auto_created=True, primary_key=True, serialize=False, to='backend.CraftedItem')),
            ],
            bases=('backend.crafteditem',),
        ),
        migrations.AddField(
            model_name='crafteditem',
            name='enklave',
            field=models.ForeignKey(db_constraint=False, default=None, blank=True, to='backend.Enklave', null=True),
        ),
        migrations.AddField(
            model_name='crafteditem',
            name='user',
            field=models.ForeignKey(to=settings.AUTH_USER_MODEL, db_constraint=False),
        ),
    ]
