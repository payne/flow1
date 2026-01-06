-- Initialize database with two schemas for the Order Flow application
-- app_schema: Application data (orders, customers, inventory, etc.)
-- flowable_schema: Flowable engine tables (auto-created by Flowable)

-- Create application schema
CREATE SCHEMA IF NOT EXISTS app_schema;

-- Create flowable schema
CREATE SCHEMA IF NOT EXISTS flowable_schema;

-- Grant permissions to the application user
GRANT ALL ON SCHEMA app_schema TO orderflow_user;
GRANT ALL ON SCHEMA flowable_schema TO orderflow_user;
GRANT ALL ON ALL TABLES IN SCHEMA app_schema TO orderflow_user;
GRANT ALL ON ALL TABLES IN SCHEMA flowable_schema TO orderflow_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA app_schema TO orderflow_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA flowable_schema TO orderflow_user;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA app_schema GRANT ALL ON TABLES TO orderflow_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA flowable_schema GRANT ALL ON TABLES TO orderflow_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA app_schema GRANT ALL ON SEQUENCES TO orderflow_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA flowable_schema GRANT ALL ON SEQUENCES TO orderflow_user;

-- Display confirmation
\echo 'Database schemas created successfully:'
\echo '  - app_schema (for application data)'
\echo '  - flowable_schema (for Flowable BPMN engine)'
