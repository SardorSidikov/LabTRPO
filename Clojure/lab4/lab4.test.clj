(ns evilcap.clojure.4
  (:use clojure.test)
)

(load-file "lab4.clj")

(declare is_contains_equal_forms?)

(defn is_equal_forms? [first_form second_form]
	(cond
		(is_variable? first_form) (is_equal_variables? first_form second_form)

		(is_const? first_form) (and 
							(is_const? second_form) 
							(= (const_value first_form) (const_value second_form)))

		(is_negation? first_form) (and 
							(is_negation? second_form)
							(is_equal_forms? (args first_form) (args second_form)))

		(is_conjunction? first_form) (and
							(is_conjunction? second_form)
							(= (count (args first_form)) (count (args second_form))) 
							(every? #(is_contains_equal_forms? % (args second_form)) (args first_form)))

		(is_disjunction? first_form) (and
							(is_disjunction? second_form)
							(= (count (args first_form)) (count (args second_form))) 
							(every? #(is_contains_equal_forms? % (args second_form)) (args first_form)))

		:else false
	)
)

(defn is_contains_equal_forms? [form list_of_forms]
	  some #(is_equal_forms? form %) list_of_forms
)

(defn is_negation_or_atomic? [input]
  (or (is_negation? input) (is_atomic? input))
)

(defn is_negation_atomic_or_conjunction? [input]
	 (or (is_negation_or_atomic? input) (is_conjunction? input))
)

(defn is-dnf? [form]
	(cond
		(is_atomic? form) true
		(is_negation? form) (is_atomic? (args form))
		(is_conjunction? form) (every? is_negation_or_atomic? (args form)) 
		(is_disjunction? form) (every? is_negation_atomic_or_conjunction? (args form)) 
		:else false)
)

(deftest a-test 
	(testing "primary-test"
		 (is (let [
				          first_form_input (implication (variable :x) (variable :y))
				          second_form_input (disjunction (negation (variable :x)) (variable :y))
				          first_form (process first_form_input)
				          second_form (process second_form_input)
				    ]   
				(and (is-dnf? first_form) (is_equal_forms? first_form second_form))))

	  (is (let [
				          first_form_input (implication (variable :x) (implication (variable :y) (variable :z)))
				          second_form_input (disjunction (negation (variable :x)) (disjunction (negation (variable :y)) (variable :z)))
				          first_form (process first_form_input)
				          second_form (process second_form_input)
				        ]
				(and (is-dnf? first_form) (is_equal_forms? first_form second_form))))
	)
)

(run-tests 'evilcap.clojure.4)
