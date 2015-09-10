(ns article-tag-checker.core
	(:require [hickory.core :as hickory]
		[clojure.set :as set-ops]))

(def valid-scribe-tags
	#{:code :strong :b :em :i :strike :a :ul :ol :li :blockquote :h2 :sub :sup :p :br})

(def valid-scribe-attributes #{:a [:href] :blockquote :class})

(def analysis (atom {}))

(defn valid-flexible-content [tag-set]
	(set-ops/subset? tag-set valid-scribe-tags))

(defn extract-tags [html-string]
	(->> (tree-seq #(not-empty (:content %)) :content (hickory/as-hickory (hickory/parse html-string)))
		(filter #(= :element (:type %)))
		(map :tag)
		set
		))

(defn read-capi [from to]
	)

(defn analyse-content [capi-results]
	)

(defn write-results [analysed-capi-results]
	)

(defn foo
  [x]
  (->> (read-capi)
  	(analyse-content)
	(write-results)))
