(ns todo.backend.db
  (:require [next.jdbc :as jdbc]))

(def db-spec {:dbtype "sqlite"
              :dbname "prod.db"})

(defn initialize-database! []
  (jdbc/execute! db-spec ["
    CREATE TABLE IF NOT EXISTS todos (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      title TEXT,
      description TEXT,
      completed BOOLEAN DEFAULT 0,
      created_at TEXT
    )
  "]))

(defn get-all-todos []
  (jdbc/execute! db-spec ["SELECT * FROM todos ORDER BY created_at DESC"]))

(defn create-todo [todo-data]
  (jdbc/execute-one! db-spec ["
    INSERT INTO todos (title, description, completed, created_at)
    VALUES (?, ?, ?, ?)
    RETURNING *"
    (:title todo-data)
    (:description todo-data)
    0
    (str (java.time.Instant/now))]))

(defn toggle-todo! [id]
  (jdbc/execute-one! db-spec ["
    UPDATE todos
    SET completed = (1 - completed)
    WHERE id = ?
    RETURNING *"
    id]))

(defn delete-todo! [id]
  (jdbc/execute-one! db-spec ["
    DELETE FROM todos
    WHERE id = ?
    RETURNING *"
    id]))
