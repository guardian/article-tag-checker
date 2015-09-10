(ns article-tag-checker.core)

(def valid-scribe-tags
	[:code :strong :b :em :i :strike :a :ul :ol :li :blockquote :h2 :sub :sup])

(def valid-scribe-attributes {:a [:href] :blockquote :class})

(def analysis (atom {}))

(defn extract_tags [html_string]
	[:a])

(defn read_capi [from to]
	)

(defn analyse_content [capi_results]
	)

(defn write_results [analysed_capi_results]
	)

(defn foo
  [x]
  (->> (read_capi)
  	(analyse_content)
	(write_results)))
