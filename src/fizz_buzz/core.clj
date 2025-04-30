(ns fizz-buzz.core
  (:require [org.httpkit.server :as server]
            [clojure.data.json :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.util.response :as resp]
            [clojure.string :as str]
            [fizz-buzz.openapi :as openapi]
            [clojure.spec.alpha :as s]
            [fizz-buzz.spec :as spec])
  (:gen-class))

(def defaults
  {:Fizz 3
   :Buzz 5})

(defn- x [i val nam]
  (when (= 0 (mod i val))
    (-> nam
        name
        str/capitalize)))

(defn fizz-buzz
  [i]
  (let [x2 #(x i (first %1) (second %1))
        values {3 :fizz
                5 :buzz}
        output (apply str (map x2 values))]
    {:result (if (= "" output)
               i
               output)}))

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
    (-> i
        fizz-buzz
        json/write-str
        resp/response
        (resp/status 200)
        (resp/header "Content-Type" "application/json"))))

(defn fb-custom
  [req]
  (let [i (-> req
              :params
              :id
              Integer/parseInt)]
    (if-let [body (some-> req
                          :body
                          slurp)]
      (try
        (let [json-body (json/read-str body :key-fn keyword)
              body (into {} (map (fn [[kw v]] [kw (Integer/parseInt v)])) json-body)]
          (if (s/valid? ::spec/fizz-buzz-rules body)
            (-> {:result (fizz-b i body)}
                resp/response
                (resp/status 200)
                (resp/header "Content-Type" "application/json"))
            (do
              (println "error: " (s/explain ::spec/fizz-buzz-rules body))
              (-> "Your data, doesn't match what we expected"
                  resp/response
                  (resp/status 400)
                  (resp/header "Content-Type" "application/json")))))
        (catch Exception e
          (do
            (println "Error:" (.getMessage e))
            (-> "Your data, doesn't match what we expected"
                resp/response
                (resp/status 400)
                (resp/header "Content-Type" "application/json")))))
      (-> {:result (fizz-b i)}
          resp/response
          (resp/header "Content-Type" "application/json")
          (resp/status 200)))))

(defroutes app-routes
           (GET "/fizzbuzz/:id" [] fb)
           (POST "/fizzbuzz/:id" [] fb-custom)
           (GET "/openapi.json" [] (fn [_]
                                     (-> openapi/openapi-spec
                                         json/write-str
                                         resp/response
                                         (resp/status 200)
                                         (resp/header "Content-Type" "application/json"))))
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
