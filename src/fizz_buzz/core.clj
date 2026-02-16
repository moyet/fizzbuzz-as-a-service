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
               (str i)
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

(defn- json-response
  [status body]
  (-> body
      json/write-str
      resp/response
      (resp/status status)
      (resp/header "Content-Type" "application/json")))

(defn- error-response
  [message]
  (json-response 400 {:error message}))

(defn- parse-id
  [req]
  (try
    (-> req :params :id Integer/parseInt)
    (catch Exception _
      nil)))

(defn- parse-rule-value
  [v]
  (cond
    (integer? v) v
    (string? v) (Integer/parseInt v)
    :else (throw (ex-info "invalid-rule-value" {:value v}))))

(defn- parse-custom-rules
  [req]
  (let [body (some-> req :body slurp)]
    (if (str/blank? body)
      nil
      (let [json-body (json/read-str body :key-fn keyword)]
        (when-not (map? json-body)
          (throw (ex-info "invalid-rules-body" {:value json-body})))
        (let [rules (into {} (map (fn [[kw v]] [kw (parse-rule-value v)])) json-body)]
          (if (s/valid? ::spec/fizz-buzz-rules rules)
            rules
            (throw (ex-info "invalid-rules-spec" {:value rules}))))))))

(defn fb [req]
  (if-let [i (parse-id req)]
    (json-response 200 (fizz-buzz i))
    (error-response "Path parameter 'id' must be an integer.")))

(defn fb-custom
  [req]
  (if-let [i (parse-id req)]
    (try
      (if-let [rules (parse-custom-rules req)]
        (json-response 200 {:result (fizz-b i rules)})
        (json-response 200 {:result (fizz-b i)}))
      (catch Exception e
        (println "Error:" (.getMessage e))
        (error-response "Custom rules must be a JSON object mapping names to positive integers.")))
    (error-response "Path parameter 'id' must be an integer.")))

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
    (server/run-server (wrap-defaults #'app-routes custom-middleware)
                       {:port port :ip "0.0.0.0"})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
