(ns vimsical.web-stack.dev
  (:require [mount.core :as mount]
            [vimsical.web-stack.http :as http]
            [vimsical.web-stack.config :as config]
            ;[vimsical.web-stack.db :as db] ;; add because overwriting http
            [vimsical.web-stack.build :as build]

            [vimsical.web-stack.util :as util]))

(defn default-config
  "Fetches default configs from namespaces of user-provided qualified keys.
  Merges into one config."
  [user-config]
  (let [user-ns (into #{} (comp (map namespace) (map symbol)) (keys user-config))]
    (apply require user-ns)
    ;; merge default configs
    (into {}
          (comp (keep (fn [ns] (resolve (symbol (str ns "/" 'default-config)))))
                (map var-get))
          user-ns)))

(defn make-config [user-config]
  (util/deep-merge2 (default-config user-config) user-config))

(defn start-dev [user-config]
  (let [config (make-config user-config)]
    (mount/start-with {#'config/config-component config
                       #'http/http-component     {}})))

(def stop-dev mount/stop)

(defn reset-dev [config-or-update-fn]
  (stop-dev)
  (start-dev config-or-update-fn))

(defn make-release [config-or-update-fn]
  (let [config (make-config config-or-update-fn)]
    (mount/start-with {#'config/config-component config
                       #'http/http-component     nil
                       #'build/watch-component   nil})
    (build/release config)))

;(comment
; (def user-config
;   {::db/name           "test-db"
;
;    ::build/config      {:app-init-fn 'vimsical.web-stack.abc/init
;                         :stylesheets ["//cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.12/semantic.min.css"]}
;    ::build/shadow-cljs {:lein   true
;                         :builds {:app
;                                  {:modules
;                                             {:main
;                                              {:entries
;                                               '[vimsical.web-stack.abc]}}
;                                   :devtools {:after-load 'vimsical.web-stack.abc/mount}}}}
;
;    ::http/port         8020
;    ::router/routes     {}
;    })

 ;(start-dev user-config)
 ;(stop-dev)
 ;(reset-dev user-config))

;(make-release config)

