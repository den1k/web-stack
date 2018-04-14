(ns vimsical.web-stack.build
  (:require [shadow.cljs.devtools.config :as shc]
            [shadow.cljs.devtools.api :as shadow-api]
            [shadow.cljs.devtools.server :as shadow-server]
            [medley.core :as md]
            [mount.core :as mount :refer [defstate]]
            [vimsical.web-stack.config :as config]
            [hiccup.page :as html :refer [html5]]
            [clojure.string :as string]
            [camel-snake-kebab.core :as csk]
            [clojure.java.io :as io]
            [vimsical.web-stack.router :as router]))

(def default-config
  {::config      {:scripts ["/js/main.js"]}

   ::shadow-cljs {:lein
                  true
                  :builds
                  {:app
                   {:target     :browser
                    :output-dir "resources/public/js"
                    :asset-path "/js"
                    :nrepl      {:port 9000}
                    :devtools
                    ;; before live-reloading any code call this function
                                {
                                 ;:before-load starter.browser/stop

                                 ;; after live-reloading finishes call this function
                                 ;:after-load dashboard.core/mount

                                 ; serve the public directory over http at port 8020
                                 :http-root    "resources/public"
                                 ;; todo FIXME use top ::http/port from config
                                 :http-port    8020
                                 :http-handler `router/router-component
                                 ;:module-hash-names true
                                 :preloads     ['devtools.preload]}}}}})

(defn index-html-string
  [{:as   static-config
    :keys [app-init-fn scripts stylesheets]}]
  (html5
   [:head
    [:link {:rel "shortcut icon" :href "#"}]
    (apply html/include-css stylesheets)]
   [:body
    [:div {:id "root"}
     "Loading..."]
    (apply html/include-js scripts)
    (let [js-var (-> (str app-init-fn) csk/->snake_case (string/replace #"/" "."))]
      [:script (str js-var "()")])]))

(defn node-modules []
  (when-not (.exists (io/file "node_modules"))
    (println "Installing node modules...")
    (letfn [(print-fn [{:keys [out err]}]
              (when err (println "Errors:") (println err))
              (when out (println "Out:") (println out)))]
      (print-fn
       (clojure.java.shell/sh
        "npm" "install"
        "react" "react-dom" "create-react-class" "semantic-ui-react")))))

(defn start-static-resources [{::keys [config]}]
  (node-modules)
  (println "Making index.html")
  (clojure.java.io/make-parents "resources/public/index.html")
  (spit "resources/public/index.html"
        (index-html-string config)))


(defn with-shadow-config-fn [edn body]
  (with-redefs [shc/load-cljs-edn (constantly edn)]
    (body)))

(defmacro with-shadow-config [edn & body]
  `(with-shadow-config-fn ~edn #(do ~@body)))

(defn make-shadow-config
  [build-config]
  (merge
   (shc/load-system-config)
   (-> build-config
       (shc/normalize)
       (->> (merge shc/default-config))
       (update :builds #(merge shc/default-builds %)))))

(defn shadow-config
  [{::keys [shadow-cljs]}]
  (-> (make-shadow-config shadow-cljs)
      (shc/normalize)))

;; todo use target from config (`:app`)
(defn release [config]
  (start-static-resources config)
  (with-shadow-config
   (shadow-config config)
   (shadow-api/release :app)))

(defn watch [config]
  (start-static-resources config)
  (with-shadow-config
   (shadow-config config)
   (shadow-server/start!)
   (shadow-api/watch :app)))

(defn start-watch [config]
  (watch config))

(defn stop-watch []
  (shadow-server/stop!))

(declare watch-component)
(defstate watch-component
  :start (start-watch config/config-component)
  :stop (stop-watch))

