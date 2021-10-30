(ns core)

(defn -main []
  (eval (read-string "(defn do-it[] (spit \"docs/index.html\" \"Hello world\"))"))
  (eval (read-string "(do-it)")))

