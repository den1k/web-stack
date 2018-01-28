(ns vimsical.web-stack.abc
  (:require [reagent.core :as reagent])
  )

(defn app []
  [:h1 "APPPPPP"])

(defn mount []
  (reagent/render [app] (js/document.getElementById "root")))

(defn init []
  (mount)
  )
