-- Initialize database schemas for the order flow application

-- Create app schema for application data
CREATE SCHEMA IF NOT EXISTS app_schema;

-- Create flowable schema for Flowable workflow engine
CREATE SCHEMA IF NOT EXISTS flowable_schema;

-- Grant privileges to orderflow_user
GRANT ALL PRIVILEGES ON SCHEMA app_schema TO orderflow_user;
GRANT ALL PRIVILEGES ON SCHEMA flowable_schema TO orderflow_user;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA app_schema GRANT ALL ON TABLES TO orderflow_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA flowable_schema GRANT ALL ON TABLES TO orderflow_user;
