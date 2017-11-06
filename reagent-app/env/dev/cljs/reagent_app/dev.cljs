(ns ^:figwheel-no-load reagent-app.dev
  (:require
    [reagent-app.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
