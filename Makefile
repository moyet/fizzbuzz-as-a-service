# Stuff
DOCKER_USERNAME ?= moyet
APPLICATION_NAME ?= fizzbuzz
GIT_SHA := $(shell git rev-parse --short HEAD)


compile:
	lein uberjar

lein-test:
	lein test

lein-run:
	lein run

build:
	docker build --tag ${DOCKER_USERNAME}/${APPLICATION_NAME}:${GIT_SHA} .

push:
	docker push  ${DOCKER_USERNAME}/${APPLICATION_NAME}:${GIT_SHA}
