(ns cloth.keys
  (:require #?@(:cljs [ethereumjs-tx])
            [cloth.util :as util])
  #?(:clj
     (:import [org.ethereum.crypto ECKey]
              [org.ethereum.core Transaction])))

#?(:cljs
   (def secp256k1 (aget util/eth-util "secp256k1")))

#?(:cljs
   (defn random-bytes
     ([] (random-bytes 32))
     ([length]
      (util/Buffer. (. js/window.crypto getRandomValues (js/Uint8Array. length))))))

#?(:cljs
   (defn verify-private-key [key]
     (try
       ((aget secp256k1 "privateKeyVerify") key)
       (catch js/TypeError e nil))))

(defn create-private-key []
  #?(:cljs
     (loop []
       (let [key (random-bytes)]
         (if-not (verify-private-key key)
           (recur)
           key))))
  #?(:clj (ECKey.)))

#?(:cljs
   (defn ->public-key [private-key]
     ((aget util/eth-util "privateToPublic") private-key)))

(defn ->address [private-key]
  #?(:cljs (util/hex0x ((aget util/eth-util "pubToAddress") (->public-key private-key))))
  #?(:clj (util/hex0x (.getAddress private-key))))

(defn priv->b [priv]
  #?(:cljs (identity priv))
  #?(:clj (.getPrivKeyBytes priv)))

(defn b->priv [b]
  #?(:cljs (identity b))
  #?(:clj
     (if (instance? ECKey b)
       b
       (ECKey/fromPrivate b))))


(defn keypair
  ([b]
   (let [private-key (b->priv (if (string? b)
                                (util/hex-> b)
                                b))]
     {:private-key (util/hex0x (priv->b private-key))
      :address     (->address private-key)})))

(defn get-private-key
  "pass a keypair map or a private-key either hex or buffer and returns a private key for signing pupr"
  [kp-or-private-key]
  (let [b (if (:private-key kp-or-private-key)
            (util/hex-> (:private-key kp-or-private-key))
            (if (string? kp-or-private-key)
              (util/hex-> kp-or-private-key)
              kp-or-private-key))]
    (b->priv b)))

(defn create-keypair
  "Creates a map of hex encoded keypair with
   :private-key and :address keys"
  []
  (keypair (create-private-key)))
