(ns repl
  (:require [clojure.tools.nrepl.server :refer [start-server]]))

(def port 5556)

(println "Starting NREPL on port" (str port "..."))
(start-server :port port)
(println "NREPL started")
