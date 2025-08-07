(ns fizz-buzz.openapi
  (:require [ring.swagger.swagger2 :as swagger]
            [schema.core :as s]))

(s/defschema FizzBuzz {:id s/Str})
(def openapi-spec
  (swagger/swagger-json
    {:info {:title "FizzBuzz API"
            :version "1.0.0"}
     :paths {"/fizzbuzz/:id" {:get {:summery "FizzBuzz as a service"
                                    :parameters {:path {:id s/Int}}
                                    :responses {200 {:schema FizzBuzz
                                                     :description "Found it!"}
                                                404 {:description "Ohnoes."}}}
                              :post {:summery "Custom fizzbuzz"
                                     :parameters {:path {:id s/Int}}
                                     :responses {200 {:schema FizzBuzz
                                                      :description "Found it!"}
                                                 404 {:description "Ohnoes."}}}}}}))
