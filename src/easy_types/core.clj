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
;; The input to the fn will be a map of {:args :ret}
(t/tc-ignore
 (s/fdef yget-42 :args (s/cat) :ret int? ;; :fn #(= 42 (:ret %))
         ))
;; Raises a warning when lint is called, nice!
(t/ann yget-42 [:-> t/Int])
(defn yget-42 [] "42")
(t/tc-ignore
 (st/instrument `yget-42)
 (st/check `yget-42))

;; https://github.com/typedclojure/core.typed-example/blob/master/src/fire/simulate.clj#L42
(t/defalias Point
  "Just a point."
  '{:x t/Int
    :y t/Int})

;; (t/ann grid-from-fn [[Point -> State]
;;                    & :optional {:rows Long, :cols Long :wind Wind}
;;                    :mandatory {:q Number :p Number :f Number}
;;                    -> Grid])

(t/tc-ignore
 (s/def ::x (s/and int? #(> % 0) #(< % 100)))
 (s/def ::y (s/and int? #(> % 0) #(< % 100)))
 (s/def ::point (s/keys :req-un [::x ::y])))
;; We can ensure inputs match forms of data...nice!
(t/tc-ignore (s/fdef get-area
               :args (s/cat :m ::point)
               :ret int?
               :fn #(int? (:ret %))))
(t/ann get-area [Point :-> t/Int])
(defn get-area [{:keys [x y]}]
  (* x y))

(t/tc-ignore
 (st/instrument `get-area)
 ;; Interesting, found integer overflow, so would need to constrain x / y to max
 ;; Now with the new constraints though, check fails after 100 tries.
 (st/check `get-area {:clojure.spec.test.check/opts {:num-tests 25}})

 (sg/generate (s/gen ::point))
 (sg/sample (s/gen ::point)))

(defn call-get-area []
  ;; call with bad input, will produce error
  ;; (get-area {:a 1 :b 2})
  ;; (get-area {:x "oh" :y 2})
  ;; good input will not
  (get-area {:x 1 :y 2})
  )

(defn lint []
  (t/tc-ignore (st/instrument))
  (t/check-ns)
  ;; (require 'typespec.core :reload)
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
