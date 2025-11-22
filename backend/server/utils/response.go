package utils

import (
	"encoding/json"
	"net/http"
)

func SendError(w http.ResponseWriter, message string, statusCode int, code ...string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(statusCode)
	
	apiError := APIError{
		Error: message,
	}
	if len(code) > 0 && code[0] != "" {
		apiError.Code = code[0]
	}
	
	json.NewEncoder(w).Encode(apiError)
}

func SendSuccess(w http.ResponseWriter, data any, message ...string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	
	if len(message) > 0 && message[0] != "" {
		json.NewEncoder(w).Encode(APIMessage{
			Message: message[0],
			Data:    data,
		})
	} else {
		json.NewEncoder(w).Encode(data)
	}
}
