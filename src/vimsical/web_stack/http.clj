(ns vimsical.web-stack.http
  (:require
   [vimsical.web-stack.config :refer [config-component]]
   [vimsical.web-stack.router :as router]
   [immutant.web :as web]
   [immutant.web.undertow :as uw]
   [mount.core :refer [defstate]]
   [environ.core :refer [env]]))

(def default-config
  {::port (Integer. (or (env :port) 8020))})

(defn start-http [{::keys [port]} handler]
  (println "Starting Web Server on port " port "...")
  (web/run handler (uw/options {:port port :host "0.0.0.0"})))

(defn stop-http [server]
  (println "Stopping Web Server")
  (web/stop server))

(declare http-component)
(defstate http-component
  :start (start-http config-component router/router-component)
  :stop (stop-http http-component))
