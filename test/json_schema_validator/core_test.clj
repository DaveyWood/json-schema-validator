(ns json-schema-validator.core-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [json-schema-validator.core :refer :all]))

(def schema "{
    \"$schema\": \"http://json-schema.org/draft-04/schema#\",
    \"title\": \"Product\",
    \"description\": \"A product from Acme's catalog\",
    \"type\": \"object\",
    \"properties\": {
      \"id\": {
        \"description\": \"The unique identifier for a product\",
        \"type\": \"integer\"
      }
    },
    \"required\": [\"id\"]
}")


(deftest test-is-object
  (testing "testing the true case for is object"
    (is (= nil (validate-is-object {:hello "world"} "blah is not an object")))))

(deftest test-is-object-false
  (testing "testing the false case for is object"
    (is (= "blah is not an object" (validate-is-object 7 "blah is not an object")))))


(deftest test-is-number
  (testing "testing the true case for is number"
    (is (= nil (validate-is-number 7 "blah is not an number")))))

(deftest test-is-number-false
  (testing "testing the false case for is number"
    (is (= "blah is not an number" (validate-is-number {:hello "world"} "blah is not an number")))))


(deftest test-is-integer
  (testing "testing the true case for is integer"
    (is (= nil (validate-is-integer 7 "blah is not an integer")))))

(deftest test-is-integer-false
  (testing "testing the false case for is integer"
    (is (= "blah is not an integer" (validate-is-integer 2.2 "blah is not an integer")))))

(def validator (build-validator-from-schema schema)) ;builds a very basic validator

(def property-validators (get-property-validators (json/read-str schema :key-fn keyword)))


(deftest property-validator-length
  (testing "testing property validator length"
    (is (= 1 (count property-validators)))))

(def id-property-validator (first property-validators))

(deftest test-id-property-validator
  (testing "test id property validator"
    (is (empty? (id-property-validator {:id 8})))))


;(def id-validator (build-validator-for-property {:properties { :id {:type "integer"}}} :id))
(def id-validator (first (get-property-validators {:properties { :id {:type "integer"}}})))

(deftest test-id-validator
  (testing "testing that the id validator validates"
    (is (empty? (id-validator {:id 8})))))

(deftest test-id-validator-fail
  (testing "testing that the id validator validates"
    (is ((complement empty?) (id-validator "donkey")))))

(def valid-product {:id 9})

(def no-id-product {:blah "blah"})

(def string-id-product {:id "blah"})

(deftest test-validator
  (testing "testing the validator's true case"
    (is (empty? (validator valid-product)))))

(deftest test-validator-not-an-object
  (testing "testing the validator with a value that is not a map"
    (is ((complement empty?) (validator 9)))))

(deftest test-validator-invalid-object
  (testing "testing the validator with an invalid map"
    (is ((complement empty?) (validator no-id-product)))))

(deftest test-validator-non-numeric-id
  (testing "testing the validator with property of the wrong type"
    (is ((complement empty?) (validator string-id-product)))))




