(ns caper.pql
  (:require [clj-antlr.core :as antlr])
  (:use [clojure.string :only [join]]))

(def ^{:private true
       :dynamic true}
  commands (ref {}))

(def ^{:private true
       :dynamic true}
  comparisons {:> :>= := :< :<= :!=})

(defmacro def-command
  [cmd grammar & body]
  `(dosync
    (alter commands assoc
           (keyword '~cmd)
           {:grammar ~grammar
            :func (fn ~body)})))

(defn do-command
  [comd & params]
  (apply
   (:func ((keyword comd) @commands)) params))

(def-command find
  "expression"
  [expression data]
  (println expression))

(def-command dump
  "STRING WHERE expression"
  [expression data]
  (let [[dump-file sub-ex] expression]
    (do-command :find sub-ex data)))

(defn join-keys
  [separator coll]
  (join separator
        (map #(name (first %)) coll)))

(defn- antlr-commands []
  (apply concat
         (map #(vector
                (first %)
                (str (name (first %)) " " (:grammar (second %))))
              @commands)))


(defn antlr-rule
  [& options]
  (def ^:dynamic *parser*
    (antlr/parser
     (apply str
            (map #(if (keyword? %)
                    (str (name %) ":")
                    (str % ";\n"))
                 (apply concat options))))))


(antlr-rule ["grammar Pql"]
            (antlr-commands)
            [:expression    "or+"
             :or            "and ( OR and )*"
             :and           "atom ( AND atom )*"
             :atom          "condition | PBR expression NBR"
             :condition     "VAR COMP (STRING|INT)"]
            [:OR            "'or'  -> channel(HIDDEN)"
             :AND           "'and' -> channel(HIDDEN)"
             :PBR           "'('   -> channel(HIDDEN)"
             :NBR           "')'   -> channel(HIDDEN)"
             :STRING        "'\"' (('A'..'Z'|'a'..'z'|'0'..'9'|'.'|'_') +) '\"'"
             :INT           "('0'..'9')+"
             :VAR           "('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_')+"
             :COMP          (join-keys "|" comparisons)
             :WS            "[" "\\n\\r\\t] -> channel(HIDDEN)"])


(defn pql-parse
  [query]
  (println (*parser* query)))

(pql-parse "find x > 5")

(defn pql-query
  [data query]
  (let [q (pql-parse query)]
    (q data)))
