#!/bin/bash
set -e

# This script runs inside the Docker container
# Environment variables are already available from docker-compose

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create schema
    CREATE SCHEMA IF NOT EXISTS personal_manager;

    -- Set ownership
    ALTER SCHEMA personal_manager OWNER TO $POSTGRES_USER;

    -- Grant privileges
    GRANT ALL ON SCHEMA personal_manager TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA personal_manager TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA personal_manager TO $POSTGRES_USER;
    ALTER DEFAULT PRIVILEGES FOR USER $POSTGRES_USER IN SCHEMA personal_manager GRANT ALL ON TABLES TO $POSTGRES_USER;
    ALTER DEFAULT PRIVILEGES FOR USER $POSTGRES_USER IN SCHEMA personal_manager GRANT ALL ON SEQUENCES TO $POSTGRES_USER;

    -- Set default search path
    ALTER USER $POSTGRES_USER SET search_path TO personal_manager, public;
EOSQL