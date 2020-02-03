(ns evilcap.clojure.2)

(def eps 0.01)

(defn evilcap_process [a b c] 
		(* 0.5 c (+ a b))
)

(defn evilcap_reduce [input_func r]
		(reduce (fn [sum i] (+ sum ((memoize evilcap_process) (input_func (* i eps)) (input_func (* (+ 1 i) eps)) eps))) 0 (range r))
)

(defn evilcap_result [input] 
		(Math/log (+ 1 input))
)

(defn evilcap_normalize [input]
		(fn [value] 
    (+ 
    		(evilcap_reduce input (quot value eps))
     	(evilcap_process (input (* (quot value eps) eps)) (input value) (- value (* (quot value eps) eps)))
    )
  )
)

(println (time ((evilcap_normalize evilcap_result) 5.0)))
