package utils

// APIError represents an error response
type APIError struct {
	Error   string `json:"error"`
	Code    string `json:"code,omitempty"`
	// Details string `json:"details,omitempty"`
}

// APIMessage represents a success response
type APIMessage struct {
	Message string `json:"message"`
	Data    any    `json:"data,omitempty"`
}
