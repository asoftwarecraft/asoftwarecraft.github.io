(ns core
  (:require [clojure.string :as s]))

(defn search-source [source value from-index]
  (let [index (if from-index (s/index-of source value from-index))]
    [index (if index (+ index (count value)))]))

(defn split-source [source open-token close-token]
  (let [[begin-open after-open] (search-source source open-token 0)
        [begin-close after-close] (search-source source close-token after-open)]
    (if begin-close
      (cons
        [(subs source 0 begin-open) (subs source after-open begin-close)]
        (split-source
          (subs source after-close)
          open-token
          close-token))
      (list [source ""]))))

(defn filter-files [name-suffix files]
  (filter #(s/ends-with? (.getName %) name-suffix) files))

(defn eval-file [file-path]
  (println file-path)
  (load-file file-path))

(defn get-doc-data [f]
  (let
    [doc-name (s/replace (.getName f) "page" "html")
     doc-path (str "docs/" doc-name)
     doc-data (->> f .getPath eval-file)]
    (assoc doc-data :name doc-name :path doc-path)))

(defn write-file [doc-data]
  (spit (doc-data :path) (doc-data :body)))

(defn make-doc-def [doc-data]
  (str "(hash-map :name \"" (doc-data :name) "\" :title \"" (doc-data :title) "\" :date \"" (doc-data :date) "\")"))

(defn walk-files [action]
  (->> "content"
       clojure.java.io/file
       .listFiles
       (filter-files ".page")
       (map get-doc-data)
       (map action)
       doall))

(defn -main []
  (->> "content/publish.css" slurp (spit "docs/publish.css"))
  (eval-file "content/core.defs")
  ;(println (str "(def documents (list" (s/join " " (walk-files make-doc-def)) "))"))
  (eval (read-string (str "(def documents (list" (s/join " " (walk-files make-doc-def)) "))")))
  (walk-files write-file))
