(ns nlp.intent.ner
  (:require [clojure.string :refer [lower-case]])
  (:import [java.util Properties]
           [edu.stanford.nlp.pipeline StanfordCoreNLP Annotation]
           [edu.stanford.nlp.ling
            CoreAnnotations$TokensAnnotation
            CoreAnnotations$SentencesAnnotation
            CoreAnnotations$NamedEntityTagAnnotation
            CoreAnnotations$TextAnnotation
            CoreAnnotations$IndexAnnotation
            CoreAnnotations$MentionsAnnotation]))

(defn ^:private new-pipeline []
  (let [props (new Properties)]
    (.put props "annotators" "tokenize, ssplit, pos, lemma, ner, entitymentions")
    (let [pipeline (new StanfordCoreNLP props)]
      pipeline)))

(defn ^:private annotate-document [pipeline text]
  (let [document (new Annotation text)]
    (.annotate pipeline document)
    document))

(defn annotate [text]
  (let [pipeline (new-pipeline)
        document (annotate-document pipeline text)
        sentences (.get document CoreAnnotations$SentencesAnnotation)]
    (reduce merge
            (map (fn [sentence]
                   (into {}
                         (vec (map (fn [mention]
                                     (let [word (.get mention CoreAnnotations$TextAnnotation)
                                           ne (.get mention CoreAnnotations$NamedEntityTagAnnotation)]
                                       [(keyword (lower-case ne)) word]))
                                   (.get sentence CoreAnnotations$MentionsAnnotation)))))
                 sentences))))
