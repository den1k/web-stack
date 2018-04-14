(ns vimsical.web-stack.router
  (:require [vimsical.web-stack.config :as config]
            [mount.core :refer [defstate]]
            [ring.util.response :as res]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [bidi.ring]))

(def default-config
  {::routes    {#{"" "/"}   (fn [_]     ;; when not-found serves index
                              (println "REMOTE DISPATCH")
                              (-> "index.html"
                                  (res/resource-response {:root "public"})
                                  (res/content-type "text/html")))
                "/dispatch" (fn [_]
                              (println "REMOTE DISPATCH")
                              (res/response "dispatchhhhh"))

                ;true        (fn [_] (res/redirect "/"))
                }
   :middleware [[wrap-defaults api-defaults]
                wrap-params]})

(defn- wrap-routes [r]
  "Puts routes map into a vector so that bidi can match em."
  ["" r])

(defn- middleware-fn
  "Middleware are specified in a vector, either as standalone
  functions (when they don't take arguments other than the handler),
  or as vectors where the function come first, and the arguments
  next.

  Example:

  (new-middleware {:middleware [wrap-restful-format
                                [wrap-defaults site]
                                [wrap-not-found (html/not-found)]]})"
  [entry]
  (if (vector? entry)
    #(apply (first entry) % (rest entry))
    entry))

(defn- compose-middleware
  "Explanation for reverse:
  https://github.com/duct-framework/duct/issues/31#issuecomment-171459482"
  [entries]
  (apply comp (map middleware-fn (reverse entries))))

(defn start-router [{::keys [routes middleware]}]
  (let [middleware (compose-middleware middleware)]
    (-> routes
        wrap-routes
        bidi.ring/make-handler
        middleware)))

(declare router-component)
(defstate router-component
  :start (start-router config/config-component))
