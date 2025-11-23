package main

import (
	"database/sql"
	"log"
	"net/http"
	"os"

	"github.com/joho/godotenv"
	"github.com/saleemlawal/FinSight/backend/routes"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	_ "github.com/lib/pq"
)

func main() {
	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file")
	}
	PORT := os.Getenv("PORT")
	dbURL := os.Getenv("DATABASE_URL")

	// connect to database
	conn, err := sql.Open("postgres", dbURL)
	if err != nil {
		log.Fatal("Error connecting to database: ", err)
	}
	defer conn.Close()

	// test connection
	if err := conn.Ping();err != nil {
		log.Fatal("Error pinging database: ", err)
	}

	// run migrations
	schema, err := os.ReadFile("db/schema.sql")
	if err != nil {
		log.Fatal("Error reading schema file: ", err)
	}
	
	if _, err := conn.Exec(string(schema)); err != nil {
		log.Fatal("Error running migrations: ", err)
	}

	r := chi.NewRouter()
	r.Use(middleware.Logger)
	r.Use(middleware.Recoverer)

	r.Mount("/api", routes.ApiRouter(conn))

	http.ListenAndServe(":"+PORT, r)
}
