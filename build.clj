(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'org-analyzer)
(def version "1.0.4")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

(defn clean []
  (println "Cleaning target directory...")
  (b/delete {:path "target"})
  (println "Clean complete."))

(defn compile-clj []
  (println "Compiling Clojure source files...")
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (println "Clojure compilation complete."))

(defn compile-cljs []
  (println "Starting ClojureScript compilation with Figwheel...")
  (b/process {:command-args ["clj" "-M:cljs-prod"]})
  (println "ClojureScript compilation complete."))

(defn uber [_]
  (clean)
  (compile-cljs)
  (b/copy-dir {:src-dirs ["resources"]
               :target-dir class-dir})

  (compile-clj)
  (println "Creating uberjar...")
  (b/uber {:class-dir class-dir
             :uber-file uber-file
             :basis basis
             :main 'org-analyzer.main})
  (println "Uberjar creation complete."))
