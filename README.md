# Overview

This repo has the code for the orders service for demostrations.  See the [overview](https://github.com/dt-orders/overview) repo for an overiew for that whole application.

# Developer Notes

This service needs to pull in data from the customer and catalog services, so they must be running.  If you are running from code, then you need to set the Environment variables to match the end points.

```
CUSTOMER_SERVICE_DOMAIN: "localhost"
CUSTOMER_SERVICE_PORT: "8181"

CATALOG_SERVICE_DOMAIN: "localhost"
CATALOG_SERVICE_PORT: "8182"
```

There is a feature to run in `DEV_MODE` where the calls to customer and catalog services are not made.  When `DEV_MODE` is set, then the data is faked out.  Just set `DEV_MODE` as an environment variable. 

## Pre-requisites

The following programs to be installed
* Java 1.8
* Maven
* Docker

## Build and Run Locally

1. run these commands
  ```
  ./mvnw clean package -Dmaven.test.skip=true
  java -jar target/*.jar
  ```
2. access application at ```http://localhost:8080```

## Build Docker Images and push images to a repository

Use the provided Unix shell scipt that will build the docker image and publish it. There are three versions that will be built.

    Just call: `./buildpush.sh <REPOSITORY>`

    For example: `./buildpush.sh dtdemos`

## Build Docker images and run locally 

Use the provided Unix shell scipt that will build the docker image and run it. 

    Just call: `./buildrun.sh <REPOSITORY> <VERSION_TAG> <DEV_MODE>`

    For example: `./buildrun.sh dtdemos 1 true`

2. access application at ```http://localhost:8080```

## quicktest

Use the provided Unix shell script that loops and calls the app URL.  Just call:

```./quicktest.sh <catalog base url>```

For example:

```./quicktest.sh http://localhost:8080```

# Credits

* Orginal demo code: https://github.com/ewolff/microservice-kubernetes