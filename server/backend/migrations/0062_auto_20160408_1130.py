# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0061_auto_20160408_1126'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='turret',
            name='created_at',
        ),
        migrations.RemoveField(
            model_name='turret',
            name='enklave',
        ),
        migrations.RemoveField(
            model_name='turret',
            name='id',
        ),
        migrations.RemoveField(
            model_name='turret',
            name='type',
        ),
        migrations.RemoveField(
            model_name='turret',
            name='updated_at',
        ),
        migrations.RemoveField(
            model_name='turret',
            name='used_at',
        ),
        migrations.RemoveField(
            model_name='turret',
            name='user',
        ),
        migrations.AddField(
            model_name='turret',
            name='crafteditem_ptr',
            field=models.OneToOneField(parent_link=True, auto_created=True, primary_key=True, default=None, serialize=False, to='backend.CraftedItem'),
            preserve_default=False,
        ),
    ]
