(ns easy-types.core
  {:lang :core.typed}
  (:require
   [clojure.core.typed :as t]
   [clojure.repl :refer :all]
   [clojure.test :as ct]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as sg]
   [clojure.spec.test.alpha :as st]
   )
  (:gen-class))

;; Spec out a 0 arity function
(t/tc-ignore
 (s/fdef get-42 :args (s/cat) :ret int? :fn #(= 42 (:ret %))))
(t/ann get-42 [:-> t/Int])
(defn get-42 [] 42)
;; instrument can apply to future invocations, but the return types
;; are only ever validated during the check calls (which also will
;; only work if test.check is in deps) check will work with the
;; generative testing stuff, and for some input types, needs more fine
;; grained inputs via a generator (if test.check isn't included, it'll
;; be a different stack trace/error there).
(t/tc-ignore
 (st/instrument `get-42)
 (st/check `get-42))

;; Spec out a 0 arity function that will fail the spec
(t/tc-ignore
 (s/fdef yget-42 :args (s/cat) :ret int? ;; :fn #(= 42 (:ret %))
         ))
;; Raises a warning when lint is called, nice!
(t/ann yget-42 [:-> t/Int])
(defn yget-42 [] "42")
(t/tc-ignore
 (st/instrument `yget-42)
 (st/check `yget-42))

;; (t/Ann get-area )
;; (defn get-area [{:keys [x y]}]
;;   (* x y))

(defn lint []
  (t/tc-ignore (st/instrument))
  (t/check-ns)
  ;; (require 'typespec.core :reload)
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
