(ns fizz_buzz.core
  (:require [org.httpkit.server :as server]
            [clojure.data.json :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.string :as str]
            [fizz-buzz.openapi :as openapi]
            [clojure.spec.alpha :as s]
            [fizz-buzz.spec :as spec])
  (:gen-class))

(defn- x [i val nam]
  (if (= 0 (mod i val))
    (-> nam name
        str/capitalize)))

(defn fizz-buzz
  [i]
  (let [x2 #(x i (first %1) (second %1) )
        values {3 :fizz
                5 :buzz}
        output (apply str (map x2 values))
        output (if (= "" output)
                 i
                 output)
        ]
    {:result output}))

(def defaults
  {:Fizz 3
   :Buzz 5})

(defn fizz-b
  ([i] (fizz-b i defaults))
  ([i fizzings]
   (let [f (fn [s [kw number]]
             (if (zero? (mod i number))
               (str s (name kw))
               s))
         re (reduce f "" fizzings)]
   (if (= "" re)
     (str i)
     re))))


(defn fb [req]
  (let [i (-> req
              :params
              :id
              Integer/parseInt)]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (-> i
             fizz-buzz
             json/write-str)}))


(defn fb-custom
  [req]
  (let [i (-> req
              :params
              :id
              Integer/parseInt)]
  (if-let [body (some-> req
                        :body
                        slurp)]
    (let [json-body (json/read-str body :key-fn keyword)
          body  (into {} (map (fn [[kw v]] [kw (Integer/parseInt v)])) json-body )]
      {:status  200
       :headers {"Content-Type" "text/html"}
       :body    (fizz-b i body)})
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (fizz-b i)})))

(defroutes app-routes
           (GET "/fizzbuzz/:id" [] fb)
           (POST "/fizzbuzz/:id" [] fb-custom)
           (GET "/openapi.json" [] (fn [_] {:status 200
                                            :headers {"Content-Type" "application/json"}
                                            :body (json/write-str openapi/openapi-spec)}))
           (route/not-found "Error, page still not found!\n"))

(def custom-middleware
  (-> site-defaults
      (assoc-in [:security :anti-forgery] false)))          ;; Disable CSRF

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes custom-middleware) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))