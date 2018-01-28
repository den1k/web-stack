(ns vimsical.web-stack.util)

(defn deep-merge
  "Like merge, but merges maps recursively."
  [& maps]
  (if (every? map? (filter identity maps))
    (apply merge-with deep-merge maps)
    (last maps)))

(defn deep-merge2
  "Like merge, but merges maps & joins vectors recursively."
  [& maps]
  (apply merge-with
         (fn [x y & args]
           (cond (map? y) (apply deep-merge2 x y args)
                 (vector? y) (vec (concat x y args))
                 :else y))
         maps))
