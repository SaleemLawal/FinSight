package routes

import (
	"database/sql"

	"github.com/go-chi/chi/v5"
	"github.com/saleemlawal/FinSight/backend/handlers/account"
	"github.com/saleemlawal/FinSight/backend/handlers/health"
	"github.com/saleemlawal/FinSight/backend/handlers/user"
	"github.com/saleemlawal/FinSight/backend/middleware"
)

// setup the api router
func ApiRouter(conn *sql.DB) *chi.Mux {

	apiRouter := chi.NewRouter()

	apiRouter.Get("/healthz", health.Healthz)

	userHandler := user.NewUserHandler(conn)

	apiRouter.Route("/users", func(r chi.Router) {

		r.Post("/", userHandler.Register)
		r.Post("/login", userHandler.Login)

		r.Group(func(r chi.Router) {
			r.Use(middleware.AuthMiddleware)
			r.Post("/logout", userHandler.Logout)
			r.Get("/", userHandler.GetCurrentUser)
		})
	})

	accountHandler := account.NewAccountHandler(conn)

	apiRouter.Route("/accounts", func(r chi.Router) {
		r.Use(middleware.AuthMiddleware)
		r.Post("/", accountHandler.CreateAccount)
		r.Get("/", accountHandler.GetAccounts)
		// r.Get("/{accountId}", accountHandler.GetAccountById)
		r.Patch("/{accountId}", accountHandler.UpdateAccount)
		r.Delete("/{accountId}", accountHandler.DeleteAccount)
	})

	return apiRouter
}
