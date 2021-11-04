(ns core)

(defn filter-files [name-suffix files]
  (filter #(clojure.string/ends-with? (.getName %) name-suffix) files))

(defn eval-file [file-path]
  (println file-path)
  (load-file file-path))

(defn publish-file [f]
  (let
    [doc-path (str "docs/" (clojure.string/replace (.getName f) "page" "html"))]
    (->> f .getPath eval-file (spit doc-path))))

(defn -main []
  (->> "content/publish.css" slurp (spit "docs/publish.css"))
  (eval-file "content/core.defs")
  (->> "content"
       clojure.java.io/file
       .listFiles
       (filter-files ".page")
       (map publish-file)
       doall))
