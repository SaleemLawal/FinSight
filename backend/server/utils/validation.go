package utils

import (
	"net/http"
	"strings"

	"github.com/go-playground/validator/v10"
)

// FieldErrorMapper is a function type that maps field names to error messages and codes
type FieldErrorMapper func(fieldName string) (message string, code string)

// ValidateStruct validates a struct using the validator and sends appropriate error responses
// Returns true if validation passes, false otherwise
func ValidateStruct(w http.ResponseWriter, v *validator.Validate, s interface{}, mapper FieldErrorMapper) bool {
	if err := v.Struct(s); err != nil {
		if validationErr, ok := err.(validator.ValidationErrors); ok && len(validationErr) > 0 {
			fieldErr := validationErr[0]
			fieldName := fieldErr.Field()
			
			var message, code string
			if mapper != nil {
				message, code = mapper(fieldName)
			}
			
			// Default error handling if no mapper provided or mapper returns empty
			if message == "" {
				message = getDefaultErrorMessage(fieldErr)
				code = "VALIDATION_ERROR"
			}
			if code == "" {
				code = "VALIDATION_ERROR"
			}
			
			SendError(w, message, http.StatusBadRequest, code)
			return false
		}
		SendError(w, "Validation failed", http.StatusBadRequest, "VALIDATION_ERROR")
		return false
	}
	return true
}

// getDefaultErrorMessage generates a default error message based on validation error
func getDefaultErrorMessage(fieldErr validator.FieldError) string {
	fieldName := strings.ToLower(fieldErr.Field())
	
	switch fieldErr.Tag() {
	case "required":
		return fieldName + " is required"
	case "email":
		return "Invalid email format"
	case "min":
		return fieldName + " is too short"
	case "max":
		return fieldName + " is too long"
	default:
		return "Invalid " + fieldName
	}
}

// ValidateField validates a single field value using the validator
// Returns true if validation passes, false otherwise
func ValidateField(w http.ResponseWriter, validator *validator.Validate, value interface{}, rules string, errorMessage string, errorCode string) bool {
	if err := validator.Var(value, rules); err != nil {
		SendError(w, errorMessage, http.StatusBadRequest, errorCode)
		return false
	}
	return true
}

