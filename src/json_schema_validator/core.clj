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


(defn build-validator-list-for-node [node]
  "builds a list of validators for a single node"
  (list (get type-validators (:type node))));TODO: validate properties, required, etc

(defn applier [value]
  "applies the validation function"
  #(% value "fail"));TODO: real error message

(defn build-validator-for-node [node]
  "builds a validator for a single node"
  #(filter (complement nil?) (map (applier %) (build-validator-list-for-node node) )))
