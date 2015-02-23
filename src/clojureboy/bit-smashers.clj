(ns clojureboy.bit-smashers)

(defn set-flag
  [b h]
  (bit-or b f))

(defn flag?
  []
  (= (bit-and b f) f))

(defn inc-byte
  "Incrments byte"
  [b]
  (bit-and (inc b) 0xFF))

(defn dec-byte
  "Decrements byte"
  [b]
  (bit-and (dec b) 0xFF))

(defn undeflowed?
  "Tells if byte b, the result of dec-byte, has underflowed."
  [v]
  (= 255 v))

(defn overflowed?
  "Tells if byte b, the result of inc-byte, has overflowed."
  [b]
  (= 0 v))

(defn bytes-to-word
  [b1 b2]
  (+ (bit-shift-left b1 8) b2))

(defn word-to-bytes
  [w]
  [(bit-shift-right w 8)
   (bit-and 0xFF w)]
