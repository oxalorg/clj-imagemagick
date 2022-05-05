(ns clj-imagemagick.hiccup
  (:require [clj-imagemagick.core :as core]))

(declare hiccup->recipes)

(defn- normalize-form
  "Deal with optional props map in hiccup forms"
  ([form]
   (if (vector? form)
     (apply normalize-form form)
     form))
  ([type ?props & children]
   (let [[props children] (cond
                            (map? ?props)
                            [?props children]
                            (nil? ?props)
                            [nil children]
                            :else
                            [nil (cons ?props children)])]
     (into [type props] (map normalize-form) children))))

(defmulti op
  "Given the type/props/children of a hiccup form, return a sequence of ffmpeg
  operations. These are maps which can be passed to [[clj-imagemagick.core/args]]."
  (fn [[type opts & children]] type))

(defmethod op :combine
  [[_ opts & children]]
  (concat
   (mapcat core/other-args opts)
   (hiccup->recipes children)
   (core/other-args [:composite "out.png"])))

(comment
  (hiccup->recipes [:combine {:gravity "center"} [:grayscale "file.png"]])
  (hiccup->recipes [:grayscale "file.png"]))

(defmethod op :grayscale
  [[_ opts & children]]
  (concat
   (core/other-args [:colorspace "gray"])
   ;; TODO: a better way to compose
   (core/other-args [:+colorspace true])
   ))

(defmethod op :resize
  [[_ opts & children]])

(defmethod op :default
  [[_ opts & children]])

(defn hiccup->recipes [form]
  #_(assert (vector? form) "Form must be a vector")
  (op (normalize-form form)))

(comment

  (def f [:combine
          {:gravity "center"}
          [:grayscale "background.png"]
          [:resize
           {:size "100x100"}
           "object.png"]])

  (normalize-form f)
  (hiccup->recipes f)
  (normalize-form [:combine {} "file"])

  (hiccup->recipes (normalize-form [:combine {:gravity "center"} [:grayscale "file.png"]]))
  ,)

;; convert -gravity center -colorspace gray background.png +colorspace \( object.png -resize 100x100 \) -composite out.png

;; convert -gravity center -colorspace gray face.png +colorspace eyes.png"[100x100]" -composite out.png

;; convert -gravity center -colorspace gray face.png +colorspace \( eyes.png -resize 150% \) -composite out.png
