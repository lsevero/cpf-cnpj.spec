(defproject cpf-cnpj.spec "0.1.0"
  :description "Some utils for working with cpfs and cnpjs and clojure.spec."
  :url "https://github.com/lsevero/cpf-cnpj.spec"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.9.293"]
                 [org.clojure/test.check "0.10.0"]]
  :profiles {:dev {:plugins [[cider/cider-nrepl "0.22.3"]]
                   :repl-options {:init-ns cpf-cnpj-spec.cpf}
                   :source-paths ["src" "test"]}}
  :source-paths ["src"])
