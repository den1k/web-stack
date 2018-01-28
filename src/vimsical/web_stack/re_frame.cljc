(ns vimsical.web-stack.re-frame
  (:require [re-frame.core :as re-frame]
   #?@(:cljs [[cljs.reader :as reader]
              [goog.events :as gevents]]))
  #?(:cljs
     (:import [goog.net XhrIo]
      [goog.events EventType])))

(re-frame/reg-event-db :assoc
  (fn [db [_ k v]]
    (assoc db k v)))

(re-frame/reg-event-db :assoc-in
  (fn [db [_ ks v]]
    (assoc-in db ks v)))

(re-frame/reg-event-db :error
  (fn [db [_ & args]]
    (update db :errors conj args)))

(re-frame/reg-sub :d/q
  (fn [db [_ q-name]]
    (get-in db [::by-query-name q-name])))

(def ^:private meths
  {:get    "GET"
   :put    "PUT"
   :post   "POST"
   :delete "DELETE"})

(defn edn-xhr [{:as opts :keys [method url data on-complete]}]
  #?(:cljs
     (let [xhr (XhrIo.)]
       (gevents/listen
        xhr goog.net.EventType.COMPLETE
        (fn [e]
          (on-complete (reader/read-string (.getResponseText xhr)))))

       (doto xhr
         (.send url (meths method) (when data (pr-str data))
                #js {"Content-Type" "application/edn"}
                ;#js {}
                )
         ))))

(re-frame/reg-fx ::http
  (fn [{:keys [url method data remote-dispatch on-success-dispatch]}]
    ;; todo on-error-dispatch
    (edn-xhr
     {:url         url
      :method      method
      :data        data
      :on-complete #(re-frame/dispatch (conj on-success-dispatch %))})))

(re-frame/reg-event-fx :d/q
  (fn [{:keys [db]} [k name query force?]]
    (let [no-result-or-query-changed?
          (or (nil? (get-in db [::by-query-name name]))
              (not= query (get-in db [::query-by-name name])))]
    (re-frame.loggers/console :log :DB db
                              (or force? no-result-or-query-changed?))
      (cond-> {:db db}
        (or force? no-result-or-query-changed?)
        (merge {::http
                          {:method              :post
                           :url                 "//localhost:8020/q"
                           :data                query
                           :on-success-dispatch [:assoc-in [::by-query-name name]]}
                :dispatch [:assoc-in [::query-by-name name] query]})))))
