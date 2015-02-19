(ns clojureboy.z80
  (:require [clojureboy.mmu :as mmu]))

;; clock
(def cM (atom 0))
(def cT (atom 0))

;; 8-bit registers
(def rA (atom 0))
(def rB (atom 0))
(def rC (atom 0))
(def rD (atom 0))
(def rE (atom 0))
(def rF (atom 0))

;; 16-bit registers
(def rPC (atom 0))
(def rSP (atom 0))

;; clock for last instruction
(def rM (atom 0))
(def rT (atom 0))

;; control flags
(def halt (atom 0))
(def stop (atom 0))

(defn word
  "Combines two bytes into a word."
  [b1 b2]
  (+ (bit-shift-left b1 8) b2))

(defn HALT
  "Tell system to halt."
  []
  (swap! halt 1)
  (swap! rs #(assoc % :M 1)))

(defn RESET
  "Resets the z80."
  []
  (let [init-clock (into {} (map (fn [[k v]] [k 0]) @clock))
        init-rs (into {} (map (fn [[k v]] [k 0]) @rs))]
    (reset! clock init-clock)
    (reset! rs init-rs)
    (reset! halt 0)
    (reset! stop 0)
    (println "z80 reset!")))

;; Last op flags
(def ZERO 0x80)
(def SUBTRACTION 0x40)
(def HALF-CARRY 0x20)
(def CARRY 0x10)

;; Bit Hackers
(defn set-flag
  [b h]
  (bit-or b f))

(defn flag?
  []
  (= (bit-and b f) f))

(defn inc-byte
  "Incrments byte"
  [b]
  (bit-and (inc b) 255))

(defn dec-byte
  "Decrements byte"
  [b]
  (bit-and (dec b) 255))

(defn undeflowed?
  "Tells if byte b, the result of dec-byte, has underflowed."
  [v]
  (= 255 v))

(defn overflowed?
  "Tells if byte b, the result of inc-byte, has overflowed."
  [b]
  (= 0 v))

;; Instructions
(defn INC-register
  [r]
  (if (= 0 (swap! r inc-byte))
    (swap! set-flag rF ZERO))
  (reset rM 1))

(defn INC-register-pair
  [r1 r2]
    (if (overflowed? (swap! r2 inc-byte))
      (swap! r1 inc-byte))
    (reset rM 2))

(defn DEC-register
  [r]
  (swap! r #(bit-and (dec %) 255))
  (reset! rM 1))

(defn DEC-register-pair
  [r1 r2]
  (if (underflowed? (swap! r2 dec-byte))
    (swap! (swap! r1 dec-byte)))
  (reset! rM 1))

(defn LD-register-n
  [r]
  (reset! r (mmu/rb @rPC))
  (swap! rPC inc)
  (reset! rM 2))

(defn LD-register-pair-nn
  [r1 r2]
  (reset! r1 (mmu/rb @rPC))
  (reset! r2 (mmu/rb (+ @rPC 1)))
  (swap! rPC + 2)
  (reset! rM 3))

(defn LD-register-mem
  [r1 r2 r3]
  (reset! r (mmu/rb (word @r1 @r2)))
  (reset! rM 2))

(defn LD-mem-register
  [r1 r2 r3]
  (mmu/wb (word @r1 @r2) @r3)
  (reset! rM 2))

(def instructions
  [;; 00x NOP
   (fn NOP []
     (reset! cM 1))

   ;; 01x LD BC,nn
   (fn LD-BC-nn []
     (LD-register-pair-nn rB rC))

   ;; 02x LD (BC),A
   (fn LD-BCm-A []
     (LD-mem-register rB rC rA))

   ;; 03x INC BC
   ;; why doesn't this set rF-0x80???
   ;; why isn't this in the z80 manual???
   (fn INC-BC []
     (INC-register-pair rB rC))

   ;; 04x INC B
   (fn INC-B []
     (if (= 0 (inc-register rB)) (set-zero-flag))
     (reset! rM 1))

   ;; 05x DEC B
   (fn DEC-B []
     (DEC-register rB))

   ;; 06x LD B,n
   (fn LD-B-n []
     (LD-register-n rB))

   ;; 07x RLC A
   (fn RLC-A []
     nil)

   ;; 08x LD (nn),SP
   (fn LD-nnm-SP []
     nil)

   ;; 09x ADD HL,BC
   (fn ADD-HL-BC []
     nil)

   ;; 0Ax LD A,(BC)
   (fn LD-A-BCm []
     (LD-register-mem rA rB rC))

   ;; 0Bx DEC BC
   (fn DEC-BC []
     (DEC-register-pair rB rC))

   ;; 0Cx INC C
   (fn INC-C []
     (INC-register rC))

   ;; 0Dx DEC C
   (fn DEC-C []
     (DEC-register rC))

   ;; 0Ex LD C,n
   (fn LD-C-n []
     (LD-register-n rC))

   ;; 0Fx RRC A
   (fn RRC-A []
     nil)

   ;; 10x STOP
   (fn STOP []
     (println "Stopping!")
     (reset! STOP 1))

   ;; 11x LD DE,nn
   (fn LD-DE-nn []
     (LD-register-pair-nn rD rE))

   ;; 12x LD (DE),A
   (fn LD-DEm-A []
     (LD-mem-register rD rE rA))

   ;; 13x INC DE
   (fn INC-DE []
     (INC-register-pair rD rE))

   ;; 14x INC D
   (fn INC-D []
     (INC-register rD))

   ;; 15x DEC D
   (fn DEC-D []
     (DEC-register rD))

   ;; 16x LD D,n
   (fn LD-D-n []
     (LD-register-n rD))

   ;; 17x RL A
   (fn RL-A []
     nil)

   ;; 18x JR n
   (fn JR-n []
     nil)

   ;; 19x ADD HL,DE
   (fn ADD-HL-DE []
     nil)

   ;; 1Ax LD A,(DE)
   (fn LD-A-DEm []
     (LD-register-mem rA rD rE))

   ;; 1Bx DEC DE
   (fn DEC-DE []
     (DEC-register-pair rD rE))

   ;; 1Cx INC C
   (fn INC-C []
     (INC-register rE))

   ;; 1Dx DEC C
   (fn DEC-C []
     (DEC-register rE))

   ;; 1Ex LD C,n
   (fn LD-C-n []
     (LD-register-n rE))

   ;; 1Fx RRC A
   (fn RRC-A []
     nil)
















   ;; 13x LDI (HL),A
   (fn LDI-HLm-A []
     nil)

   ;; 14x INC HL
   (fn INC-HL []
     (INC-register-pair rH rL))

   ;; 15x INC H
   (fn INC-H []
     (INC-register rH))



   ])











