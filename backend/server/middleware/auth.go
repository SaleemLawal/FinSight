package middleware

import (
	"context"
	"errors"
	"net/http"
	"os"
	"strings"

	"github.com/golang-jwt/jwt/v5"
	"github.com/saleemlawal/FinSight/backend/utils"
)

type contextKey string

const UserIDKey contextKey = "userId"

func AuthMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func (w http.ResponseWriter, r *http.Request) {
		authHeader := r.Header.Get("Authorization")

		if authHeader == "" {
            utils.SendError(w, "Unauthorized", http.StatusUnauthorized, "UNAUTHORIZED")
            return
        }

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")

		parsedToken, err := jwt.Parse(tokenString, func(token *jwt.Token) (any, error) {
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, jwt.ErrSignatureInvalid
			}
			return []byte(os.Getenv("JWT_SECRET")), nil
		})

		if err != nil || !parsedToken.Valid {
			utils.SendError(w, "Invalid token", http.StatusUnauthorized, "INVALID_TOKEN")
            return
		}

		claims, ok := parsedToken.Claims.(jwt.MapClaims)
        if !ok {
            utils.SendError(w, "Invalid token claims", http.StatusUnauthorized, "INVALID_TOKEN")
            return
        }

		userIdFloat, ok := claims["userId"].(float64)
		if !ok {
            utils.SendError(w, "Invalid user ID in token", http.StatusUnauthorized, "INVALID_TOKEN")
            return
        }
		userId := int32(userIdFloat)

		ctx := context.WithValue(r.Context(), UserIDKey, userId)
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}

// Helper to get user ID from context
func GetUserID(r *http.Request) (int32, error) {
    userId, ok := r.Context().Value(UserIDKey).(int32)
    if !ok {
        return 0, errors.New("user ID not found in context")
    }
    return userId, nil
}