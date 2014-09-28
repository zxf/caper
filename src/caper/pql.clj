(ns clopcap.pql
  :require [clj-antlr.core :as antlr]
  :use [clojure.string :only join])

(defmacro def-command
  [cmd & body]
  ())

(defn do-command
  [data & params]
  ())

(def-command find
  '[WHERE+ expression]
  [expression data]
  ())

(def-command dump
  '[STRING WHERE expression]
  [data file-name expression]
  (write-pcap file-name (do-command data expression)))

(defn join-keys
  [separator coll]
  (join separator
        (map #(name (first %)) coll)))

(defn anltr-rule
  [& options]
  (def ^:dynamic *parser*
    (anltr/parser
     (apply str
            (map #(if (keyword? %)
                    (str (name %) ":")
                    (str % ";\n"))
                 (apply concat options))))))

(anltr-rule ["grammar Pql"]
            [:command       "COMMAND expression"
             :expression    "or+"
             :or            "and ( OR and )*"
             :and           "atom ( AND atom )*"
             :atom          "condition | PBR expression NBR"
             :condition     "(VAR|pld) COMP (STRING|INT|list)"
             :list          "'[' (STRING|INT)* ']'"
             :pld           "VAR '[' INT ':' INT ']'"]
            [:COMMAND       (join-keys "|" *commands*)
             :OR            "'or'  -> channel(HIDDEN)"
             :AND           "'and' -> channel(HIDDEN)"
             :PBR           "'('   -> channel(HIDDEN)"
             :NBR           "')'   -> channel(HIDDEN)"
             :INT           "('0'..'9')+"
             :VAR           "('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_')+"
             :COMP          (join-keys "|" *comparisons*)
             :STRING        "'\"' (('A'..'Z'|'a'..'z') +) '\"'"
             :WS            "[ \n\r\t\,] -> channel(HIDDEN)"])



(defn pql-parse
  [query]
  (*parser* query))

(defn pql-query
  [data query]
  (let [q (pql-parse query)]
    (q data)))
