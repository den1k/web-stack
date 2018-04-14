(ns vimsical.web-stack.dev
  (:require [mount.core :as mount]
            [vimsical.web-stack.util :as util]))

(defn loaded-namespaces [user-config]
  (into #{} (comp (map namespace) (map symbol)) (keys user-config)))

(defn default-config
  "Fetches default configs from namespaces of user-provided qualified keys.
  Merges into one config."
  [user-config]
  (let [loaded-ns' (loaded-namespaces user-config)]
    (apply require loaded-ns')
    ;; merge default configs
    (into {}
          (comp (keep (fn [ns] (resolve (symbol (str ns "/" 'default-config)))))
                (map var-get))
          loaded-ns')))

(defn filter-vars-from-loaded-namespaces [config symbol-seq]
  (let [loaded-ns' (loaded-namespaces config)]
    (into #{}
          (comp (filter #(contains? loaded-ns' (symbol (namespace %))))
                (map symbol)
                (map find-var))
          symbol-seq)))

(defn make-config [user-config]
  (util/deep-merge2 (default-config user-config) user-config))

(defn mount-start-map [user-config stub-component-vars]
  (let [config        (make-config user-config)
        filtered-vars (filter-vars-from-loaded-namespaces user-config stub-component-vars)
        m             (into {#'vimsical.web-stack.config/config-component config}
                            ; stub var keys have nil values
                            (map #(vector % nil))
                            filtered-vars)]
    m))

(defn start-map [user-config]
  (mount-start-map user-config []))

(defn start [user-config]
  (mount/start-with (mount-start-map user-config [])))

(def stop mount/stop)

(defn reset [user-config]
  (stop)
  (start user-config))

(defn start-all [user-config]
  (mount/start-with (mount-start-map user-config
                                ['vimsical.web-stack.http/http-component])))

(def stop-all mount/stop)

(defn reset-all [config]
  (stop-all)
  (start-all config))

(defn reset-backend [user-config]
  (apply mount/stop-except
         (filter-vars-from-loaded-namespaces
          user-config ['vimsical.web-stack.build/watch-component]))
  (start-all user-config))


;(comment
; (def user-config
;   {::build/config      {:app-init-fn 'vimsical.web-stack.abc/init
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

