(ns todo.backend.core
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [todo.backend.handler :as handler]
            [ring.middleware.cors :refer [wrap-cors]]
            [todo.backend.handler :as handler])
  (:gen-class))

;; --- 1. Definição das Rotas ---
(def app-routes
  (ring/router
   ;; Aninhamos tudo sob "/api"
   ["/api"
    ["/hello" {:get {:handler handler/hello-handler}}]

    ["/todos"
     {:get  {:handler handler/list-todos-handler}      ;; GET  chama 'list'
      :post {:handler handler/create-todo-handler}}]])) ;; POST chama 'create'

;; --- 2. Definição da Aplicação (App) ---
(def app
  (ring/ring-handler
   app-routes
   (ring/create-default-handler)
   {:middleware [[wrap-cors
                  :access-control-allow-origin [#"http://localhost:8000"]
                  :access-control-allow-methods [:get :post :put :delete]
                  :access-control-allow-headers ["Content-Type"]]
                 wrap-json-response
                 [wrap-json-body {:keywords? true}]
                 wrap-params
                 wrap-keyword-params]}))

;; --- 3. Função para Iniciar o Servidor ---
(defn start-server [port]
  (println (str "Servidor iniciado na porta " port))
  ;; #'app passa a "var" da nossa app para o Jetty (útil no desenvolvimento)
  ;; :join? false evita que o servidor bloqueie a thread principal.
  (jetty/run-jetty #'app {:port port :join? false}))

;; --- 4. Ponto de Entrada Principal (-main) ---
;; Esta é a função que o alias :run (do deps.edn) procura.
(defn -main [& args]
  ;; Permite passar a porta como argumento (ex: clj -M:run 8080)
  ;; ou usa "3000" como padrão.
  (let [port (Integer/parseInt (or (first args) "3000"))]
    (start-server port)))
