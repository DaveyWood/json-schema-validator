(ns json-schema-validator.core
  (:require [clojure.data.json :as json]))

;TODO: should this be a macro?
(defn validation-builder [condition-function]
  "helper to create functions to validate rules"
  #(if (condition-function %1)
     nil
     %2))

(def validate-is-object (validation-builder map?))

(def validate-is-number (validation-builder number?))

(def validate-is-integer (validation-builder integer?))

(def type-validators {"object" validate-is-object
                      "number" validate-is-number
                      "integer" validate-is-integer})

;each property is a node
;validating a node with property means validating it, then validating its properties

(declare get-property-validators)

(defn applier [value]
  "applies the validation function"
  #(% value (str value)));TODO: real error message

(defn property-applier [value]
  #(% value))

(defn build-validator-list-for-node [node]
  "builds a list of validators for a single node"
  (list (get type-validators (:type node))));TODO: validate properties, required, etc
  ;(concat (list (get type-validators (:type node))) (get-property-validators node)));TODO: validate properties, required, etc

(defn build-validator-for-node [node]
  "builds a validator for a single node"
  #(filter (complement nil?) (concat (map (applier %) (build-validator-list-for-node node) ) (flatten (map (property-applier %) (get-property-validators node))))))

(defn build-validator-for-property [node property]
  "takes a node and a keyword (property) and builds a validator for that property"
  (let [node-validator (build-validator-for-node (get (:properties node) property))]
    #(node-validator (get % property))))

(defn get-property-validators [node]
  "get all the property validators for a node as a sequence"
  (if (:properties node)
    (map #(build-validator-for-property node %) (keys (:properties node)))
    '()))

(defn build-validator-from-schema [schema]
  "takes a json schema as a string and returns a function to validate maps based on that schema"
  (build-validator-for-node (json/read-str schema :key-fn keyword)))


;(get-property-validators node)
