(ns core-test
  (:require [clojure.test :refer :all]
            [core :refer :all]))

(deftest split-with-no-delimiters
  (is (= '(["source" ""]) (split-source "source" "%<" ">%"))))

(deftest split-empty-source
  (is (= '(["" ""]) (split-source "" "%<" ">%"))))

(deftest split-with-one-delimiter
  (is (= '(["source%<stuff" ""]) (split-source "source%<stuff" "%<" ">%"))))

(deftest split-with-one-delimiter-at-end
  (is (= '(["source%<" ""]) (split-source "source%<" "%<" ">%"))))

(deftest split-with-both-delimiters
  (is (= '(["source" "stuff"] ["more" ""]) (split-source "source%<stuff>%more" "%<" ">%"))))

(deftest split-with-empty-delimited
  (is (= '(["source" ""] ["more" ""]) (split-source "source%<>%more" "%<" ">%"))))

(deftest split-with-nothing-outside-delimited
  (is (= '(["" "stuff"] ["" ""]) (split-source "%<stuff>%" "%<" ">%"))))

(deftest split-with-multiple-delimited
  (is (= '(["some" "stuff"] ["and" "also"] ["more" ""])) (split-source "some%<stuff>%and%<also>%more" "%<" ">%")))

(deftest split-with-only-multiple-delimited
  (is (= '(["" "stuff"] ["" "more"] ["" ""]) (split-source "%<stuff>%%<more>%" "%<" ">%"))))


