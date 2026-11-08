(ns todo.backend.handler
  (:require [todo.backend.db :as db]
            [clojure.string :as str]))

(defn hello-handler [_request]
  {:status 200
   :body "Hello, World!"})

(defn list-todos-handler [_request]
  {:status 200
   :body {:todos (db/get-all-todos)}})

(defn create-todo-handler [request]
  (let [todo-data (:body request)
        title (:title todo-data)]
    (if (and title (not (str/blank? title)))
      (let [new-todo (db/create-todo todo-data)]
        {:status 201
         :body new-todo})
      {:status 400
       :body {:error "O 'título' (title) é obrigatório"}})))

(defn toggle-todo-handler [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (if-let [updated-todo (db/toggle-todo! id)]
      {:status 200 :body updated-todo}
      {:status 404 :body {:error "Todo não encontrado"}})))

(defn delete-todo-handler [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (if-let [deleted-todo (db/delete-todo! id)]
      {:status 200 :body deleted-todo}
      {:status 404 :body {:error "Todo não encontrado"}})))
