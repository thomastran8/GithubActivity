#/bin/bash

# create the two containers (The user will do this)
#bash dorun.sh

# check if web1 or web2 is currently running
WEBP="$(docker ps --filter=name=web1 -q)"
WEBS=""
SWAP=""
if [ ! -z "$WEBP" ]; then
 WEBP="web1" \
 WEBS="web2" \
 SWAP="swap2.sh"
else
 WEBP="web2" \
 WEBS="web1" \
 SWAP="swap1.sh"
fi

#use correct web, add image from command argument
docker run -d --net=ecs189_default --name="$WEBS" "$1"

#use container id of ng, use correct swap based on web
docker exec $(docker ps --filter=ancestor=ng -q) /bin/bash /bin/"$SWAP"

# remove old container
if [ "$WEBP" == "web1" ]; then
 docker rm -f "$(docker ps --filter=name=web1 -q)"
else
 docker rm -f "$(docker ps --filter=name=web2 -q)"
fi

sleep 1
