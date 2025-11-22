package routes

import (
	"github.com/go-chi/chi/v5"
	"github.com/saleemlawal/FinSight/backend/handlers/health"
	"github.com/saleemlawal/FinSight/backend/handlers/user"
)

// setup the api router
func ApiRouter() *chi.Mux {

	apiRouter := chi.NewRouter()

	apiRouter.Get("/healthz", health.Healthz)

	apiRouter.Route("/user", func(r chi.Router) {
		r.Post("/", user.Register)
		r.Post("/login", user.Login)
		r.Post("/logout", user.Logout)
		r.Get("/", user.GetCurrentUser)
	})

	return apiRouter
}
