#!/usr/bin/env bash
# ============================================================
# One-shot local setup for the AI-Based Integrated MOOC Platform.
# - Copies .env.example to .env if missing
# - Builds the backend
# - Installs frontend dependencies
# ============================================================
set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "== MOOC Platform setup =="

if [ ! -f "$ROOT_DIR/.env" ]; then
  cp "$ROOT_DIR/.env.example" "$ROOT_DIR/.env"
  echo "Created .env from .env.example — update it with your DB and AI credentials."
fi

echo "-- Building backend (Spring Boot) --"
cd "$ROOT_DIR/backend"
./mvnw -q -DskipTests package || mvn -q -DskipTests package

echo "-- Installing frontend dependencies --"
cd "$ROOT_DIR/frontend"
npm install

echo "== Setup complete =="
echo "Run the database migration:  bash scripts/seed_database.sh"
echo "Start backend:                cd backend && ./mvnw spring-boot:run"
echo "Start frontend:               cd frontend && npm run dev"
