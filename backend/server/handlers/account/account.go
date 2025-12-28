package account

import (
	"context"
	"database/sql"
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/go-chi/chi/v5"
	"github.com/go-playground/validator/v10"
	db "github.com/saleemlawal/FinSight/backend/db/sqlc"
	"github.com/saleemlawal/FinSight/backend/middleware"
	"github.com/saleemlawal/FinSight/backend/models"
	"github.com/saleemlawal/FinSight/backend/utils"
	"github.com/shopspring/decimal"
)

type AccountQuerier interface {
	CreateAccount(ctx context.Context, arg db.CreateAccountParams) (db.CreateAccountRow, error)
	GetAccountsByUserId(ctx context.Context, userID int32) ([]db.GetAccountsByUserIdRow, error)
	GetAccountByIdAndUserId(ctx context.Context, arg db.GetAccountByIdAndUserIdParams) (db.GetAccountByIdAndUserIdRow, error)
	UpdateAccount(ctx context.Context, arg db.UpdateAccountParams) (db.UpdateAccountRow, error)
	DeleteAccount(ctx context.Context, arg db.DeleteAccountParams) error
}

type AccountHandler struct {
	queries AccountQuerier
	validator *validator.Validate
}

func NewAccountHandler(conn *sql.DB) *AccountHandler {
	return &AccountHandler{
		queries: db.New(conn),
		validator: validator.New(),
	}
}

func (h *AccountHandler) CreateAccount(w http.ResponseWriter, r *http.Request) {
	// user is already authenticated, so we can get the user id from the context
	userId, err := middleware.GetUserID(r)
	if err != nil {
		utils.SendError(w, err.Error(), http.StatusUnauthorized, "UNAUTHORIZED")
		return
	}

	var req models.CreateAccountRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		utils.SendError(w, "Invalid request body", http.StatusBadRequest, "INVALID_REQUEST_BODY")
		return
	}
	fieldMapper := func(fieldName string) (string, string) {
		switch fieldName {
		case "Name":
			return "Invalid name", "INVALID_NAME"
		case "Type":
			return "Invalid type", "INVALID_TYPE"
		case "InstitutionName":
			return "Invalid institution name", "INVALID_INSTITUTION_NAME"
		case "InstitutionId":
			return "Invalid institution id", "INVALID_INSTITUTION_ID"
		case "LastFour":
			return "Invalid last four", "INVALID_LAST_FOUR"
		case "Balance":
			return "Invalid balance", "INVALID_BALANCE"
		default:
			return "Validation failed", "VALIDATION_ERROR"
		}
	}

	if !utils.ValidateStruct(w, h.validator, req, fieldMapper) {
		return
	}

	balance := decimal.NewFromFloat(req.Balance)

	createAccountParams := db.CreateAccountParams{
		Name:            req.Name,
		Type:            req.Type,
		InstitutionName: req.InstitutionName,
		InstitutionID:   req.InstitutionId,
		LastFour:        req.LastFour,
		Balance:         balance,
		UserID:          userId,
	}

	account, err := h.queries.CreateAccount(r.Context(), createAccountParams)
	if err != nil {
		utils.SendError(w, "Failed to create account", http.StatusInternalServerError, "INTERNAL_ERROR")
		return
	}

	utils.SendSuccess(w, account, "Account created successfully")
}

func (h *AccountHandler) GetAccounts(w http.ResponseWriter, r *http.Request) {
	userId, err := middleware.GetUserID(r)
	if err != nil {
		utils.SendError(w, err.Error(), http.StatusUnauthorized, "UNAUTHORIZED")
		return
	}

	accounts, err := h.queries.GetAccountsByUserId(r.Context(), userId)
	if err != nil {
		utils.SendError(w, "Failed to get accounts", http.StatusInternalServerError, "INTERNAL_ERROR")
		return
	}

	utils.SendSuccess(w, accounts, "Accounts retrieved successfully")
}

