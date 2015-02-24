(ns clojureboy.registers.mutables)

(def mutable-a (atom 0))
(def mutable-b (atom 0))
(def mutable-c (atom 0))
(def mutable-d (atom 0))
(def mutable-e (atom 0))
(def mutable-h (atom 0))
(def mutable-l (atom 0))
(def mutable-f (atom 0))
(def mutable-pc (atom 0))
(def mutable-sp (atom 0))
(def mutable-m (atom 0))
(def mutable-t (atom 0))

(ns clojureboy.registers
  (:require [clojureboy.registers.mutables :refer :all]
            [clojureboy.datatypes])
  (:import [clojureboy.datatypes ByteRegister WordRegister RegisterPair]))

;; 8-bit registers
(def A (ByteRegister. mutable-a mutable-f mutable-m)) ;; accumulator
(def B (ByteRegister. mutable-b mutable-f mutable-m))
(def C (ByteRegister. mutable-c mutable-f mutable-m))
(def D (ByteRegister. mutable-d mutable-f mutable-m))
(def E (ByteRegister. mutable-e mutable-f mutable-m))
(def H (ByteRegister. mutable-h mutable-f mutable-m))
(def L (ByteRegister. mutable-l mutable-f mutable-m))
(def F (ByteRegister. mutable-f mutable-f mutable-m)) ;; flag register

;; register pairs
(def BC (RegisterPair. mutable-b mutable-c mutable-f mutable-m))
(def DE (RegisterPair. mutable-d mutable-e mutable-f mutable-m))
(def HL (RegisterPair. mutable-h mutable-l mutable-f mutable-m))

;; 16-bit registers
(def PC (WordRegister. mutable-pc mutable-f mutable-m)) ;; program counter
(def SP (WordRegister. mutable-sp mutable-f mutable-m)) ;; stack pointer
