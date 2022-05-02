(ns clj-imagemagick.core
  (:require [clojure.java.shell :as sh]
            [clojure.java.io :as io]
            [clojure.string :as str]))

;; image settings -> persist
;; image operators -> only to next image
;; image sequence operator ->

(def default-options
  {:executable "convert"})

(first (-> :+abcd name))

(defn other-args [[arg value]]
  ;; converts
  ;; :option => -option
  ;; and
  ;; :+option => +option
  (let [[hyphen-or-plus arg] (if (= \+ (first (name arg)))
                               [\+ (subs (name arg) 1)]
                               [\- (name arg)])]
    (cond
      (true? value)
      [(str hyphen-or-plus arg)]

      :else
      [(str hyphen-or-plus arg value)])))

(defn input-args [{:keys [file] :as input}]
  (concat
   (mapcat other-args (dissoc input :file))
   [(str file)]))

(defn output-args [{:keys [file] :as output}]
  (concat
   (mapcat other-args (dissoc output :file))
   [(str file)]))

(defn command [{:keys [inputs outputs options] :as recipe}]
  (let [{:keys [executable] :as options} (merge default-options options)]
    (into [executable]
          cat
          [(mapcat input-args inputs)
           (mapcat output-args outputs)])))

(def command->str #(str/join " " %))
(def shell-command (comp command->str command))

(command->str
 (command {:inputs [{:file "omg.png"}]}))

(comment
  (other-args [:negate true])
  ;; => ("-negate")
  (input-args {:file "wand.png"
               :+negate true})
  ;; => ("-negate" "wand.png")
  (input-args {:file "wand.png"
               :+matte true})
  ;; => ("+matte" "wand.png")

  (clojure.test/is (= 3 (+ 1 3)))

  (clojure.test/are [invocation recipe] (= invocation (command->str (command recipe)))
    "convert test.png out.png2"
    {:inputs [{:file "test.png"}]
     :outputs [{:file "out.png"}]}

    "convert wand.png -negate wizard.png images.png"
    {:inputs [{:file "wand.png"}
              {:file "wizard.png"
               :negate true}]
     :outputs [{:file "images.png"}]})

  )
