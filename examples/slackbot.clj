(ns nlp.intent.examples.scheduler
  (:gen-class)
  (:require [clj-slack-client.core :as slack]
            [clj-slack-client.rtm-transmit :as rtm]
            [clj-slack-client.team-state :as state]
            [nlp.intent :as intent]
            [clojure.edn :as edn]))

(def rater (intent/new-rater))

(defn handle-event [msg]
  (let [add-type-prefix "Add type:"
        train-prefix "Train:"]
    (cond
      (.startsWith msg add-type-prefix)
      (let [ednable (.substring msg (.length add-type-prefix))
            typ (edn/read-string ednable)]
        (if (and (vector? typ) (> (count typ) 1))
          (do
            (intent/add-type rater typ)
            (str "Added type " typ))
          "The format is wrong."))

      (.startsWith msg train-prefix)
      (let [ednable (.substring msg (.length train-prefix))
            edned (edn/read-string ednable)]
        (if (and (vector? edned) (= (count edned) 2))
          (do
            (intent/train rater (first edned) (second edned))
            (intent/sync rater)
            "I'm trained, thanks!")
          "The format is wrong."))

      :else
      (str "You might mean: " (intent/rate rater msg)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (slack/connect "xoxb-21096606934-JFahqKwaZjzFonbVSqAbyre4"
                 (fn [event]
                   (if (and (= (:type event) "message") (not= (:user event) (state/self-id)))
                     (rtm/say-message (:channel event) (handle-event (:text event)))))))
