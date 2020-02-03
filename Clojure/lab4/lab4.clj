(ns evilcap.clojure.4)

(require '[clojure.string :as string])

(defn const [n]
  {pre [(or (= n 1) (= n 0))]}
	 (list ::const n)
)

(defn is_const? [input]
  (= (first input) ::const)
)

(defn const_value [input]
	(second input))

(defn is_true? [input]
	(and (is_const? input) (= (const_value input) 1))
)

(defn is_false? [input]
	 (and (is_const? input) (= (const_value input) 0))
)


(defn variable [input]
	 {pre [(keyword? input)]}
	 (list ::var input)
)

(defn is_variable? [input]
  (= (first input) ::var)
)

(defn variable_name [input]
 	(second input)
)

(defn is_equal_variables? [first_variable second_variable]
	 (and
		  (is_variable? first_variable)
		  (is_variable? second_variable)
		  (= (variable_name first_variable) (variable_name second_variable))
  )
)

(defn conjunction [input & rest]
	 (if (empty? rest)
		 input
		 (list ::and (cons input rest))
	 )
)

(defn is_conjunction? [input]
	 (= (first input) ::and)
)

(defn disjunction [input & rest]
	 (if (empty? rest)
		 input
		 (list ::or (cons input rest))
		)
)

(defn is_disjunction? [input]
	 (= (first input) ::or)
)

(defn negation [input]
	 (list ::not input)
)

(defn is_negation? [input]
	(= (first input) ::not))


(defn implication [first second]
	 (disjunction (negation first) second)
)


(defn args [input]
	 (first (rest input))
)

(defn is_atomic? [input]
	 (or
		  (is_variable? input) 
		  (is_const? input)
		)
)


(declare calculate)

(def evaluation_rules
	(list

		; propagate negations

		[(fn [input variables]
			  (and
				  (is_negation? input)
				  (is_conjunction? (args input))
			  )
			)

		 (fn [input variables] 
		 	 (apply disjunction (map negation (args (args input))))
		 )
		]

		[(fn [input variables]
			  (and
				  (is_negation? input)
				  (is_disjunction? (args input))
			  )
			)
		 (fn [input variables] 
		 	 (apply conjunction (map negation (args (args input))))
		 )
		]

		; remove double negations

		[(fn [input variables]
			  (and
				   (is_negation? input)
				   (is_negation? (args input))
				 )
			)
		 (fn [input variables] 
		  	(args (args input))
		 )
		]

		; distributive rule

		[(fn [input variables]
			  (and
				   (is_conjunction? input)
				   (some is_disjunction? (args input))
				 )
			)

		 (fn [input variables]
		 	(let [
		 		conj_elements (args input)
		 		disj (some #(if (is_disjunction? %) % false) conj_elements)
		 		xrest (filter #(not (= % disj)) conj_elements)]

		 		(do 
		 		  (apply disjunction (map #(apply conjunction (cons % xrest)) (args disj)))
		 		))
		 )
		]


		[(fn [input variables]
			  (and
				   (is_conjunction? input)
				   (some is_conjunction? (args input))
				 )
			)

		 (fn [input variables]
		 	(let [
		 		conj_elements (args input)
		 		conjunc (some #(if (is_conjunction? %) % false) conj_elements)
		 		xrest (filter #(not (= % conjunc)) conj_elements)]

		 		(do 
		 			(apply conjunction (concat xrest (args conjunc)))
		 		))
		 )
		]

		[(fn [input variables]
			  (and
				   (is_disjunction? input)
				   (some is_disjunction? (args input))
				 )
			)

		 (fn [input variables]
		 	(let [
		 		disj_elements (args input)
		 		disjunc (some #(if (is_disjunction? %) % false) disj_elements)
		 		xrest (filter #(not (= % disjunc)) disj_elements)]

		 		(do 
		 			(apply disjunction (concat xrest (args disjunc)))
		 		))
		 )
		]

		; simplify inputessions

		[(fn [input variables]
			  (and
				  (is_conjunction? input)
				  (some is_false? (args input))
				 )
			)

		 (fn [input variables]
		 	 (const 0)
		 )
		]

		[(fn [input variables]
			  (and
				   (is_disjunction? input)
				   (some is_true? (args input))
				 )
			)

		 (fn [input variables]
		 	 (const 1)
		 )
		]

		[(fn [input variables]
			  (and
				   (is_conjunction? input)
				   (some is_true? (args input))
				 )
			)

		 (fn [input variables]
		 	 (let [rst (filter #(not (is_true? %)) (args input))]
		 		  (apply conjunction (if (empty? rst) (const 1) rst))
		 		)
		 )
		]

		[(fn [input variables]
			  (and
				   (is_disjunction? input)
				   (some is_false? (args input))
			  )
			)

		 (fn [input variables]
		 	 (let [rst (filter #(not (is_false? %)) (args input))]
		 		  (apply disjunction (if (empty? rst) (const 0) rst))
		 		)
		 )
		]

		; negation of a constant

		[(fn [input variables]
			  (and
				   (is_negation? input)
				   (is_false? (args input))
				 )
			)

		 (fn [input variables]
		 	 (const 1)
		 )
		]

		[(fn [input variables]
			  (and
				   (is_negation? input)
				   (is_true? (args input))
				 )
			)

		 (fn [input variables]
		 	(const 0)
		 )
		]

		; const ops
	
		[(fn [input variables]
			  (and
				   (is_negation? input)
				   (is_true? (args input))
			  )
			)

		 (fn [input variables]
		 	 (const 0)
		 )
		]

		; variables substitution

		[(fn [input variables]
			  (and (is_variable? input) (contains? variables (variable_name input)))
			)

		 (fn [input variables]
		 	(get variables (variable_name input))
		 )
		]

		; calculate arguments of not/and/or inputessions

		[(fn [input variables]
			  (and (is_negation? input) (not (is_const? (args input))))
			)

		 (fn [input variables]
		 	 (negation (calculate (args input) variables))
		 )
		]

		[(fn [input variables]
			  (and (is_disjunction? input) (not (is_const? (args input))))
			)
		 
		 (fn [input variables] 
		 	(apply disjunction (map #(calculate % variables) (args input)))
		 )
		]

		[(fn [input variables]
			  (and (is_conjunction? input) (not (is_const? (args input))))
			)

		 (fn [input variables] 
		 	 (apply conjunction (map #(calculate % variables) (args input)))
		 )
		]

		[(fn [input variables] true) (fn [input variables] input)]
))

; apply first rule that applies (which predicate is true)
(defn calculate [input values]
	((some (fn [rule] 
				(if ((first rule) input values)
					(second rule)
					false)
				)
			evaluation_rules)
		input values)
)

(defn is_printable? [input]
	(or 
		(is_atomic? input)
		(is_negation? input))
)

(defn input_print [input]
	(str
		(if (is_printable? input) "" "(")
		(cond
			(is_variable? input) (str (variable_name input))
			(is_const? input) (str (const_value input))
			(is_disjunction? input) (string/join " || " (map input_print (args input)))
			(is_conjunction? input) (string/join " && " (map input_print (args input)))
			(is_negation? input) (str "!" (input_print (args input)) "")
			:else "unknown")
		(if (is_printable? input) "" ")")))

; apply rules until inputession stops changing
(defn process 
	([input] (process input nil))
	([input variables] (process input variables false))
	([input variables debug]
		(do 
			(if debug (println (input_print input)) false)
			(let [ev (calculate input variables)]
				(if (= ev input)
					input (process ev variables debug))))))


(def input (disjunction (const 0) (const 1)))

(println (input_print input))
(println (input_print (process (variable :x) {:x (const 1)}) ))
