#!/usr/bin/env bash

apt-get install python-pip -y
apt-get install python-dev -y
apt-get install build-essential -y
apt-get install nginx -y
apt-get install postgresql -y
apt-get install postgresql-contrib -y
apt-get install python-psycopg2 -y
apt-get install libpq-dev -y
apt-get install libjpeg62 -y
apt-get install libjpeg-dev -y
apt-get install freetype* -y
apt-get install libpng-dev -y
apt-get install zlib1g-dev -y
apt-get install dtach -y
apt-get install git -y

#pip install newrelic
#cd /srv
#newrelic-admin generate-config 93ee4b05eb750cfcce202d2eb19259ecc3bf68b6 newrelic.ini