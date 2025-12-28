package models

type RegisterRequest struct {
	Name     string `json:"name" validate:"required,min=2,max=50"`
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required,min=8"`
}

type LoginRequest struct {
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required"`
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

type CreateAccountRequest struct {
	Name string `json:"name" validate:"required,min=2,max=50"`
	Type string `json:"type" validate:"required,oneof=CREDIT DEPOSITORY INVESTMENT LOAN"`
	InstitutionName string `json:"institution_name" validate:"required,min=2,max=50"`
	InstitutionId string `json:"institution_id" validate:"required,min=2,max=50"`
	LastFour string `json:"last_four" validate:"required,min=4,max=4"`
	Balance float64 `json:"balance" validate:"required"`
}

type UpdateAccountRequest struct {
	Name *string `json:"name,omitempty" validate:"omitempty,min=2,max=50"`
	Type *string `json:"type,omitempty" validate:"omitempty,oneof=CREDIT DEPOSITORY INVESTMENT LOAN"`
	InstitutionName *string `json:"institution_name,omitempty" validate:"omitempty,min=2,max=50"`
	InstitutionId *string `json:"institution_id,omitempty" validate:"omitempty,min=2,max=50"`
	LastFour *string `json:"last_four,omitempty" validate:"omitempty,min=4,max=4"`
	Balance *float64 `json:"balance,omitempty" validate:"omitempty"`
}

type DeleteAccountRequest struct {
	AccountId string `json:"account_id" validate:"required"`
}