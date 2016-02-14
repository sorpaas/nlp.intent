(ns nlp.intent.classifier
  (:import [edu.stanford.nlp.classify ColumnDataClassifier Dataset]
           [java.util Properties])
  (:require [clojure.string :refer [split]]))

(def default-properties
  {;; Features
   :useClassFeature true
   :1.useNGrams true
   :1.usePrefixSuffixNGrams true
   :1.useLowercaseSplitWords true
   :1.maxNGramLeng 4
   :1.minNGramLeng 1
   :1.binnedLengths "10,20,30"
   ;; Printing
   :printClassifierParam 200
   ;; Mapping
   :goldAnswerColumn 0
   :displayedColumn 1
   ;; Optimization
   :intern true
   :sigma 3
   :useQN true
   :QNsize 15
   :tolerance "1e-4"})

(defn ^:private map-to-properties
  "Convert a map to java properties."
  [m]
  (let [prop (new Properties)]
    (doseq [kv m]
      (.setProperty prop (name (key kv)) (str (val kv))))
    prop))

(defn new-classifier
  "Create a new classifier."
  []
  (transient {:classifier (new ColumnDataClassifier (map-to-properties default-properties))
              :dataset (new Dataset)}))

(defn train
  "Train a classifier with a keyword and a sentence."
  [classifier keyword sentence]
  (let [datum (.makeDatumFromStrings (:classifier classifier)
                                     (into-array (into [(name keyword)]
                                                       (split sentence #" "))))]
    (.add (:dataset classifier) datum)))

(defn sync
  "Sync classifier."
  [classifier]
  (assoc! classifier :trained (.makeClassifier (:classifier classifier) (:dataset classifier))))

(defn classify
  "Classify a sentence."
  [classifier sentence]
  (let [datum (.makeDatumFromStrings (:classifier classifier)
                                     (into-array (split sentence #" ")))]
    (.classOf (:trained classifier) datum)))
