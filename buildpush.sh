#!/bin/bash

clear
REPOSITORY=$1

if [ -z "$REPOSITORY" ]
then
    REPOSITORY=dtdemos
fi

IMAGE=dt-orders-order-service
FULLIMAGE=$REPOSITORY/$IMAGE

#./mvnw clean package
./mvnw clean package -Dmaven.test.skip=true

docker build -t $FULLIMAGE:1 . --build-arg APP_VERSION=1
docker build -t $FULLIMAGE:2 . --build-arg APP_VERSION=2
docker build -t $FULLIMAGE:3 . --build-arg APP_VERSION=3

echo "========================================================"
echo "Ready to push images ?"
echo "========================================================"
read -rsp "Press ctrl-c to abort. Press any key to continue"

echo "Pushing $FULLIMAGE:1"
docker push $FULLIMAGE:1

echo "Pushing $FULLIMAGE:2"
docker push $FULLIMAGE:2

echo "Pushing $FULLIMAGE:3"
docker push $FULLIMAGE:3