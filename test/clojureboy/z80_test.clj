(ns clojureboy.z80-test
  (:require [clojure.test :refer :all]
            [clojureboy.mmu :as mmu]
            [clojureboy.z80 :refer :all]))

(deftest test-reset
  (testing "Resets every register to 0."
    (with-redefs [rs (atom {:A 1 :B 2})]
      (RESET)
      (is (every? #(= % 0) (vals @rs))))))

(deftest test-getr
  (testing "Returns the value defined by the pair of rs."
    (is (= (getr {:A 1 :B 2} [:A :B]) 258))))

(deftest test-LDrr
  (testing "copies between rs."
    (with-redefs [rs (atom {:A 100 :B 0})]
      (LDrr :B :A)
      (is (= (@rs :B) 100)))))

(deftest test-LDrm
  (testing "copies value memory at PC into register"
    (with-redefs [rs (atom {:PC 42})
                  mmu/rb {42 20}]
      (LDrm :A (getr @rs :PC) 2 0)
      (is (= (@rs :A) 20))))

  (testing "copies value memory at HL into register"
    (with-redefs [rs (atom {:H 1 :L 2 :A 0 :PC 5})
                  mmu/rb {258 30}]
      (LDrm :A (getr @rs [:H :L]) 2 0)
      (is (= (@rs :A) 30)))))

(deftest test-LDmm-byte
  (testing "copies value memory at PC into memory at HL"
    (let [_memory (atom nil)]
      (with-redefs [rs (atom {:H 1 :L 2 :PC 10})
                    mmu/rb {10 42}
                    mmu/wb (fn [a v] (if (= a 258) (reset! _memory v)))]
        (LDmm-byte (getr @rs [:H :L]) (getr @rs :PC) 4 1)
        (is (= @_memory 42))))))

(deftest test-LDmr-byte
  (testing "copies register A into memory at BC"
    (let [_memory (atom nil)]
      (with-redefs [rs (atom {:A 42 :B 1 :C 2 :PC 10})
                    mmu/wb (fn [a v] (if (= a 258) (reset! _memory v)))]
        (LDmr-byte (getr @rs [:B :C]) :A 2 0)
        (is (= @_memory 42))))))

