(ns article-tag-checker.core)

(def valid-scribe-tags
	[:code :strong :b :em :i :strike :a :ul :ol :li :blockquote :h2 :sub :sup])
(def valid-scribe-attributes {:a [:href] :blockquote :class})

(def analysis (atom {}))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
