# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0068_auto_20160413_0650'),
    ]

    operations = [
        migrations.CreateModel(
            name='Cell',
            fields=[
                ('crafteditem_ptr', models.OneToOneField(parent_link=True, auto_created=True, primary_key=True, serialize=False, to='backend.CraftedItem')),
            ],
            bases=('backend.crafteditem',),
        ),
    ]
