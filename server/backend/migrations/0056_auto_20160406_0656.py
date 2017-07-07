# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('backend', '0055_auto_20160406_0651'),
    ]

    operations = [
        migrations.RunSQL("""CREATE INDEX backend_enklave_location ON
        backend_enklave USING gist (ll_to_earth(latitude, longitude));""")
    ]
