(ns easy-types.core
  (:require
   [clojure.repl :refer :all]
   [clojure.test :as ct]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as sg]
   [clojure.spec.test.alpha :as st]
   )
  (:gen-class))

;; Spec out a 0 arity function
(s/fdef get-42 :args (s/cat) :ret int? :fn #(= 42 (:ret %)))
(defn get-42 [] 42)
(st/instrument `get-42)
(st/check `get-42)

;; Spec out a 0 arity function that will fail the spec
(s/fdef yget-42 :args (s/cat) :ret int? ;; :fn #(= 42 (:ret %))
        )
(defn yget-42 [] "42")
(st/instrument `yget-42)
(st/check `yget-42)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
