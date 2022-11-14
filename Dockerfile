FROM openjdk:8-slim-buster

WORKDIR /tmp

COPY target/uberjar/fizz-buzz-0.1.0-SNAPSHOT-standalone.jar fizz-buzz.jar

CMD java -jar /tmp/fizz-buzz.jar

EXPOSE 3000