#!/usr/bin/env bash

# An example script for using FizzaaS, you could use almost any other language for this.

echo "Testing Fizzbuzz as a Service"

url="127.0.0.1:3000/fizzbuzz/[1-200]"

echo $(curl -s $url | jq -r .result)
