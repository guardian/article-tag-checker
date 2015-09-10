(ns article-tag-checker.core-test
  (:require [clojure.test :refer :all]
            [article-tag-checker.core :refer :all]))

(deftest a-test
  (testing "tag extraction"
    (is (= [:a] (extract_tags "<p>Hello <a href=\"/test\"></a></p>")))))
