(ns article-tag-checker.core
	(:require [hickory.core :as hickory]
		[clojure.set :as set-ops]
		[clojure.string :as str-ops]
		[clj-http.lite.client :as http]
		[cheshire.core :as json]
		[article-tag-checker.consts :as consts]
		[article-tag-checker.scribe :as scribe]))

(def standard-tags
	#{:html :head :body})

(def capi-host (System/getenv "CAPI_HOST"))

(defn valid-flexible-content? [tag-set]
	(set-ops/subset? tag-set
		(set-ops/union scribe/valid-tags standard-tags)))

(defn extract-tags [html-string]
	(if (not (nil? html-string))
		(->> (tree-seq #(not-empty (:content %)) :content (hickory/as-hickory (hickory/parse html-string)))
			(filter #(= :element (:type %)))
			(map :tag)
			set
			)
		#{}))

(defn parse-json [body] (json/parse-string body true))

(defn read-from-capi [url]
	(let [internal-url (str-ops/replace url "content.guardianapis.com" capi-host)
		response (http/get internal-url
			{
				:throw-exceptions false
				:query-params {
					:show-fields "body"
					}
			})]
		(if (= 200 (:status response))
			(:body response)
			nil)))

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


(defn write-results [filename analysed-capi-results]
	(spit (str "/tmp/article-analysis/" filename) analysed-capi-results))

(defn analyse-from-url [url]
	(->> url
		read-from-capi
		extract-response
		analyse-content))

(defn extract-api-urls [api-results]
	(map (fn [item] (:apiUrl item)) api-results))


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
			:pages)))

(defn get-article-urls-for-page [start-time end-time page-no]
	(let [api-key (System/getenv "API_KEY")
		payload {:page-size consts/default-page-size
			:api-key api-key
			:from-date start-time
			:to-date end-time
			:tags "type/article,-tone/minutebyminute"
			:section "-crosswords"
			:page (str page-no)
			}]
		(-> (str "https://" capi-host "/search")
			(http/get {:query-params payload})
			:body
			parse-json
			:response
			:results
			extract-api-urls)))

(defn get-article-urls [start-time end-time]
	(let [capi-host (System/getenv "CAPI_HOST")
		api-key (System/getenv "API_KEY")
		payload {:page-size consts/default-page-size
			:api-key api-key
			:from-date start-time
			:to-date end-time
			:tags "type/article,-tone/minutebyminute"
			:section "-crosswords"
			}
		no-of-pages (get-pages-for-query start-time end-time)]
		(mapcat
				(fn [page-no]
					(get-article-urls-for-page start-time end-time page-no))
				(range 1 (inc no-of-pages)))))

(defn get-live-articles [article-urls]
	(filter (fn [r] (not (nil? r))) (map read-from-capi article-urls)))

(defn analyse-period [start-time end-time]
	(->> (get-article-urls start-time end-time)
		get-live-articles
		(map extract-response)
		(map analyse-content)))

(defn foo
  [x]
  "hello")
