package models

import "time"

type User struct {
	Id string `json:"id"`
	Name string `json:"name"`
	Email string `json:"email"`
	Password string `json:"password"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`	
}

type LoginRequest struct {
	Email string `json:"email"`
	Password string `json:"password"`
}

type CurrentUserResponse struct {
	Id string `json:"id"`
	Name string `json:"name"`
	Email string `json:"email"`
}