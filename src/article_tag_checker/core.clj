(ns article-tag-checker.core
	(:require [hickory.core :as hickory]
		[clojure.set :as set-ops]
		[clj-http.lite.client :as http]
		[cheshire.core :as json]
		[article-tag-checker.consts :as consts]))

(def standard-tags
	#{:html :head :body})

(def valid-scribe-tags
	#{:code :strong :b :em :i :strike :a :ul :ol :li :blockquote :h2 :sub :sup :p :br})

(def valid-scribe-attributes #{:a [:href] :blockquote :class})

(def analysis (atom {}))

(defn valid-flexible-content? [tag-set]
	(set-ops/subset? tag-set
		(set-ops/union valid-scribe-tags standard-tags)))

(defn extract-tags [html-string]
	(->> (tree-seq #(not-empty (:content %)) :content (hickory/as-hickory (hickory/parse html-string)))
		(filter #(= :element (:type %)))
		(map :tag)
		set
		))

(defn parse-json [body] (json/parse-string body true))

(defn read-from-capi [url]
	(->> url
		http/get
		:body
		))

(defn extract-response [capi-response]
	(->> capi-response
		parse-json
		:response
		:content
		)
)

(defn extract-body [response]
	(->> response
		:fields
		:body)
)

(defn analyse-content [capi-response]
	(let [response-body (extract-body capi-response)
		tags (extract-tags response-body)
		url (:webUrl capi-response)]
	{:tags-used tags
		:valid-content (valid-flexible-content? tags)
		:url url}))

(defn write-results [analysed-capi-results]
	)

(defn read-from-url [url]
	(->> url
		read-from-capi
		extract-response
		analyse-content))

(defn extract-api-urls [api-results]
	(map (fn [item] (:apiUrl item)) api-results))

(defn get-article-content [start-time end-time]
	(let [capi-host (System/getenv "CAPI_HOST")
		api-key (System/getenv "API_KEY")
		payload {:page-size consts/default-page-size
			:api-key api-key
			:from-date start-time
			:to-date end-time
			:tags "type/article"
			}]
		(-> (str "https://" capi-host "/search")
			(http/get {:query-params payload})
			:body
			parse-json
			:response
			:results
			extract-api-urls)))

(defn get-pages-for-query [start-time end-time]
	(let [capi-host (System/getenv "CAPI_HOST")
		api-key (System/getenv "API_KEY")
		payload {:page-size consts/default-page-size
			:api-key api-key
			:from-date start-time
			:to-date end-time
			:tags "type/article"
			}]
		(-> (str "https://" capi-host "/search")
			(http/get {:query-params payload})
			:body
			parse-json
			:response
			:pages
			str)))

(defn foo
  [x]
  "hello")
