(ns clojureboy.bit-smashers)

(defn set-flag
  [b f]
  (bit-or b f))

(defn flag?
  [b f]
  (= (bit-and b f) f))

(defn inc-byte
  "Incrments byte"
  [b]
  (bit-and (inc b) 0xFF))

(defn dec-byte
  "Decrements byte"
  [b]
  (bit-and (dec b) 0xFF))

(defn underflowed?
  "Tells if byte b, the result of dec-byte, has underflowed."
  [b]
  (= 255 b))

(defn overflowed?
  "Tells if byte b, the result of inc-byte, has overflowed."
  [b]
  (= 0 b))

(defn bytes-to-word
  [b1 b2]
  (+ (bit-shift-left b1 8) b2))

(defn word-to-bytes
  [w]
  [(bit-shift-right w 8)
   (bit-and 0xFF w)])
