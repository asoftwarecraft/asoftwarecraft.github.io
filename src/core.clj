(ns core)

(defn filter-files [name-suffix files]
  (filter #(clojure.string/ends-with? (.getName %) name-suffix) files))

(defn eval-file [file-path]
  (println file-path)
  (->> file-path slurp read-string eval))

(defn publish-file [f]
  (let
    [doc-path (str "docs/" (clojure.string/replace (.getName f) "page" "html"))]
    (->> f .getPath eval-file (spit doc-path))))

(defn -main []
  (eval-file "content/core.defs")
  (->> "content"
       clojure.java.io/file
       .listFiles
       (filter-files ".page")
       (map publish-file)
       doall))
