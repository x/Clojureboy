(ns clojureboy.datatypes
  (:require [clojureboy.mmu :as mmu]
            [clojureboy.op-protocols :refer :all]
            [clojureboy.bit-smashers :refer :all]
            [clojureboy.flags :refer :all]))


(deftype MemoryPointer [addr instruction-time-register]
  WordContainer
  (read-word [this]
    (swap! instruction-time-register + 0)
    (mmu/rw addr))

  ByteContainer
  (read-byte [this]
    (swap! instruction-time-register + 0)
    (mmu/rb addr))

  InferredLoads
  (LD [this container]
    (swap! instruction-time-register + 0)
    ;; If we're doing an LD (_) (_) anywhere then this falls apart...
    (condp satisfies? container
      WordContainer (read-word container)
      ByteContainer (read-byte container)))

  clojure.lang.IFn
  (invoke [this]
    (swap! instruction-time-register + 0)
    (MemoryPointer. (read-word this) instruction-time-register)))


(deftype ByteRegister [mutable flags-register instruction-time-register]
  ByteContainer
  (read-byte [this]
    (swap! instruction-time-register + 4)
    @mutable)

  Resetable
  (RESET [this]
    (reset! mutable 0))

  ByteLoads
  (LD [this byte-container]
    (reset! mutable (read-byte byte-container)))

  ByteArithmetic
  (INC [this]
    (swap! instruction-time-register + 4)
    (if (= 0 (swap! mutable inc-byte))
      (swap! flags-register set-flag ZERO)))
  (DEC [this]
    (swap! instruction-time-register + 4)
    (swap! mutable dec-byte)))

(deftype WordRegister [mutable flags-register instruction-time-register]
  WordContainer
  (read-word [this]
    (swap! instruction-time-register + 8)
    @mutable)

  Resetable
  (RESET [this]
    (reset! mutable 0))

  clojure.lang.IFn
  (invoke [this]
    (swap! instruction-time-register + 0)
    (MemoryPointer. @mutable instruction-time-register))

  WordLoads
  (LD [this word-container]
    reset! mutable (read-word word-container))

  WordArithmetic
  (INC [this]
    (swap! instruction-time-register + 8)
    (swap! mutable inc)
    @mutable)
  (DEC [this]
    (swap! instruction-time-register + 8)
    (swap! mutable dec)
    @mutable))


(deftype RegisterPair [mutable1 mutable2 flags-register instruction-time-register]
  WordContainer
  (read-word [this]
    (swap! instruction-time-register + 0)
    (bytes-to-word @mutable1 @mutable2))

  clojure.lang.IFn
  (invoke [this]
    (swap! instruction-time-register + 0)
    (MemoryPointer. (mmu/rw (bytes-to-word @mutable1 @mutable2))
                    instruction-time-register))

  WordLoads
  (LD [this word-container]
    (let [[b1 b2] (word-to-bytes (read-word word-container))]
      (reset! mutable1 b1)
      (reset! mutable2 b2)))

  WordArithmetic
  (INC [this]
    (swap! instruction-time-register + 0)
    (if (overflowed? (swap! mutable2 inc-byte))
      (swap! mutable1 inc-byte)))
  (DEC [this]
    (swap! instruction-time-register + 0)
    (if (underflowed? (swap! mutable2 dec-byte))
      (swap! (swap! mutable1 dec-byte)))))
