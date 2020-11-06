#!/bin/bash

clear
if [ $# -lt 1 ]
then
  echo "missing arguments. Expect ./buildpush.sh <REPOSITORY> "
  echo "example:   ./buildpush.sh dtdemos"
  exit 1
fi

IMAGE=dt-orders-order-service
REPOSITORY=$1
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

docker push $FULLIMAGE:1
docker push $FULLIMAGE:2
docker push $FULLIMAGE:3