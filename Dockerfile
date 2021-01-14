FROM openjdk:14
COPY target/*.jar .

# used to set the problem pattern
ARG APP_VERSION=1
ENV APP_VERSION=$APP_VERSION

# during development can set to true so that you dont
# need to have the customer and catalog service running
ARG DEV_MODE=false
ENV DEV_MODE=$DEV_MODE

CMD /usr/bin/java -Xmx400m -Xms400m -jar *.jar 
EXPOSE 8080
