(ns clojureboy.z80
  (:require [clojureboy.mmu :as mmu]))

(def clock (atom {:M 0 :T 0}))
(def rs
  (atom {:A 0 :B 0 :C 0 :D 0 :E 0 :H 0 :L 0 :F 0 ;; 8-bit rs
         :PC 0 :SP 0                             ;; 16-bit rs
         :M 0 :T 0}))                            ;; clock for last instruction

(def halt (atom 0))
(def stop (atom 0))

(defn getr
  "Finds the 8-bit value of a register r or 16-bit value of a register pair r."
  [rs r]
    (cond
      (keyword? r)
        (get rs r)
      (vector? r)
        (+ (bit-shift-left (get rs (r 0)) 8)
           (get rs (r 1)))
      :else
        (throw (Exception. (str "I don't know how to get register " r)))))

(defn LDrr
  "Loads byte register r2 into register r1.

  Increments :M by 1."
  [r1 r2]
  (swap! rs
    #(assoc % r1 (getr % r2)
              :M 1)))

(defn LDrm
  "Loads byte from memory at address m into register r.

  Increments :PC by pi and sets :M to t."
  [r m t pi]
  (swap! rs
    #(assoc % r (mmu/rb m)
              :PC (+ (getr % :PC) pi)
              :M t)))

(defn LDmr-byte
  "Loads byte register r into memory at address m.

  Increments :PC by pi and sets :M to t.
  "
  [m r t pi]
  (mmu/wb m (getr @rs r))
  (swap! rs
    #(assoc % :PC (+ (getr % :PC) pi)
              :M t)))

(defn LDmm-byte
  "Loads byte from memory location m1 into memory location m2.

  Increments :PC by pi and sets :M to t."
  [m1 m2 t pi]
  (mmu/wb m1 (mmu/rb m2))
  (swap! rs
    #(assoc % :PC (+ (getr % :PC) pi)
              :M t)))

(defn LDmr-word
  "Loads word register r into memory at address m.

  Increments :PC by pi and sets :M to t.
  "
  [m r t pi]
  (mmu/ww m (getr @rs r))
  (swap! rs
    #(assoc % :PC (+ (getr % :PC) pi)
              :M t)))

(defn LDmm-word
  "Loads word from memory location m1 into memory location m2.

  Increments :PC by pi and sets :M to t."
  [m1 m2 t pi]
  (mmu/ww m1 (mmu/rb m2))
  (swap! rs
    #(assoc % :PC (+ (getr % :PC) pi)
              :M t)))

(defn NOP
  "No operations."
  []
  (swap! rs #(assoc % :M 1)))

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
