package models

type LoginRequest struct {
	Email string `json:"email"`
	Password string `json:"password"`
}

type CurrentUserResponse struct {
	Id int32 `json:"id"`
	Name string `json:"name"`
	Email string `json:"email"`
}

type LoginResponse struct {
	Token string `json:"token"`
	User CurrentUserResponse `json:"user"`
}