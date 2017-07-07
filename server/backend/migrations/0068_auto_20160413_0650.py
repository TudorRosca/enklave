# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0067_auto_20160411_1224'),
    ]

    operations = [
        migrations.CreateModel(
            name='Brick',
            fields=[
                ('crafteditem_ptr', models.OneToOneField(parent_link=True, auto_created=True, primary_key=True, serialize=False, to='backend.CraftedItem')),
            ],
            bases=('backend.crafteditem',),
        ),
        migrations.AddField(
            model_name='crafteditem',
            name='energy',
            field=models.IntegerField(default=0),
        ),
    ]
