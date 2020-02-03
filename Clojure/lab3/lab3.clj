(ns evilcap.clojure.3)

(def eps 0.01)

(defn evilcap_process [a b c] 
		(* 0.5 c (+ a b))
)

(defn evilcap_nth [input_func value]
  (nth (map first (iterate (fn [[sum i]]
    [
      (+ sum (evilcap_process (input_func (* i eps)) 
      (input_func (* (+ 1 i) eps)) eps)) (inc i)
    ]
  ) [0 0] )) value))

(defn evilcap_normalize [input]
		(fn [value] 
    (+ 
    		(evilcap_nth input (quot value eps))
     	(evilcap_process (input (* (quot value eps) eps)) (input value) (- value (* (quot value eps) eps)))
    )
  )
)

(defn evilcap_result [input] 
		(Math/log (+ 1 input))
)

(println (time ((evilcap_normalize evilcap_result) 5.0)))