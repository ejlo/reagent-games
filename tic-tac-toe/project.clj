(defproject tic-tac-toe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs"]
  :template-additions ["README.md"
                       "LICENSE"
                       ".gitignore"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [reagent "0.5.0-alpha"]
                 [reagent-utils "0.1.2"]
                 [cljsjs/react "0.12.2-5"]
                 [garden "1.2.5"]
                 [org.clojure/clojurescript "0.0-2740" :scope "provided"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.cemerick/piggieback "0.1.5"]
                 [weasel "0.5.0"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [prone "0.8.0"]
                 [compojure "1.3.1"]
                 [environ "1.0.0"]
                 [leiningen "2.5.1"]
                 [figwheel "0.2.2-SNAPSHOT"]
                 [ring-mock "0.1.5"]
                 [ring/ring-devel "1.3.2"]
                 [pjstadig/humane-test-output "0.6.0"]
                 [prismatic/dommy "1.0.0"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-garden "0.2.5"]
            [lein-environ "1.0.0"]
            [lein-ring "0.9.1"]
            [lein-asset-minifier "0.2.2"]
            [lein-exec "0.3.4"]
            [lein-pdo "0.1.1"]
            [com.cemerick/clojurescript.test "0.3.3"]
            [lein-figwheel "0.2.2-SNAPSHOT"]
            [cider/cider-nrepl "0.9.0-SNAPSHOT"]]

  :ring {:handler tic-tac-toe.server.handler/app}

  :min-lein-version "2.5.0"

  :clean-targets  ^{:protect false} ["target/"
                                     "resources/public/js/"
                                     "resources/public/css/"]

  :minify-assets
  {:dev
   {:assets {"resources/public/css/site.min.css" ["target/css/site.css"
                                                  "target/css/dev.css"]}
    :options {:linebreak 0}}

   :production
   {:assets {"resources/public/css/site.min.css" "target/css/site.css"}}}

  :aliases {"server"   ["ring" "server"]
            "css"      ["garden" "auto" "dev"]
            "minify"   ["minify-assets" "watch" "dev"]
            "autotest" ["cljsbuild" "auto" "test"]
            "test"     ["cljsbuild" "once" "test"]
            "web"      ["with-profile" "production" "trampoline" "ring" "server"]
            "prod"     ["with-profile" "production" "do"
                        "clean,"
                        "garden" "once" "site,"
                        "minify-assets" "production,"
                        "cljsbuild" "once" "app"]
            "live"     ["pdo" "css," "minify," "figwheel," "server"]
            "once"     ["do" "cljsbuild" "once" "app,"
                        "garden" "once,"
                        "minify-assets" "dev"]
            "dev"      ["do" "once," "live"]}

  :garden {:builds [{:id "site"
                     :source-paths ["src/styles"]
                     :stylesheet tic-tac-toe.styles.site/site
                     :compiler {:output-to "target/css/site.css"
                                :pretty-print? true}}
                    {:id "dev"
                     :source-paths ["src/styles"]
                     :stylesheet tic-tac-toe.styles.dev/dev
                     :compiler {:output-to "target/css/dev.css"
                                :pretty-print? true}}]}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to "resources/public/js/app.js"}}
                       :test {:source-paths ["src/cljs" "src/env/test/cljs" "test"]
                              :notify-command ["phantomjs" "target/test/test.js"]
                              :compiler {:output-to "target/test/test.js"
                                         :optimizations :whitespace
                                         :pretty-print true
                                         :preamble ["templates/js/phantomjs_polyfills.js"]}}}
              :test-commands {"unit-tests" ["phantomjs" "target/test/test.js"]}}

  :profiles {:dev {:repl-options {:init-ns tic-tac-toe.server.handler
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :css-dirs ["resources/public/css"]
                              :server-logfile "logs/figwheel.log"
                              :repl false}

                   :env {:dev? true}

                   :cljsbuild
                   {:builds
                    {:app {:source-paths ["src/env/dev/cljs" "test"]
                           :compiler
                           {:main          "tic-tac-toe.dev"
                            :asset-path    "js/out"
                            :output-dir    "resources/public/js/out"
                            :optimizations :none
                            :source-map    "resources/public/js/out.js.map"
                            :pretty-print  true}}}}}

             :production {:ring {:open-browser? false
                                 :stacktraces?  false
                                 :auto-reload?  false}
                          :env {:production true}
                          :cljsbuild
                          {:builds
                           {:app
                            {:source-paths ["src/env/prod/cljs"]
                             :compiler
                             {:main          "tic-tac-toe.prod"
                              :optimizations :advanced
                              :pretty-print  false}}}}}})
