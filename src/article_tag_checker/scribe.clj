(ns article-tag-checker.scribe)

(def valid-tags
  #{:code :strong :b :em :i :strike :a :ul :ol :li :blockquote :h2 :sub :sup :p :br})

(def valid-attributes #{:a [:href] :blockquote :class})