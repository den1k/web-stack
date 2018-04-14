(ns vimsical.web-stack.routes.db
  (:require [ring.util.response :as res]
            [vimsical.web-stack.db :as db]
            [datomic.client.api :as d]))

(defn q-handler [req]
  (let [q      (-> req :body slurp clojure.edn/read-string)
        result (d/q q (d/db db/db-conn))]
    (res/response (pr-str result))))

(def q {"/q" q-handler})
