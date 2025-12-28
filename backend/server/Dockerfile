# FROM golang:1.25.1-alpine AS builder

# WORKDIR /app

# RUN go install github.com/cosmtrek/air@latest

# COPY go.mod go.sum ./

# RUN --mount=type=cache,target=/go/pkg/mod \
#     --mount=type=cache,target=/root/.cache/go-build \
#     go mod download && go mod verify

# COPY . .

# RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o main

# FROM scratch
# WORKDIR /app

# COPY --from=builder /app /app

# CMD ["air", "-c", ".air.toml"]

FROM golang:1.25.1-alpine

WORKDIR /app

RUN apk add --no-cache git build-base

RUN go install github.com/air-verse/air@v1.63.1

COPY go.mod go.sum ./
RUN go mod download && go mod verify

COPY . .

CMD ["air", "-c", ".air.toml"]


