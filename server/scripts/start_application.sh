#!/usr/bin/env bash

mkdir -p /srv/gunicorn
chmod -R 777 /srv/gunicorn

cd /srv/enklave_app/enklave/config/

cp nginx.conf /etc/nginx/nginx.conf
cp gunicorn.conf /etc/init/gunicorn.conf
cp tornado.conf /etc/init/tornado.conf


service gunicorn restart
service nginx restart
service tornado restart

