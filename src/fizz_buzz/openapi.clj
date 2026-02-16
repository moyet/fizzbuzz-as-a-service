(ns fizz-buzz.openapi
  (:require [ring.swagger.swagger2 :as swagger]
            [schema.core :as s]))

(s/defschema FizzBuzzResult {:result s/Str})
(s/defschema ErrorResponse {:error s/Str})
(def openapi-spec
  (swagger/swagger-json
    {:info {:title "FizzBuzz API"
            :version "1.0.0"}
     :paths {"/fizzbuzz/{id}" {:get {:summary "FizzBuzz as a service"
                                     :parameters {:path {:id s/Int}}
                                     :responses {200 {:schema FizzBuzzResult
                                                      :description "Found it!"}
                                                 400 {:schema ErrorResponse
                                                      :description "Invalid path parameter."}}}
                               :post {:summary "Custom fizzbuzz"
                                      :parameters {:path {:id s/Int}
                                                   :body {s/Keyword s/Int}}
                                      :responses {200 {:schema FizzBuzzResult
                                                       :description "Found it!"}
                                                  400 {:schema ErrorResponse
                                                       :description "Invalid request payload."}}}}}}))
