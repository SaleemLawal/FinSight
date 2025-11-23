package user

import (
	"bytes"
	"context"
	"database/sql"
	"encoding/json"
	"errors"
	"net/http"
	"net/http/httptest"
	"os"
	"testing"

	"github.com/go-playground/validator/v10"
	"github.com/lib/pq"
	db "github.com/saleemlawal/FinSight/backend/db/sqlc"
	"github.com/saleemlawal/FinSight/backend/middleware"
	"github.com/saleemlawal/FinSight/backend/models"
	"github.com/saleemlawal/FinSight/backend/utils"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"golang.org/x/crypto/bcrypt"
)

type MockQuerier struct {
	mock.Mock
}

func (m *MockQuerier) CreateUser(ctx context.Context, arg db.CreateUserParams) (db.CreateUserRow, error) {
	args := m.Called(ctx, arg)
	return args.Get(0).(db.CreateUserRow), args.Error(1)
}

func (m *MockQuerier) GetUserByEmail(ctx context.Context, email string) (db.GetUserByEmailRow, error) {
	args := m.Called(ctx, email)
	return args.Get(0).(db.GetUserByEmailRow), args.Error(1)
}

func (m *MockQuerier) GetUserById(ctx context.Context, id int32) (db.GetUserByIdRow, error) {
	args := m.Called(ctx, id)
	return args.Get(0).(db.GetUserByIdRow), args.Error(1)
}

func createTestHandler(mock *MockQuerier) *UserHandler {
	return &UserHandler{
		queries: mock,
		validator: validator.New(),
	}
}

func TestRegister(t *testing.T) {
	tests := []struct {
		name           string
		requestBody    interface{}
		mockSetup      func(*MockQuerier)
		expectedStatus int
		expectedCode   string
	}{
		{
			name: "successful registration",
			requestBody: map[string]string{
				"name":     "Test User",
				"email":    "test@example.com",
				"password": "password123",
			},
			mockSetup: func(m *MockQuerier) {
				m.On("CreateUser", mock.Anything, mock.MatchedBy(func(arg db.CreateUserParams) bool {
					return arg.Name == "Test User" && arg.Email == "test@example.com"
				})).Return(db.CreateUserRow{
					ID:    1,
					Name:  "Test User",
					Email: "test@example.com",
				}, nil)
			},
			expectedStatus: http.StatusOK,
		},
		{
			name: "invalid request body",
			requestBody: "invalid json",
			mockSetup: func(m *MockQuerier) {
			},
			expectedStatus: http.StatusBadRequest,
			expectedCode:   "INVALID_REQUEST_BODY",
		},
		{
			name: "duplicate email",
			requestBody: map[string]string{
				"name":     "Test User",
				"email":    "existing@example.com",
				"password": "password123",
			},
			mockSetup: func(m *MockQuerier) {
				m.On("CreateUser", mock.Anything, mock.Anything).Return(db.CreateUserRow{}, &pq.Error{Code: "23505"})
			},
			expectedStatus: http.StatusConflict,
			expectedCode:   "EMAIL_ALREADY_EXISTS",
		},
		{
			name: "database error",
			requestBody: map[string]string{
				"name":     "Test User",
				"email":    "test@example.com",
				"password": "password123",
			},
			mockSetup: func(m *MockQuerier) {
				m.On("CreateUser", mock.Anything, mock.Anything).Return(db.CreateUserRow{}, errors.New("database error"))
			},
			expectedStatus: http.StatusInternalServerError,
			expectedCode:   "INTERNAL_SERVER_ERROR",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockQuerier := new(MockQuerier)
			tt.mockSetup(mockQuerier)
			handler := createTestHandler(mockQuerier)

			var body []byte
			var err error
			if str, ok := tt.requestBody.(string); ok {
				body = []byte(str)
			} else {
				body, err = json.Marshal(tt.requestBody)
				assert.NoError(t, err, "Failed to marshal request body")
			}

			req := httptest.NewRequest(http.MethodPost, "/register", bytes.NewBuffer(body))
			req.Header.Set("Content-Type", "application/json")
			w := httptest.NewRecorder()

			handler.Register(w, req)

			assert.Equal(t, tt.expectedStatus, w.Code, "Status code should match")

			if tt.expectedCode != "" {
				var apiError utils.APIError
				err := json.Unmarshal(w.Body.Bytes(), &apiError)
				assert.NoError(t, err, "Failed to unmarshal response")
				assert.Equal(t, tt.expectedCode, apiError.Code, "Error code should match")
			}

			mockQuerier.AssertExpectations(t)
		})
	}
}

