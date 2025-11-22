package health

import (
	"net/http"

	"github.com/saleemlawal/FinSight/backend/utils"
)

func Healthz(w http.ResponseWriter, r *http.Request) {
	utils.SendSuccess(w, "API is live ✅", nil)
}