func (h *AccountHandler) UpdateAccount(w http.ResponseWriter, r *http.Request) {
	userId, err := middleware.GetUserID(r)
	if err != nil {
		utils.SendError(w, err.Error(), http.StatusUnauthorized, "UNAUTHORIZED")
		return
	}
	// fetch the account id from the url
	accountId := chi.URLParam(r, "accountId")
	if accountId == "" {
		utils.SendError(w, "Account id is required", http.StatusBadRequest, "BAD_REQUEST")
		return
	}

	var req models.UpdateAccountRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		utils.SendError(w, "Invalid request body", http.StatusBadRequest, "INVALID_REQUEST_BODY")
		return
	}

	fieldMapper := func(fieldName string) (string, string) {
		switch fieldName {
		case "Name":
			return "Invalid name", "INVALID_NAME"
		case "Type":
			return "Invalid type", "INVALID_TYPE"
		case "InstitutionName":
			return "Invalid institution name", "INVALID_INSTITUTION_NAME"
		case "InstitutionId":
			return "Invalid institution id", "INVALID_INSTITUTION_ID"
		case "LastFour":
			return "Invalid last four", "INVALID_LAST_FOUR"
		case "Balance":
			return "Invalid balance", "INVALID_BALANCE"
		default:
			return "Validation failed", "VALIDATION_ERROR"
		}
	}

	if !utils.ValidateStruct(w, h.validator, req, fieldMapper) {
		return
	}

	parsedInt64, err := strconv.ParseInt(accountId, 10, 32)
	if err != nil {
		utils.SendError(w, "Invalid account id", http.StatusBadRequest, "INVALID_ACCOUNT_ID")
		return
	}
	accountIdInt32 := int32(parsedInt64)

	// gets account by id and user id
	account, err := h.queries.GetAccountByIdAndUserId(r.Context(), db.GetAccountByIdAndUserIdParams{
		ID: accountIdInt32,
		UserID: userId,
	})

	if err != nil {
		if err == sql.ErrNoRows {
			utils.SendError(w, "Account not found", http.StatusNotFound, "ACCOUNT_NOT_FOUND")
			return
		}
		utils.SendError(w, "Failed to get account", http.StatusInternalServerError, "INTERNAL_ERROR")
		return
	}

	// Start with existing account values
	updateAccountParams := db.UpdateAccountParams{
		ID:              account.ID,
		Name:            account.Name,
		Type:            account.Type,
		InstitutionName: account.InstitutionName,
		InstitutionID:   account.InstitutionID,
		LastFour:        account.LastFour,
		Balance:         account.Balance,
	}

	// Only update fields that are provided in the request
	if req.Name != nil {
		updateAccountParams.Name = *req.Name
	}

	if req.Type != nil {
		updateAccountParams.Type = *req.Type
	}

	if req.InstitutionName != nil {
		updateAccountParams.InstitutionName = *req.InstitutionName
	}

	if req.InstitutionId != nil {
		updateAccountParams.InstitutionID = *req.InstitutionId
	}

	if req.LastFour != nil {
		updateAccountParams.LastFour = *req.LastFour
	}

	if req.Balance != nil {
		updateAccountParams.Balance = decimal.NewFromFloat(*req.Balance)
	}

	updatedAccount, err := h.queries.UpdateAccount(r.Context(), updateAccountParams)
	if err != nil {
		utils.SendError(w, "Failed to update account", http.StatusInternalServerError, "INTERNAL_ERROR")
		return
	}

	utils.SendSuccess(w, updatedAccount, "Account updated successfully")
}

func (h *AccountHandler) DeleteAccount(w http.ResponseWriter, r *http.Request) {
	userId, err := middleware.GetUserID(r)
	if err != nil {
		utils.SendError(w, err.Error(), http.StatusUnauthorized, "UNAUTHORIZED")
		return
	}

	accountId := chi.URLParam(r, "accountId")
	if accountId == "" {
		utils.SendError(w, "Account id is required", http.StatusBadRequest, "BAD_REQUEST")
		return
	}

	parsedInt64, err := strconv.ParseInt(accountId, 10, 32)
	if err != nil {
		utils.SendError(w, "Invalid account id", http.StatusBadRequest, "INVALID_ACCOUNT_ID")
		return
	}

	accountIdInt32 := int32(parsedInt64)

	// Verify the account exists and belongs to the user before deleting
	_, err = h.queries.GetAccountByIdAndUserId(r.Context(), db.GetAccountByIdAndUserIdParams{
		ID: accountIdInt32,
		UserID: userId,
	})
	if err != nil {
		if err == sql.ErrNoRows {
			utils.SendError(w, "Account not found", http.StatusNotFound, "ACCOUNT_NOT_FOUND")
			return
		}
		utils.SendError(w, "Failed to get account", http.StatusInternalServerError, "INTERNAL_ERROR")
		return
	}

	err = h.queries.DeleteAccount(r.Context(), db.DeleteAccountParams{
		ID: accountIdInt32,
		UserID: userId,
	})

	if err != nil {
		utils.SendError(w, "Failed to delete account", http.StatusInternalServerError, "INTERNAL_ERROR")
		return
	}

	utils.SendSuccess(w, nil, "Account deleted successfully")
}