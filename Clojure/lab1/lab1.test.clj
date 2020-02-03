(ns evilcap.clojure.1
  (:use clojure.test)
  (:require [clojure.string :as str])
)

(load-file "lab1.clj")

(deftest a-test
  (testing "primary-test"
    (is (= (sort (process ["a" "b" "c"] 2)) (sort ["cb" "ca" "bc" "ba" "ab" "ac"])))
    (is (= (sort (process ["b" "b" "a"] 3)) (sort ["bab" "aba"])))
    (is (= (sort (process [] 1000)) (sort [])))
  )
)

(run-tests 'evilcap.clojure.1)
