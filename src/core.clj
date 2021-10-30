(ns core)

(defn do-it []
  (spit "docs/index.html" "Hello world
"))

(defn -main []
  (do-it))

