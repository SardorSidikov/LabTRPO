(ns evilcap.clojure.3
  (:use clojure.test)
  )

(load-file "lab3.clj")

(defn evilcap_test_exp [input] 
		(Math/exp input)
)

(deftest a-test
  (testing "a-test"
    (is (< (Math/abs (- ((evilcap_normalize evilcap_test_exp) 5.0) 147.4131591025766)) 0.01))
  )
)

(run-tests 'evilcap.clojure.3)