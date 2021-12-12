(ns core
  (:require [clojure.string :as s]))

(defn indexes-of [source value from-index]
  (let [index (if from-index (s/index-of source value from-index))]
    [index (if index (+ index (count value)))]))

(defn split-source [source open-token close-token]
  (let [[begin-open after-open] (indexes-of source open-token 0)
        [begin-close after-close] (indexes-of source close-token after-open)]
    (if begin-close
      (cons
        [(subs source 0 begin-open) (subs source after-open begin-close)]
        (split-source
          (subs source after-close)
          open-token
          close-token))
      (list [source ""]))))

(defn add-to-blocks [blocks line]
  (cond
    (and (s/blank? line) (not (s/blank? (first blocks))))
    (cons "" blocks)
    (and (s/blank? (first blocks)) (not (s/blank? line)))
    (cons line blocks)
    :else
    (cons
      (str (first blocks) line)
      (rest blocks))))

(defn build-blocks [lines]
  (reverse (reduce add-to-blocks '() lines)))

(defn process-block [block]
  (if (s/blank? block) "(break)" (str "\"" block "\"")))

(defn process-marked [chunk]
  (if (s/blank? chunk)
    ""
    (->>
      (s/split chunk #"(?<=\n)")
      build-blocks
      (map process-block)
      s/join)))


(defn join-source [chunks]
  (str (nth chunks 0) (process-marked (nth chunks 1))))

(defn transform [source]
  (->>
    (split-source source "%<" ">%")
    (map join-source)
    s/join))

(defn filter-files [name-suffix files]
  (filter #(s/ends-with? (.getName %) name-suffix) files))

(defn eval-file [file-path]
  (println file-path)
  (->> file-path slurp transform read-string eval))

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
  (load-file "content/core.defs")
  ;(println (str "(def documents (list" (s/join " " (walk-files make-doc-def)) "))"))
  (eval (read-string (str "(def documents (list" (s/join " " (walk-files make-doc-def)) "))")))
  (walk-files write-file))
