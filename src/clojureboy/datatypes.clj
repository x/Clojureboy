(ns clojureboy.datatypes
  (:require [clojureboy.mmu :as mmu]
            [clojureboy.op-protocols :refer :all]
            [bit-smashers :refer :all]
            [flags :refer :all]))


(deftype ByteRegister [mutable flags-register intruction-time-register]
  ByteContainer
  (read-byte [this]
    (swap! inctruction-time-register + 4)
    @mutable)

  ByteLoads
  (LD [this byte-storage]
    (reset! mutable (read-byte byte-storage)))

  ByteArithmetic
  (INC [this]
    (swap! inctruction-time-register + 4)
    (if (= 0 (swap! mutable inc-byte))
      (swap! flags-register set-flag ZERO)))
  (DEC [this]
    (swap! inctruction-time-register + 4)
    (swap! mutable dec-byte))


(deftype WordRegister [mutable flags-register]
  WordContainer
  (read-word [this]
    (swap! inctruction-time-register + 8)
    @mutable)

  clojure.lang.IFn
  (invoke [this]
    (let [addr (bytes-to-word @mutable1 @mutable2)
          word-data (mmy/rw addr)]
      (WordContainer. word-data flags-register cost-fn)))

  WordLoads
  (LD [this word-storage]
    reset! mutable (read-word word-storage))

  WordArithmetic
  (INC [this]
    (swap! inctruction-time-register + 8)
    (swap! mutable inc)
    @mutable))
  (DEC [this]
    (swap! inctruction-time-register + 8)
    (swap! mutable dec)
    @mutable))


(deftype RegisterPair [mutable1 mutable2 flags-register cost-fn]
  WordContainer
  (read-word [this]
    (cost-fn)
    (bytes-to-word @mutable1 @mutable2))

  clojure.lang.IFn
  (invoke [this]
    (let [addr (bytes-to-word @mutable1 @mutable2)
          word-data (mmy/rw addr)]
      (WordContainer. word-data flags-register cost-fn)))

  Mutable
  (LD [this word-storage]
    (let [[b1 b2] (word-to-bytes (read-word word-storage))]
      (reset! mutable1 b1)
      (reset! mutable2 b2)))
  (INC [this]
    (cost-fn)
    (if (overflowed? (swap! mutable2 inc-byte))
      (swap! mutable1 inc-byte)))
  (DEC [this]
    (cost-fn)
    (if (underflowed? (swap! mutable2 dec-byte))
      (swap! (swap! mutable1 dec-byte)))))
