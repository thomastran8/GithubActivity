#docker rm $(docker ps -qa --no-trunc --filter "status=exited")
docker rm -f $(docker ps -qa --no-trunc)
docker network rm $(docker network ls | grep "bridge" | awk '/ / { print $1 }')
