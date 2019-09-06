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
;; instrument can apply to future invocations, but the return types
;; are only ever validated during the check calls (which also will
;; only work if test.check is in deps) check will work with the
;; generative testing stuff, and for some input types, needs more fine
;; grained inputs via a generator (if test.check isn't included, it'll
;; be a different stack trace/error there).
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
