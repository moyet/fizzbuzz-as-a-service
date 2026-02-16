(ns fizz-buzz.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::fizz-value pos-int?)
(s/def ::fizz-name keyword? )
(s/def ::fizz-buzz-rules (s/map-of ::fizz-name ::fizz-value))
