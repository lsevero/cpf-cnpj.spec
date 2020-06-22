(ns cpf-cnpj-spec.cpf
  (:refer-clojure :exclude [format])
  (:require [cpf-cnpj-spec.shared :as shared]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(def ^:const length 11)

(def ^:private control-digits
  (partial shared/control-digits (range 10 1 -1) (range 11 1 -1)))

(def ^:private repeated
  "A set of cpfs with repeated digits that are
  considered valid by the algorithm, but normally shouldn't count as valid."
  (conj (set (for [i (range 10)]
               (repeat length i))) [0 1 2 3 4 5 6 7 8 9 0]))

(defn- valid-impl?
  "Takes a string, seq of digits or a cpf. Returns true if valid, else false.
  A cpf
  Does not validate formatting."
  [cpf]
  (try
    (let [cpf (shared/parse cpf)
          [digits control] (shared/split-control cpf)]
      (and (= length (count cpf))
           (not (repeated cpf))
           (= control (control-digits digits))))
    (catch #?(:clj Exception
              :cljs :default) e false)))

(defn valid?
  "Takes a string. Returns true if valid, else false."
  [cpf]
  (try
    (if (= (count cpf) length)
      (valid-impl? cpf)
      false)
    (catch #?(:clj Exception
              :cljs :default) e false)))

(defn valid-int?
  "Takes a integer and validade it as a cpf"
  [cpf]
  (try
    (valid-impl? (clojure.core/format (str "%0" length "d") cpf))
    (catch #?(:clj Exception
              :cljs :default) e false)))

(def ^:const regex #"^[0-9]{3}\.[0-9]{3}\.[0-9]{3}-[0-9]{2}$")

(defn formatted?
  "Is the cpf formatted correctly?"
  [cpf]
  (try
    (boolean
      (and (string? cpf) (re-find regex cpf)))
    (catch #?(:clj Exception
              :cljs :default) e false)))

(defn format
  "Returns a string of the correctly formatted cpf"
  [cpf]
  (shared/format length {3 ".", 6 ".", 9 "-"} cpf))

(defn generate
  "Generates a random valid cpf"
  []
  (let [digits (shared/rand-digits (- length 2))]
    (apply str (concat digits (control-digits digits)))))

(defn generate-int
  "Generate a valid cpf integer, because life makes me sad"
  []
  (Long/parseLong (generate)))

(defn generate-formatted
  "Generates a random valid formatted cpf"
  []
  (format (generate)))

(s/def ::valid?
  (s/with-gen valid? #(gen/return (generate))))

(s/def ::valid-int?
  (s/with-gen valid-int? #(gen/return (generate-int))))

(s/def ::formatted?
  (s/with-gen formatted? #(gen/return (generate-formatted))))

(comment (s/valid? ::valid? (-> ::valid? s/gen gen/generate)))
(comment (s/valid? ::valid-int? (-> ::valid-int? s/gen gen/generate)))
(comment (s/valid? ::formatted? (-> ::formatted? s/gen gen/generate)))

