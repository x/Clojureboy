(ns clojureboy.z80-test
  (:require [clojure.test :refer :all]
            [clojureboy.z80 :refer :all]))


(deftest test-ld-register
  (testing "Loads a value n into register A"
    (RESET)
    (LD A 1)
    (is (= (read-byte A 1)))))
