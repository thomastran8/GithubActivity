#/bin/bash
# This shell swaps from web2 to web1
cd /etc/nginx
sed -e s?web2:8080/activity/?web1:8080/activity/? <nginx.conf> /tmp/xxx
cp /tmp/xxx nginx.conf
service nginx reload
