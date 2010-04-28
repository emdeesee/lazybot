; Written by Programble <programble@gmail.com>
; Licensed under the EPL
(ns sexpbot.plugins.fortune
  (:use [sexpbot respond info gist])
  (:require [irclj.irclj :as ircb]))

; Database file
(def fortunefile (str sexpdir "/fortunes.clj"))

(defplugin
  (:fortune 
   "Tells you what your fortune is. :>"
   ["fortune"] 
   [{:keys [irc channel args]}]
   (with-info fortunefile
     (let [db (:fortunes (read-config))]
					; Check if database is empty
       (if (zero? (count db))
	 (ircb/send-message irc channel "I have no fortune cookies. Please feed me some!")
	 (ircb/send-message irc channel (nth db (rand-int (count db))))))))

  (:addfortune 
   "Adds a fortune to the fortune database."
   ["addfortune"] 
   [{:keys [irc channel args]}]
   (if (seq args)
     (with-info fortunefile
       (let [new-fortune (->> args (interpose " ") (apply str))
	     db (read-config)]
	 (write-config (assoc db :fortunes (conj (:fortunes db) new-fortune)))
	 (ircb/send-message irc channel "Fortune cookie eaten.")))
     (ircb/send-message irc channel "An invisible fortune cookie?")))
  
  (:dumpfortunes 
   "Dumps the fortune database to a gist."
   ["dumpfortunes"] 
   [{:keys [irc channel args]}]
   (with-info fortunefile
     (let [db (:fortunes (read-config))
	   dump (->> db (interpose "\n") (apply str))]
       (ircb/send-message irc channel (post-gist "Fortunes Dump" dump))))))