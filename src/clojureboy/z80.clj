(ns clojureboy.z80
  (:require [clojureboy.mmu :as mmu]
            [clojureboy.registers :refer :all]))

;; control flags
(def halt (atom 0))
(def stop (atom 0))

;; clock accumulators
(def clock-m (atom 0))
(def clock-t (atom 0))

(defn NOP []
  nil)

(defn RESET []
  nil)

(defn STOP []
  nil)

(defn exec []
  nil)

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


(def instructions
  [;; 00x
   #(NOP)          #(LD BC nn)     #(LD (BC) A)    #(INC BC)
   #(INC B)        #(DEC B)        #(LD B n)       #(RLC A)
   #(LD (nn) SP)   #(ADD HL BC)    #(LD A (BC))    #(DEC BC)
   #(INC C)        #(DEC C)        #(LD C n)       #(RRC A)

   ;; 10x
   #(STOP)         #(LD DE nn)     #(LD (DE) A)    #(INC DE)
   #(INC D)        #(DEC D)        #(LD D n)       #(RL A)
   #(JR n)         #(ADD HL DE)    #(LD A (DE))    #(DEC DE)
   #(INC C)        #(DEC C)        #(LD C n)       #(RRC A)

   ;; 20x
   #(JR NZ n)      #(LD HL nn)     #(LDI (HL) A)   #(INC HL)
   #(INC H)        #(DEC H)        #(LD H n)       #(DAA)
   #(JR Z n)       #(ADD HL HL)    #(LDI A (HL))   #(DEC HL)
   #(INC L)        #(DEC L)        #(LD L n)       #(CPL)




   ])


















