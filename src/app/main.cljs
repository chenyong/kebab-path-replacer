
(ns app.main (:require ["fs" :as fs] ["path" :as path] [clojure.string :as string]))

(defn kebab-name [x]
  (-> x
      (string/replace #"^[A-Z]" (fn [x] (string/lower-case x)))
      (string/replace #"[A-Z]" (fn [x] (str "-" (string/lower-case x))))))

(defn replace-in-dir! [dir]
  (doseq [child (js->clj (fs/readdirSync dir))]
    (let [child-path (path/join dir child)]
      (when (re-find #"[A-Z]" child)
        (fs/renameSync (path/join dir child) (path/join dir (kebab-name child)))
        (comment println "rename:" (path/join dir child) (path/join dir (kebab-name child))))))
  (doseq [child (js->clj (fs/readdirSync dir))]
    (let [child-path (path/join dir child), stat (fs/statSync child-path)]
      (when (.isDirectory stat) (replace-in-dir! child-path)))))

(defn task! [] (replace-in-dir! "."))

(defn main! [] (println "Started.") (task!))

(defn reload! [] (.clear js/console) (println "Reloaded.") (task!))
