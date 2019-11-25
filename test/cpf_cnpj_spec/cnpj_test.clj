(ns cpf-cnpj-spec.cnpj-test
  (:require [cpf-cnpj-spec.cnpj :as cnpj]
            [clojure.test :refer :all]))

(deftest validation-test
  (testing "valid-cnpjs"
    (are [cnpj] (cnpj/valid? cnpj)
         "36564023000176"
         "46303783000183"
         "48741030000185"
         "76887562000160"))

  (testing "valid-int-cnpjs"
    (are [cnpj] (cnpj/valid-int? cnpj)
         36564023000176
         46303783000183
         48741030000185
         76887562000160))

  (testing "valid-cnpjs"
    (are [cnpj] (cnpj/formatted? cnpj)
         "36.564.023/0001-76"
         "46.303.783/0001-83"
         "48.741.030/0001-85"
         "76.887.562/0001-60"))

  (testing "invalid cnpjs"
    (are [cnpj] (not (cnpj/formatted? cnpj))
         "36.564.025/0001-76"
         "46.303.783/0001-33"
         "48.741.230/0001-85"
         "75.887.562/0001-60")))

(deftest format-test
  (testing "formats right"
    (is (= (cnpj/format "12345678901234") "12.345.678/9012-34"))
    (is (= (cnpj/format "75-887-562x0001xxx60") "75.887.562/0001-60")))

  (testing "incomplete cnpj"
    (is (= (cnpj/format "123") "12.3"))
    (is (= (cnpj/format "123456789") "12.345.678/9")))

  (testing "too long cnpj"
    (is (= (cnpj/format "1234567890123456") "12.345.678/9012-34"))))

(deftest gen-test
  (testing "generating a cnpj with branch number"
    (are [branch-int branch-str] (= branch-str
                                    (second (re-find #"/([0-9]{4})-"
                                                     (cnpj/generate-formatted branch-int))))
         1 "0001"
         5 "0005"
         120 "0120"
         9999 "9999")))
