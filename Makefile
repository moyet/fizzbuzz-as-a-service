# Stuff
DOCKER_USERNAME ?= fizzbuzz
APPLICATION_NAME ?= fizzbuzz

compile:
	lein uberjar

lein-test:
	lein test

lein-run:
	lein run

build:
	docker build --tag ${DOCKER_USERNAME}/${APPLICATION_NAME} .

push:
	docker push