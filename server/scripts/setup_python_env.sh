#!/usr/bin/env bash

export LC_ALL=C
sudo pip install virtualenv
cd /srv/enklave_app
virtualenv env

chown -R ubuntu:ubuntu /srv/enklave_app/env