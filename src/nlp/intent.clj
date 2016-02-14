(ns nlp.intent
  (:require [nlp.intent.ner :as ner]
            [nlp.intent.classifier :as classifier]))

(defn new-rater
  "Create a new rater."
  []
  (transient {:types {}
              :classifier (classifier/new-classifier)}))

(defn add-type
  "Add a new type to the rater."
  [rater type]
  (assoc! rater :types
          (assoc (:types rater) (first type) (rest type))))

(defn train
  "Train the rater of a type with sentences."
  [rater type-name sentence]
  (classifier/train (:classifier rater) type-name sentence))

(defn sync
  "Sync the rater."
  [rater]
  (classifier/sync (:classifier rater)))

(defn rate
  "Rate a sentence."
  [rater sentence]
  (let [type-name (classifier/classify (:classifier rater) sentence)
        annotated (ner/annotate sentence)]
    (if type-name
      (vec (cons type-name (map (fn [x]
                                  (println x)
                                  (x annotated))
                                (type-name (:types rater))))))))
