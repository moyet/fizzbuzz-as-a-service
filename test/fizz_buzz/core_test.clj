(ns fizz-buzz.core-test
  (:require [clojure.test :refer :all]
            [fizz-buzz.core :refer :all]))

(deftest a-test
  (testing "That 4 returns a 4"
    (is (= {:result 4} (fizz-buzz 4))))
  (testing "That 5 returns a Buzz"
    (is (= {:result "Buzz"} (fizz-buzz 5))))
  (testing "That 15 returns a FizzBuzz"
    (is (= {:result "FizzBuzz"} (fizz-buzz 15)))))
