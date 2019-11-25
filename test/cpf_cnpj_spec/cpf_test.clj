(ns cpf-cnpj-spec.cpf-test
  (:require [cpf-cnpj-spec.cpf :as cpf]
            [clojure.test :refer :all]))

(deftest cpf-test
  (testing "valid-cpfs"
    (are [cpf] (cpf/valid? cpf)
         "43764783850"
         "80148339107"
         "72540332250"
         "27060497315"))

  (testing "invalid cpfs"
    (are [cpf] (not (cpf/valid? cpf))
         "43764783550"
         "43767483850"
         "34764783850"
         "27060497325"
         "27060487315"
         "66666666666")))

(deftest formatted?-test
  (testing "formatted right"
    (are [cpf] (cpf/formatted? cpf)
         "270.604.973-25"
         "270.604.873-15"
         "270.604.873-15"
         "666.666.666-66"))

  (testing "formatted wrong"
    (are [cpf] (not (cpf/formatted? cpf))
         "270.604.973x25"
         "27.060.4873-15"
         "270.604.87csd3-15"
         "66666666666")))

(deftest format-test
  (testing "formats right"
    (is (= (cpf/format "01234567890") "012.345.678-90"))
    (is (= (cpf/format "27.060.4873-15") "270.604.873-15")))

  (testing "incomplete cpf"
    (is (= (cpf/format "1234") "123.4"))
    (is (= (cpf/format "123456789") "123.456.789"))
    (is (= (cpf/format "1234567890") "123.456.789-0")))

  (testing "too long cpf"
    (is (= (cpf/format "1234567890123456") "123.456.789-01"))))
