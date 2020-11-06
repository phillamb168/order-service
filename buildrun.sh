#!/bin/bash

clear
if [ $# -lt 2 ]
then
  echo "missing arguments. Expect ./buildrun.sh <REPOSITORY> <VERSION_TAG>"
  echo "example:   ./buildrun.sh dtdemos 1"
  exit 1
fi

IMAGE=dt-orders-order-service
REPOSITORY=$1
VERSION_TAG=$2
FULLIMAGE=$REPOSITORY/$IMAGE:$VERSION_TAG

echo "Building: $FULLIMAGE"
docker build -t $FULLIMAGE .

echo ""
echo "========================================================"
echo "Ready to run $FULLIMAGE ?"
echo "========================================================"

read -rsp "Press ctrl-c to abort. Press any key to continue"
echo ""
echo "access app @ http://localhost"
echo "" 
docker run -it -p 80:8080 \
  --env CUSTOMER_SERVICE_DOMAIN=172.17.0.1 \
  --env CUSTOMER_SERVICE_PORT=8181 \
  --env CATALOG_SERVICE_DOMAIN=172.17.0.1 \
  --env CATALOG_SERVICE_PORT=8182 \
  $FULLIMAGE 
