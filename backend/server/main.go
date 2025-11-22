package main

import (
	"log"
	"net/http"
	"os"

	"github.com/joho/godotenv"
	"github.com/saleemlawal/FinSight/backend/handlers/health"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
)

func main() {
	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file")
	}
	PORT := os.Getenv("PORT")

	r := chi.NewRouter()
	r.Use(middleware.Logger)

	apiRouter := chi.NewRouter()

	apiRouter.Get("/healthz", health.Healthz)

	r.Mount("/api/v1", apiRouter)

	http.ListenAndServe(":"+string(PORT), r)
}
