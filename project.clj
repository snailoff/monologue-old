(defproject monologue "0.1.0-SNAPSHOT"
  :dependencies [[cljs-http "0.1.46"]
                 [com.bhauman/rebel-readline "0.1.4"]
                 [compojure "1.6.1"]
                 [markdown-clj "1.10.0"]
                 [metosin/compojure-api "1.1.11"]
                 [nrepl "0.6.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.postgresql/postgresql "42.2.5"]
                 [prismatic/schema "1.1.10"]
                 [reagent "0.7.0"]
                 [ring "1.7.1"]
                 [ring/ring-json "0.4.0"]
                 [secretary "1.2.3"]
                 [toucan "1.12.0"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-ring "0.12.5"]
            [lein-auto "0.1.3"]]

  :clean-targets ^{:protect false} ["resources/public/js"
                                    "target"]
;;  :main monologue.core
  :ring {:handler monologue.core/rr-app}


  :figwheel {:css-dirs ["resources/public/css"]}
  :auto {:default {:file-pattern #"\.(clj|cljs|cljx|cljc|edn)$"
                   :wait-time 3}}

  :profiles
  {:dev
   {:dependencies []

    :plugins      [[lein-figwheel "0.5.15"]]
    }}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "monologue.core/reload"}
     :compiler     {:main                 monologue.core
                    :optimizations        :none
                    :output-to            "resources/public/js/app.js"
                    :output-dir           "resources/public/js/dev"
                    :asset-path           "js/dev"
                    :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            monologue.core
                    :optimizations   :advanced
                    :output-to       "resources/public/js/app.js"
                    :output-dir      "resources/public/js/min"
                    :elide-asserts   true
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    ]})
