(ns cpf-cnpj-spec.cnpj
  (:refer-clojure :exclude [format])
  (:require [cpf-cnpj-spec.shared :as shared]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(def ^:const length 14)

(def ^:const ^:private mask1   [5 4 3 2 9 8 7 6 5 4 3 2])
(def ^:const ^:private mask2 [6 5 4 3 2 9 8 7 6 5 4 3 2])

(def control-digits (partial shared/control-digits mask1 mask2))

(def ^:const repeated
  "A set of cnpjs with repeated digits that are
  considered valid by the algorithm, but normally shouldn't count as valid."
  (set (for [i (range 10)
             :let [xs (repeat (- length 2) i)]]
         (concat xs (control-digits xs)))))

(defn- valid-impl?
  "Takes a string. Returns true if valid, else false.
  Does not validate formatting."
  [cnpj]
  (try
    (let [cnpj (shared/parse cnpj)
          [digits control] (shared/split-control cnpj)]
      (and (= length (count cnpj))
           (not (repeated cnpj))
           (= control (control-digits digits))))
    (catch #?(:clj Exception
              :cljs :default) e false)))

(defn valid?
  "Takes a string. Returns true if valid, else false."
  [cnpj]
  (try 
    (if (= (count cnpj) length)
      (valid-impl? cnpj)
      false)
    (catch #?(:clj Exception
              :cljs :default) e false)))

(defn valid-int?
  "Takes a integer and validade it as a cnpj"
  [cnpj]
  (try
    (valid-impl? (clojure.core/format (str "%0" length "d") cnpj))
    (catch #?(:clj Exception
              :cljs :default) e false)))

(def ^:const regex #"^[0-9]{2}\.[0-9]{3}\.[0-9]{3}/[0-9]{4}-[0-9]{2}$")

(defn formatted?
  "Is the cnpj formatted correctly?"
  [cnpj]
  (try
    (boolean
      (and (string? cnpj) (re-find regex cnpj) (valid-impl? cnpj)))
    (catch #?(:clj Exception
              :cljs :default) e false)))

(defn format
  "Returns a string of the correctly formatted cnpj"
  [cnpj]
  (shared/format length {2 ".", 5 ".", 8 "/", 12 "-"} cnpj))

(defn generate
  "Generates a random valid cnpj.
  An integer argument can be given to choose headquarters or a branch.
  (Matriz ou filial)
  In a cnpj xx.xxx.xxx/0001-xx, 0001 is the branch number,
  in this case headquarters."
  ([]
   (let [digs (shared/rand-digits (- length 2))]
     (apply str (concat digs (control-digits digs)))))
  ([branch]
   {:pre [(< 0 branch 10e3) (integer? branch)]}
   (let [digs (shared/rand-digits (- length 2 4))
         branch-digs (shared/left-pad 4 0 (shared/digits branch))
         digs' (concat digs branch-digs)]
     (apply str (concat digs' (control-digits digs'))))))

(defn generate-int
  "Generates a random valid cnpj as a integer, because life sucks"
  ([]
   (Long/parseLong (generate)))
  ([branch]
   (Long/parseLong (generate branch))))

(defn generate-formatted
  "Generates a random valid cnpj.
  An integer argument can be given to choose headquarters or a branch.
  (Matriz ou filial)
  In a cnpj xx.xxx.xxx/0001-xx, 0001 is the branch number,
  in this case headquarters."
  ([]
   (format (generate)))
  ([branch]
   {:pre [(< 0 branch 10e3) (integer? branch)]}
   (format (generate branch))))

(s/def ::valid?
  (s/with-gen valid? #(gen/return (generate))))

(s/def ::valid-int?
  (s/with-gen valid-int? #(gen/return (generate-int))))

(s/def ::formatted?
  (s/with-gen formatted? #(gen/return (generate-formatted))))

(comment (s/valid? ::valid? (-> ::valid? s/gen gen/generate)))
(comment (s/valid? ::valid-int? (-> ::valid-int? s/gen gen/generate)))
(comment (s/valid? ::formatted? (-> ::formatted? s/gen gen/generate)))
