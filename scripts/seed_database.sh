#!/usr/bin/env bash
# ============================================================
# Creates the database, applies the schema, and optionally loads
# sample data for local development. Targets MariaDB (the `mariadb`
# CLI client, falling back to `mysql` since it's wire-compatible).
#
# Usage: bash scripts/seed_database.sh [--with-sample-data]
# Requires: mariadb (or mysql) client, and DB_USERNAME / DB_PASSWORD
# env vars (or edit the defaults below).
# ============================================================
set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DB_DIR="$ROOT_DIR/database"

DB_USER="${DB_USERNAME:-root}"
DB_PASS="${DB_PASSWORD:-root}"

CLIENT="mysql"
if command -v mariadb >/dev/null 2>&1; then
  CLIENT="mariadb"
fi

echo "== Applying schema (using '$CLIENT' client) =="
"$CLIENT" -u "$DB_USER" -p"$DB_PASS" < "$DB_DIR/database.sql"

for f in users courses enrollments quizzes ai; do
  echo "-- Loading schema/$f.sql --"
  "$CLIENT" -u "$DB_USER" -p"$DB_PASS" mooc_platform < "$DB_DIR/schema/$f.sql" 2>/dev/null || true
done

if [ "$1" == "--with-sample-data" ]; then
  echo "== Loading sample data =="
  "$CLIENT" -u "$DB_USER" -p"$DB_PASS" mooc_platform < "$DB_DIR/seed/sample_data.sql"
  echo "Sample accounts (password: Password123):"
  echo "  admin@mooc.dev / instructor@mooc.dev / student@mooc.dev"
fi

echo "== Database ready =="
