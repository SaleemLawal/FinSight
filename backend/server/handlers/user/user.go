package user

import (
	"encoding/json"
	"net/http"

	"github.com/saleemlawal/FinSight/backend/models"
	"github.com/saleemlawal/FinSight/backend/utils"
)

func Register (w http.ResponseWriter, r *http.Request) {
	user := models.User{}
	err := json.NewDecoder(r.Body).Decode(&user)
	if err != nil {
		utils.SendError(w, err.Error(), http.StatusBadRequest, "INVALID_REQUEST_BODY")
		return
	}

	if user.Email == "" || user.Password == "" || user.Name == "" {
		utils.SendError(w, "Email, password and name are required", http.StatusBadRequest, "INVALID_REQUEST")
		return
	}

	//TODO: Save to DB

	utils.SendSuccess(w, nil, "User registered successfully")
}

func GetCurrentUser(w http.ResponseWriter, r *http.Request) {
	currentUserResponse := models.CurrentUserResponse{}
	
	// TODO: Get current user from session

	utils.SendSuccess(w, currentUserResponse, "Current user retrieved successfully")

}
func Login(w http.ResponseWriter, r *http.Request) {
	loginRequest := models.LoginRequest{}

	err := json.NewDecoder(r.Body).Decode(&loginRequest)
	if err != nil {
		utils.SendError(w, err.Error(), http.StatusBadRequest, "INVALID_REQUEST_BODY")
		return
	}

	if loginRequest.Email == "" || loginRequest.Password == "" {
		utils.SendError(w, "Email and password are required", http.StatusBadRequest, "INVALID_REQUEST")
		return
	}

	//TODO: Perform authentication

	utils.SendSuccess(w, nil, "Login successful")
}

func Logout(w http.ResponseWriter, r *http.Request) {
	//TODO: Perform logout by invalidating the session

	utils.SendSuccess(w, nil, "Logout successful")
}