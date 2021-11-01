(ns core)

(defn publish-file [f]
  (let
    [doc-path (str "docs/" (clojure.string/replace (.getName f) "page" "html"))]
    (spit doc-path (eval (read-string (slurp (.getPath f)))))
    doc-path))

(defn -main []
  (->> "content" clojure.java.io/file .listFiles (map publish-file) doall))