func TestLogin(t *testing.T) {
	hashedPassword, _ := bcrypt.GenerateFromPassword([]byte("password123"), bcrypt.DefaultCost)
	os.Setenv("JWT_SECRET", "test-secret-key")

	tests := []struct {
		name           string
		requestBody    interface{}
		mockSetup      func(*MockQuerier)
		expectedStatus int
		expectedCode   string
		validateToken  bool
	}{
		{
			name: "successful login",
			requestBody: models.LoginRequest{
				Email:    "test@example.com",
				Password: "password123",
			},
			mockSetup: func(m *MockQuerier) {
				m.On("GetUserByEmail", mock.Anything, "test@example.com").Return(db.GetUserByEmailRow{
					ID:       1,
					Name:     "Test User",
					Email:    "test@example.com",
					Password: string(hashedPassword),
				}, nil)
			},
			expectedStatus: http.StatusOK,
			validateToken:  true,
		},
		{
			name: "invalid request body",
			requestBody: "invalid json",
			mockSetup: func(m *MockQuerier) {
			},
			expectedStatus: http.StatusBadRequest,
			expectedCode:   "INVALID_REQUEST_BODY",
		},
		{
			name: "missing email",
			requestBody: models.LoginRequest{
				Password: "password123",
			},
			mockSetup: func(m *MockQuerier) {
			},
			expectedStatus: http.StatusBadRequest,
			expectedCode:   "INVALID_EMAIL",
		},
		{
			name: "missing password",
			requestBody: models.LoginRequest{
				Email: "test@example.com",
			},
			mockSetup: func(m *MockQuerier) {
			},
			expectedStatus: http.StatusBadRequest,
			expectedCode:   "INVALID_PASSWORD",
		},
		{
			name: "user not found",
			requestBody: models.LoginRequest{
				Email:    "notfound@example.com",
				Password: "password123",
			},
			mockSetup: func(m *MockQuerier) {
				m.On("GetUserByEmail", mock.Anything, "notfound@example.com").Return(db.GetUserByEmailRow{}, sql.ErrNoRows)
			},
			expectedStatus: http.StatusNotFound,
			expectedCode:   "USER_NOT_FOUND",
		},
		{
			name: "invalid password",
			requestBody: models.LoginRequest{
				Email:    "test@example.com",
				Password: "wrongpassword",
			},
			mockSetup: func(m *MockQuerier) {
				m.On("GetUserByEmail", mock.Anything, "test@example.com").Return(db.GetUserByEmailRow{
					ID:       1,
					Name:     "Test User",
					Email:    "test@example.com",
					Password: string(hashedPassword),
				}, nil)
			},
			expectedStatus: http.StatusUnauthorized,
			expectedCode:   "INVALID_PASSWORD",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockQuerier := new(MockQuerier)
			tt.mockSetup(mockQuerier)
			handler := createTestHandler(mockQuerier)

			var body []byte
			var err error
			if str, ok := tt.requestBody.(string); ok {
				body = []byte(str)
			} else {
				body, err = json.Marshal(tt.requestBody)
				assert.NoError(t, err, "Failed to marshal request body")
			}

			req := httptest.NewRequest(http.MethodPost, "/login", bytes.NewBuffer(body))
			req.Header.Set("Content-Type", "application/json")
			w := httptest.NewRecorder()

			handler.Login(w, req)

			assert.Equal(t, tt.expectedStatus, w.Code, "Status code should match")

			if tt.expectedCode != "" {
				var apiError utils.APIError
				err := json.Unmarshal(w.Body.Bytes(), &apiError)
				assert.NoError(t, err, "Failed to unmarshal response")
				assert.Equal(t, tt.expectedCode, apiError.Code, "Error code should match")
			}

			if tt.validateToken {
				var response map[string]interface{}
				err := json.Unmarshal(w.Body.Bytes(), &response)
				assert.NoError(t, err, "Failed to unmarshal response")
				if data, ok := response["data"].(map[string]interface{}); ok {
					if token, ok := data["token"].(string); ok {
						assert.NotEmpty(t, token, "Token should not be empty")
					}
				}
			}

			mockQuerier.AssertExpectations(t)
		})
	}
}

func TestGetCurrentUser(t *testing.T) {
	tests := []struct {
		name           string
		userId         int32
		mockSetup      func(*MockQuerier)
		expectedStatus int
		expectedCode   string
	}{
		{
			name:   "successful get current user",
			userId: 1,
			mockSetup: func(m *MockQuerier) {
				m.On("GetUserById", mock.Anything, int32(1)).Return(db.GetUserByIdRow{
					ID:    1,
					Name:  "Test User",
					Email: "test@example.com",
				}, nil)
			},
			expectedStatus: http.StatusOK,
		},
		{
			name:   "unauthorized - no user ID in context",
			userId: 0,
			mockSetup: func(m *MockQuerier) {
			},
			expectedStatus: http.StatusUnauthorized,
			expectedCode:   "UNAUTHORIZED",
		},
		{
			name:   "user not found",
			userId: 999,
			mockSetup: func(m *MockQuerier) {
				m.On("GetUserById", mock.Anything, int32(999)).Return(db.GetUserByIdRow{}, sql.ErrNoRows)
			},
			expectedStatus: http.StatusNotFound,
			expectedCode:   "USER_NOT_FOUND",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mockQuerier := new(MockQuerier)
			tt.mockSetup(mockQuerier)
			handler := createTestHandler(mockQuerier)

			req := httptest.NewRequest(http.MethodGet, "/user/me", nil)
			if tt.userId > 0 {
				ctx := req.Context()
				ctx = context.WithValue(ctx, middleware.UserIDKey, tt.userId)
				req = req.WithContext(ctx)
			}
			w := httptest.NewRecorder()

			handler.GetCurrentUser(w, req)

			assert.Equal(t, tt.expectedStatus, w.Code, "Status code should match")

			if tt.expectedCode != "" {
				var apiError utils.APIError
				err := json.Unmarshal(w.Body.Bytes(), &apiError)
				assert.NoError(t, err, "Failed to unmarshal response")
				assert.Equal(t, tt.expectedCode, apiError.Code, "Error code should match")
			}

			mockQuerier.AssertExpectations(t)
		})
	}
}

func TestLogout(t *testing.T) {
	mockQuerier := new(MockQuerier)
	handler := createTestHandler(mockQuerier)

	req := httptest.NewRequest(http.MethodPost, "/logout", nil)
	w := httptest.NewRecorder()

	handler.Logout(w, req)

	assert.Equal(t, http.StatusOK, w.Code, "Status code should be 200")

	var response map[string]interface{}
	err := json.Unmarshal(w.Body.Bytes(), &response)
	assert.NoError(t, err, "Failed to unmarshal response")

	if message, ok := response["message"].(string); ok {
		assert.Equal(t, "Logout successful", message, "Message should match")
	}
}