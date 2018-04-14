(defproject vimsical/web-stack "0.1.01"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.7.1"
  :dependencies [[org.clojure/clojure "1.9.0"
                  :exclusions [org.clojure/spec.alpha]]
                 [org.clojure/spec.alpha "0.1.143"]
                 [org.clojure/test.check "0.9.0"]

                 [org.clojure/tools.reader "1.2.1"]

                 ;;; System
                 [mount "0.1.11"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [environ "1.1.0"]

                 ;;; DB
                 [com.datomic/client-cloud "0.8.50"]

                 ;;; Web

                 ;; Backend
                 [org.immutant/web "2.1.10"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 ;; needed?
                 [ring/ring-json "0.4.0" :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [bidi "2.1.3"]
                 [hiccup "1.0.5"]

                 ;; Frontend
                 [thheller/shadow-cljs "2.2.29"
                  ;:exclusions [com.fasterxml.jackson.core/jackson-core
                  ;             org.clojure/spec.alpha]
                  ]
                 [org.clojure/clojurescript "1.10.238"]
                 [binaryage/devtools "0.9.9"]
                 [reagent "0.8.0-alpha2"]

                 [re-frame "0.10.3"]

                 ;;; Async
                 [org.clojure/core.async "0.4.474"]

                 ;;; Utils
                 [medley "1.0.0"]
                 [camel-snake-kebab "0.4.0"]

                 ]

  :src-paths ["src"]

  :plugins [[lein-environ "1.1.0"]]

  ;:repl-options {:init-ns user
  ;               :init    (start!)}

  :profiles
  {:dev
   {:dependencies
    []}})
