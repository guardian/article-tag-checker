(ns article-tag-checker.core-test
  (:require [clojure.test :refer :all]
            [article-tag-checker.core :refer :all]))

(deftest a-test
  (testing "tag extraction"
    (let [tags (extract_tags "<p>Hello <a href=\"/test\"></a></p>")]
    (and
        (is (some #( = :a %) tags))
        (is (some #( = :p %) tags))))))
