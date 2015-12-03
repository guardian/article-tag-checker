(ns article-tag-checker.core-test
  (:require [clojure.test :refer :all]
            [article-tag-checker.core :refer :all]))

(deftest parsing
  (testing "tag extraction"
    (let [tags (extract-tags "<p>Hello <a href=\"/test\"></a></p>")]
    (and
        (is (some #( = :a %) tags))
        (is (some #( = :p %) tags))))))

(deftest content-checking
    (testing "valid tags"
        (is (valid-flexible-content? #{:p :a :b}))
    (testing "invalid tags"
        (is (not (valid-flexible-content? #{:script}))))))
