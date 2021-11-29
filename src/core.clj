(ns core)

(defn filter-files [name-suffix files]
  (filter #(clojure.string/ends-with? (.getName %) name-suffix) files))

(defn eval-file [file-path]
  (println file-path)
  (load-file file-path))

(defn get-doc-data [f]
  (let
    [doc-name (clojure.string/replace (.getName f) "page" "html")
     doc-path (str "docs/" doc-name)
     doc-data (->> f .getPath eval-file)]
    (assoc doc-data :name doc-name :path doc-path)))

(defn write-file [doc-data]
  (spit (doc-data :path) (doc-data :body)))

(defn make-doc-def [doc-data]
  (str "(list \"" (doc-data :name) "\" \"" (doc-data :title) "\")"))

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
  ;(println (str "(def documents (list" (clojure.string/join " " (walk-files make-doc-def)) "))"))
  (eval (read-string (str "(def documents (list" (clojure.string/join " " (walk-files make-doc-def)) "))")))
  (walk-files write-file))
