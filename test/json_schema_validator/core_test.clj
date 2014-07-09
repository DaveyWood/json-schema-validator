(ns json-schema-validator.core-test
  (:require [clojure.test :refer :all]
            [json-schema-validator.core :refer :all]))

(def schema "{
    \"$schema\": \"http://json-schema.org/draft-04/schema#\",
    \"title\": \"Product\",
    \"description\": \"A product from Acme's catalog\",
    \"type\": \"object\"
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

(deftest test-validator
  (testing "testing the validator's true case"
    (is (empty? (validator {:id 9})))))

(deftest test-validator-false
  (testing "testing the validator's false case"
    (is ((complement empty?) (validator 9)))))
