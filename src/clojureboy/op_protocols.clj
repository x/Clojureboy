(ns clojureboy.op-protocols)

(defprotocol Mutatable
  (LD [this val])
  (INC [this])
  (DEC [this]))

(defprotocol ByteContainer
  (read-byte [this]))

(defprotocol WordContainer
  (read-word [this]))

(defprotocol ByteLoads
  (LD [this byte-container])
  (LDD [this byte-container])
  (LDI [this byte-container])
  (LDH [this byte-container]))

(defprotocol WordLoads
  (LD [this word-container])
  (PUSH [this word-container])
  (POP [this word-container]))

(defprotocol ByteArithmetic
  (ADD [this byte-container])
  (ADC [this byte-container])
  (SUB [this])
  (SBC [this byte-container])
  (AND [this])
  (OR [this])
  (XOR [this])
  (CP [this])
  (INC [this])
  (DEC [this]))

(defprotocol WordArithmetic
  (ADD [this word-container])
  (INC [this word-container])
  (DEC [this word-container]))

;; Not sure if we need these just yet
(defprotocol ByteRotate
  (RLC [this])
  (RL [this])
  (RRC [this])
  (RR [this]))

(defprotocol ByteShift
  (SLA [this])
  (SRA [this])
  (SRL [this]))
