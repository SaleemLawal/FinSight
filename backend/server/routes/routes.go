package routes

import (
	"database/sql"

	"github.com/go-chi/chi/v5"
	"github.com/saleemlawal/FinSight/backend/handlers/health"
	"github.com/saleemlawal/FinSight/backend/handlers/user"
	"github.com/saleemlawal/FinSight/backend/middleware"
)

// setup the api router
func ApiRouter(conn *sql.DB) *chi.Mux {

	apiRouter := chi.NewRouter()

	apiRouter.Get("/healthz", health.Healthz)

	userHandler := user.NewUserHandler(conn)
	apiRouter.Route("/user", func(r chi.Router) {

		r.Post("/", userHandler.Register)
		r.Post("/login", userHandler.Login)

		r.Group(func(r chi.Router) {
			r.Use(middleware.AuthMiddleware)
			r.Post("/logout", userHandler.Logout)
			r.Get("/", userHandler.GetCurrentUser)
		})
	})

	return apiRouter
}
