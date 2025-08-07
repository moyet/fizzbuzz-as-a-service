FROM clojure:lein-2.9.8 AS builder


WORKDIR /app

# Make a copy of the source code in the builder

COPY . .

# Then run leinigen to build an uberjar

RUN lein uberjar


# We now switch to another container

FROM amazoncorretto:8u462-al2023-jre


LABEL maintainer="Magnus Dreyer <magnus.dreyer@gmail.com>"

WORKDIR /app

COPY --from=builder /app/target/uberjar/fizz-buzz-0.1.0-SNAPSHOT-standalone.jar fizz-buzz.jar

CMD ["java", "-jar", "/app/fizz-buzz.jar"]

EXPOSE 3000
