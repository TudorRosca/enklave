#!/usr/bin/env bash
echo Creating cloudwatch config file in /root/awslogs.conf
cat <<EOF >/root/awslogs.conf
[general]
state_file = /var/awslogs/state/agent-state
## Your config file would have a lot more with the logs that you want to monitor and send to Cloudwatch
[/var/log/nginx/guni-error.log]
datetime_format = %Y-%m-%d %H:%M:%S
file = /var/log/nginx/guni-error.log
buffer_duration = 5000
log_stream_name = Enklave Prod {instance_id}
initial_position = end_of_file
log_group_name = /var/log/nginx/guni-error.log
[/var/log/nginx/guni-access.log]
datetime_format = %Y-%m-%d %H:%M:%S
file = /var/log/nginx/guni-access.log
buffer_duration = 5000
log_stream_name = Enklave Prod {instance_id}
initial_position = end_of_file
log_group_name = /var/log/nginx/guni-access.log
[/srv/django_exception_status.log]
datetime_format = %Y-%m-%d %H:%M:%S
file = /srv/django_exception_status.log
buffer_duration = 5000
log_stream_name = Enklave Prod {instance_id}
initial_position = end_of_file
log_group_name = /srv/django_exception_status.log
EOF

echo Creating aws credentials in /home/ubuntu/.aws/credentials
mkdir /home/ubuntu/.aws/
cat <<EOF > /home/ubuntu/.aws/credentials
[default]
aws_access_key_id = AKIAISYKG2WBXMLEAIAA
aws_secret_access_key = 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
EOF

echo Downloading cloudwatch logs setup agent
cd /root
wget https://s3.amazonaws.com/aws-cloudwatch/downloads/latest/awslogs-agent-setup.py
echo running non-interactive cloudwatch-logs setup script
python ./awslogs-agent-setup.py --region us-west-2 --non-interactive --configfile=/root/awslogs.conf