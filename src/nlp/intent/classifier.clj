(ns nlp.intent.classifier
  (:import [opennlp.tools.doccat DocumentCategorizerME DoccatModel DocumentSample DoccatFactory]
           [opennlp.tools.util ObjectStream CollectionObjectStream TrainingParameters])
  (:require [clojure.string :refer [split]]))

(defn new-classifier
  "Create a new classifier."
  []
  (transient {:dataset (list)}))

(defn train
  "Train a classifier with a keyword and a sentence."
  [classifier keyword sentence]
  (let [sample (new DocumentSample (name keyword) (str sentence))]
    (assoc! classifier :dataset (conj (:dataset classifier) sample))))

(defn sync
  "Sync classifier."
  [classifier]
  (assoc! classifier :model (DocumentCategorizerME/train "en"
                                                         (new CollectionObjectStream
                                                              (:dataset classifier))
                                                         (new TrainingParameters)
                                                         (new DoccatFactory))))

(defn classify
  "Classify a sentence."
  [classifier sentence]
  (let [categorizer (new DocumentCategorizerME (:model classifier))
        categorized (.sortedScoreMap categorizer sentence)]
    (println (str categorized))
    (first (.toArray (.get categorized (.lastKey categorized))))))
