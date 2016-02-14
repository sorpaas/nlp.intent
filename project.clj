(defproject nlp.intent "0.0.1-SNAPSHOT"
  :description "A Natural Language Processing library for parsing user commands."
  :url "https://source.id.hn/diffusion/NLPI/"
  :license {:name "GNU General Public License"
            :url "https://www.gnu.org/licenses/gpl-3.0.txt"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.2"]
                 [edu.stanford.nlp/stanford-corenlp "3.5.2" :classifier "models"]
                 [org.apache.opennlp/opennlp-tools "1.6.0"]])
