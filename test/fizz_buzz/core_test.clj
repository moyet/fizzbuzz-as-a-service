(ns fizz-buzz.core-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [fizz-buzz.core :refer :all])
  (:import [java.io ByteArrayInputStream]))

(defn- req
  ([id]
   {:params {:id id}})
  ([id body]
   {:params {:id id}
    :body (ByteArrayInputStream. (.getBytes body "UTF-8"))}))

(defn- read-json-body
  [response]
  (json/read-str (:body response) :key-fn keyword))

(deftest fizz-buzz-test
  (testing "That 4 returns a string 4"
    (is (= {:result "4"} (fizz-buzz 4))))
  (testing "That 5 returns Buzz"
    (is (= {:result "Buzz"} (fizz-buzz 5))))
  (testing "That 15 returns FizzBuzz"
    (is (= {:result "FizzBuzz"} (fizz-buzz 15)))))

(deftest fb-route-test
  (testing "GET with valid id returns 200 and result"
    (let [response (fb (req "15"))]
      (is (= 200 (:status response)))
      (is (= {:result "FizzBuzz"} (read-json-body response)))))
  (testing "GET with invalid id returns 400 and error payload"
    (let [response (fb (req "not-a-number"))]
      (is (= 400 (:status response)))
      (is (= {:error "Path parameter 'id' must be an integer."}
             (read-json-body response))))))

(deftest fb-custom-route-test
  (testing "POST without body falls back to default rules"
    (let [response (fb-custom (req "10"))]
      (is (= 200 (:status response)))
      (is (= {:result "Buzz"} (read-json-body response)))))
  (testing "POST with valid custom rules accepts string numbers"
    (let [response (fb-custom (req "6" "{\"fazz\":\"3\",\"jazz\":\"2\"}"))]
      (is (= 200 (:status response)))
      (is (= {:result "fazzjazz"} (read-json-body response)))))
  (testing "POST with valid custom rules accepts numeric values"
    (let [response (fb-custom (req "10" "{\"bazz\":5}"))]
      (is (= 200 (:status response)))
      (is (= {:result "bazz"} (read-json-body response)))))
  (testing "POST with invalid id returns 400"
    (let [response (fb-custom (req "x" "{\"buzz\":5}"))]
      (is (= 400 (:status response)))
      (is (= {:error "Path parameter 'id' must be an integer."}
             (read-json-body response)))))
  (testing "POST with zero divisor returns 400"
    (let [response (fb-custom (req "10" "{\"buzz\":0}"))]
      (is (= 400 (:status response)))
      (is (= {:error "Custom rules must be a JSON object mapping names to positive integers."}
             (read-json-body response)))))
  (testing "POST with malformed JSON returns 400"
    (let [response (fb-custom (req "10" "{\"buzz\":"))]
      (is (= 400 (:status response)))
      (is (= {:error "Custom rules must be a JSON object mapping names to positive integers."}
             (read-json-body response))))))
