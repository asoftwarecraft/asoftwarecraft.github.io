(ns core
  (:require [clojure.string :as s]))

; find delimited chunks of source and process with a fn
; source 2 delimiters function -> result
; split source -> list of plain, delimited, plain, ...
; %<   >%
(defn split-source [source start-delimiter end-delimiter]
  (let [start-index (s/index-of source start-delimiter)]
    (if (nil? start-index)
      (list (list source ""))
      (let [after-start (+ start-index (count start-delimiter))
            end-index (s/index-of source end-delimiter after-start)]
        (if (nil? end-index)
          (list (list source ""))
          (cons
            (list (subs source 0 start-index) (subs source after-start end-index))
            (split-source
              (subs source (+ end-index (count end-delimiter)))
              start-delimiter
              end-delimiter)))))))

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
  (println (str "(def documents (list" (s/join " " (walk-files make-doc-def)) "))"))
  (eval (read-string (str "(def documents (list" (s/join " " (walk-files make-doc-def)) "))")))
  (walk-files write-file))
