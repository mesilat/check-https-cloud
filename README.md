# Check HTTPS Certificate for Atlassian Confluence (Cloud)

Building and installation instructions

## Create Docker machine

```
docker-machine rm checkcert

docker-machine create \
--driver=hetzner \
--hetzner-api-token=$HETZNER_TOKEN \
--hetzner-server-type=cx21 \
--hetzner-server-location=fsn1 \
checkcert
```
In case you get an error
```
Error creating machine: Error running provisioning: Unable to verify the Docker
daemon is listening: Maximum number of retries (10) exceeded
```
run
```
docker-machine ssh checkcert

apt-get update && apt-get dist-upgrade -y

reboot
```
then you will be able to connect to the docker machine
```
eval $(docker-machine env checkcert)
```
Configure Docker networking with static IP network addresses:
```
docker network create --driver=bridge \
-o com.docker.network.bridge.host_binding_ipv4=$(ifconfig eth0 | grep "inet " | awk '{print $2}') \
--subnet=172.20.0.0/24 \
--ip-range=172.20.0.0/24 \
--gateway=172.20.0.1 \
intranet
```
This setup uses [haproxy](http://www.haproxy.org) as a load balancer and I want
to have access to its logs for troubleshooting. Docker machine uses `rsyslog`
so I create a new configuration file `/etc/rsyslog.d/99-haproxy.conf` to enable remote logging:
```
$ModLoad imudp
$UDPServerAddress 172.20.0.1
$UDPServerRun 514
local0.* /var/log/haproxy-traffic.log
local0.notice /var/log/haproxy-admin.log
```
Then restart `rsyslog`:
```
systemctl restart rsyslog
```

## Install SSL certificate

Register domain name `checkcert.mesilat.com` (change to YOUR domain name).
```
ping checkcert.mesilat.com
```
Then create the certificate:
```
docker run --rm -it \
-v /var/docker/ssl:/etc/letsencrypt \
-p 80:80 certbot/certbot \
certonly --standalone -d checkcert.mesilat.com -m admin@mesilat.com \
--noninteractive --agree-tos
```
Concatenate certificate and key files:
```
docker-machine ssh ${DOCKER_MACHINE_NAME} \
'cat /var/docker/ssl/live/checkcert.mesilat.com/fullchain.pem /var/docker/ssl/live/checkcert.mesilat.com/privkey.pem > /var/docker/haproxy.pem'
```

## Build and run

To build Java backend service:
```
cd ./java

mvn clean package

docker build -t mesilat/check-https-cert-service .
```
To build the plugin:
```
cd .

docker build -t mesilat/check-https-cert-plugin .
```
To run:
```
docker-compose up -d
```
