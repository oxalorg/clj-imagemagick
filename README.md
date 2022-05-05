# clj-imagemagick [WORK IN PROGRESS]

**PS: Won't work cleanly yet**

Hiccup style DSL for creating Imagemagick commands which compose together

Eg: to take an object image, resize it to 100x100, then add it to a background image which we first convert to grayscale, and get the final file in `output.png` we can run the following code

```clojure
(clj-imagemagick.core/shell-command
 [:combine
  [:grayscale "background.png"]
  [:resize
   {:size "100x100"}
   "object.png"]])
```

will output the following string command

```bash
convert -gravity center -colorspace gray background.png +colorspace \( object.png -resize 100x100 \) -composite out.png
```
