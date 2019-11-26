# cpf-cnpj.spec

Specs para validar e gerar CPFs e CNPJs valido usando clojure.spec.

Baseado em [https://github.com/madstap/cadastro-de-pessoa](cadastro-de-pessoa)

## Usage

```clojure
(ns your.namespace
  (:require [cpf-cnpj-spec.cpf :as cpf]
            [cpf-cnpj-spec.cnpj :as cnpj]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/valid? ::cpf/valid? (-> ::cpf/valid? s/gen gen/generate))
(s/valid? ::cpf/valid-int? (-> ::cpf/valid-int? s/gen gen/generate))
(s/valid? ::cpf/formatted? (-> ::cpf/formatted? s/gen gen/generate))

(s/valid? ::cnpj/valid? (-> ::cnpj/valid? s/gen gen/generate))
(s/valid? ::cnpj/valid-int? (-> ::cnpj/valid-int? s/gen gen/generate))
(s/valid? ::cnpj/formatted? (-> ::cnpj/formatted? s/gen gen/generate))
```

## License

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
