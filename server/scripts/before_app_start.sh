#!/usr/bin/env bash

touch /srv/django_exception_status.log
touch /srv/django_daemon_status.log
chown -R ubuntu:ubuntu /srv/django_exception_status.log
chown -R ubuntu:ubuntu /srv/django_daemon_status.log