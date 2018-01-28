(ns vimsical.web-stack.db
  (:require [datomic.client.api :as d]
            [mount.core :refer [defstate]]
            [vimsical.web-stack.config :refer [config-component]]))

(defn start-client [{::keys [config]}]
  (d/client config))

(declare db-client db-conn)
(defstate db-client
  :start (start-client config-component))

(defn start-conn [client {::keys [name]}]
  (d/connect client {:db-name name}))

(defstate db-conn
  :start (start-conn db-client config-component))
