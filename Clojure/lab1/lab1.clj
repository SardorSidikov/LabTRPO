(ns evilcap.clojure.1
  (:require [clojure.string :as str])
)

(defn evilcap_add [collection char]
		(map
   	(fn [newchar] (.concat collection newchar))
   	(filter (fn [char] (not (str/ends-with? collection char))) char)
		)
)


(defn evilcap_collection_contains? [collection char]  
  (some #(= char %) collection)
)


(defn evilcap_reduce_uniq [char count]
		(reduce
  		(fn [collection _]
    		(reduce into (map #(evilcap_add % char) collection))
    )
  		char
   	(range (- count 1))
		)
)

(defn evilcap_uniq [char]
  (reduce (fn [new_char char] (if (evilcap_collection_contains? new_char char) new_char (concat new_char [char]))) [] char)
)

(defn process [char count]
  (evilcap_reduce_uniq (evilcap_uniq char) count)
)