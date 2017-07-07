#!/usr/bin/env bash

cd /srv/enklave_app
. env/bin/activate
export LC_ALL=C
pip install -U pip
pip install -r /srv/enklave_app/enklave/requirements.txt