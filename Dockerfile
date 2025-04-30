FROM clojure:lein-2.9.8 AS builder

WORKDIR /app

# Make a copy of the source code in the builder

COPY . .

# Then run leinigen to build an uberjar

RUN lein uberjar


# We now switch to another container

FROM openjdk:24-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/uberjar/*.jar fizz-buzz.jar

CMD ["java", "-jar", "/app/fizz-buzz.jar"]

EXPOSE 3000
