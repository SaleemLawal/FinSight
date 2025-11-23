package user

import (
	"database/sql"
	"encoding/json"
	"net/http"
	"os"
	"strings"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"github.com/lib/pq"
	db "github.com/saleemlawal/FinSight/backend/db/sqlc"
	"github.com/saleemlawal/FinSight/backend/middleware"
	"github.com/saleemlawal/FinSight/backend/models"
	"github.com/saleemlawal/FinSight/backend/utils"
	"golang.org/x/crypto/bcrypt"
)

type UserHandler struct {
	queries db.Querier
}

func NewUserHandler(conn *sql.DB) *UserHandler {
	return &UserHandler{
		queries: db.New(conn),
	}
}

func (h *UserHandler) Register(w http.ResponseWriter, r *http.Request) {
	user := db.CreateUserParams{}
	err := json.NewDecoder(r.Body).Decode(&user)
	if err != nil {
		utils.SendError(w, "Invalid request body", http.StatusBadRequest, "INVALID_REQUEST_BODY")
		return
	}

	user.Name = strings.TrimSpace(user.Name)
	user.Email = strings.TrimSpace(strings.ToLower(user.Email))

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(user.Password), bcrypt.DefaultCost)
	if err != nil {
		utils.SendError(w, "Failed to process password", http.StatusInternalServerError, "INTERNAL_SERVER_ERROR")
		return
	}

	user.Password = string(hashedPassword)

	_, err = h.queries.CreateUser(r.Context(), user)
	if err != nil {
		if pqErr, ok := err.(*pq.Error); ok {
			if pqErr.Code == "23505" {
				utils.SendError(w, "Email already exists", http.StatusConflict, "EMAIL_ALREADY_EXISTS")
				return
			}
		}
		utils.SendError(w, "Failed to register user", http.StatusInternalServerError, "INTERNAL_SERVER_ERROR")
		return
	}
	utils.SendSuccess(w, nil, "User registered successfully")
}

func(h *UserHandler) GetCurrentUser(w http.ResponseWriter, r *http.Request) {	
	userId, err := middleware.GetUserID(r)
	if err != nil {
		utils.SendError(w, err.Error(), http.StatusUnauthorized, "UNAUTHORIZED")
		return
	}

	// Get user from database
	user, err := h.queries.GetUserById(r.Context(), userId)
	if err != nil {
		utils.SendError(w, "User not found", http.StatusNotFound, "USER_NOT_FOUND")
		return
	}

	utils.SendSuccess(w, user, "Current user retrieved successfully")
}

func(h *UserHandler) Login(w http.ResponseWriter, r *http.Request) {
	loginRequest := models.LoginRequest{}

	if err := json.NewDecoder(r.Body).Decode(&loginRequest); err != nil {
		utils.SendError(w, err.Error(), http.StatusBadRequest, "INVALID_REQUEST_BODY")
		return
	}

	if loginRequest.Email == "" || loginRequest.Password == "" {
		utils.SendError(w, "Email and password are required", http.StatusBadRequest, "INVALID_REQUEST")
		return
	}

	// Get User by email
	user, err := h.queries.GetUserByEmail(r.Context(), loginRequest.Email)
	if err != nil {
		utils.SendError(w, "User not found", http.StatusNotFound, "USER_NOT_FOUND")
		return
	}

	// Compare password
	if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(loginRequest.Password)); err != nil {
		utils.SendError(w, "Invalid password", http.StatusUnauthorized, "INVALID_PASSWORD")
		return
	}

	// Generate JWT token
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"userId": user.ID,
		"email": user.Email,
		"exp": time.Now().Add(time.Hour * 24).Unix(), // 24 hours
	})

	tokenString, err := token.SignedString([]byte(os.Getenv("JWT_SECRET")))
	if err != nil {
		utils.SendError(w, "Failed to generate token", http.StatusInternalServerError, "INTERNAL_SERVER_ERROR")
		return
	}

	response := models.LoginResponse{
		Token: tokenString,
		User: models.CurrentUserResponse{
			Id: user.ID,
			Name: user.Name,
			Email: user.Email,
		},
	}

	utils.SendSuccess(w, response, "Login successful")
}

func(h *UserHandler) Logout(w http.ResponseWriter, r *http.Request) {
	//TODO: Perform logout by invalidating the session

	utils.SendSuccess(w, nil, "Logout successful")
}