(ns fizz-buzz.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

(defn fizz-buzz
  [i]
  (let [fizz 3
        buzz 5
        fizzbuzz (* fizz buzz)]
  (if (= 0 (mod i fizzbuzz))
    "FizzBuzz"
    (if (= 0 (mod i fizz))
      "Fizz"
      (if (= 0 (mod i buzz))
        "Buzz"
        i)))))


(defn fb [req]
  (println req)
  (let [i (-> req
              :params
              :id
              Integer/parseInt
              )]
  {:status 218
   :headers {"Content-Type" "text/html"}
   :body (-> i
             fizz-buzz
             str)}
  ))

(defroutes app-routes
           (GET "/fizzbuzz/:id" [] fb)
           (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))