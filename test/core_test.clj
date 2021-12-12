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

(deftest build-non-blank
  (is (= '("somestuff") (build-blocks '("some" "stuff")))))

(deftest build-with-blank
  (is (= '("somestuff" "") (build-blocks '("some" "stuff" "")))))

(deftest build-skips-multiple-blanks
  (is (= '("somestuff" "") (build-blocks '("some" "stuff" "" "")))))

(deftest build-multiple-non-blank-blocks
  (is (= '("somemore" "" "stuff") (build-blocks '("some" "more" " " "stuff")))))

(deftest transform-no-markup
  (is (= "stuff" (transform "stuff"))))

(deftest transform-simple-markup
  (is (= "more\"stuff\"" (transform "more%<stuff>%"))))

(deftest transform-multiple-markup
  (is (= "more\"stuff\"and\"still\"going" (transform "more%<stuff>%and%<still>%going"))))

(deftest transform-markup-with-blank-lines
  (is (= "(some \"more\n\"(break)\"stuff\")" (transform "(some %<more\n\nstuff>%)"))))
