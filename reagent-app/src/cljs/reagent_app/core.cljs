(ns reagent-app.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))



;; -------------------------
;; Views

(def value (atom "Hello"))
(def items (atom (sorted-map)))

(defn my-input-field []
  [:input {:type "test" 
           :value @value 
           :on-key-press (fn [e]
                         (println "key press" (.-charCode e))
                         (println @items)
                         (when (= 13 (.-charCode e))
                           (swap! items assoc (-> @items keys last inc) @value)))
           :on-change #(reset! value (-> % .-target .-value))}])


(defn li [[index text]]
  [:li
   {:data-index index
    :on-click (fn [] (swap! items dissoc index))}
   text]
  )

(defn generate-items []
  (->> @items (map li) vec (into [:ul])))

(defn my-header [& children]
  [:div children])

(defn home-page []
  [:div
   [my-header [:h1 "Title"] [:h2  "The subtitle"]]
   [:h2 "Welcome to reagent-app"]
   ;[my-header [:h2 "Welcome to reagent-app"]]
   ; (generate-items)
   [generate-items]
   [my-input-field]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About reagent-app"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Routes

(def page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
