-- Flyway migration: Create application schema tables
-- Schema: app_schema
-- Description: Core tables for order management system

SET search_path TO app_schema;

-- Customer table
CREATE TABLE customer (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(20),
    country VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Item category type
CREATE TYPE item_category AS ENUM ('ELECTRONICS', 'CLOTHING', 'FOOD');

-- Order status type
CREATE TYPE order_status AS ENUM (
    'PENDING',
    'VALIDATING',
    'VALIDATION_FAILED',
    'PAYMENT_PROCESSING',
    'PAYMENT_FAILED',
    'PAYMENT_COMPLETED',
    'FULFILLING',
    'FULFILLMENT_FAILED',
    'SHIPPING',
    'SHIPPED',
    'DELIVERED',
    'CANCELLED'
);

-- Item table
CREATE TABLE item (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category item_category NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    weight_kg DECIMAL(8, 3),
    requires_signature BOOLEAN DEFAULT FALSE,
    requires_refrigeration BOOLEAN DEFAULT FALSE,
    warranty_months INTEGER,
    size VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inventory table
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES item(id),
    quantity_available INTEGER NOT NULL DEFAULT 0,
    quantity_reserved INTEGER NOT NULL DEFAULT 0,
    reorder_level INTEGER DEFAULT 10,
    reorder_quantity INTEGER DEFAULT 50,
    warehouse_location VARCHAR(50),
    last_restocked_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_item FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
);

-- Order table
CREATE TABLE "order" (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customer(id),
    status order_status NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL,
    shipping_address_line1 VARCHAR(255),
    shipping_address_line2 VARCHAR(255),
    shipping_city VARCHAR(100),
    shipping_state VARCHAR(50),
    shipping_zip_code VARCHAR(20),
    shipping_country VARCHAR(50),
    payment_method VARCHAR(50),
    payment_reference VARCHAR(100),
    process_instance_id VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- Order item table
CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES "order"(id),
    item_id BIGINT NOT NULL REFERENCES item(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES "order"(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_item FOREIGN KEY (item_id) REFERENCES item(id)
);

-- Approval table (for tracking approvals needed by specific categories)
CREATE TABLE approval (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES "order"(id),
    approval_type VARCHAR(50) NOT NULL,
    approved BOOLEAN,
    approver_name VARCHAR(100),
    approval_comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    CONSTRAINT fk_approval_order FOREIGN KEY (order_id) REFERENCES "order"(id) ON DELETE CASCADE
);

-- Shipment table
CREATE TABLE shipment (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES "order"(id),
    tracking_number VARCHAR(100),
    carrier VARCHAR(50),
    shipping_method VARCHAR(50),
    estimated_delivery_date DATE,
    actual_delivery_date DATE,
    shipped_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shipment_order FOREIGN KEY (order_id) REFERENCES "order"(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_item_category ON item(category);
CREATE INDEX idx_item_sku ON item(sku);
CREATE INDEX idx_order_customer_id ON "order"(customer_id);
CREATE INDEX idx_order_status ON "order"(status);
CREATE INDEX idx_order_number ON "order"(order_number);
CREATE INDEX idx_order_process_instance ON "order"(process_instance_id);
CREATE INDEX idx_order_item_order_id ON order_item(order_id);
CREATE INDEX idx_inventory_item_id ON inventory(item_id);
CREATE INDEX idx_approval_order_id ON approval(order_id);
CREATE INDEX idx_shipment_order_id ON shipment(order_id);

-- Create unique constraint on inventory to ensure one record per item
CREATE UNIQUE INDEX idx_inventory_item_unique ON inventory(item_id);

-- Comments for documentation
COMMENT ON TABLE customer IS 'Stores customer information for order placement';
COMMENT ON TABLE item IS 'Product catalog with three categories: ELECTRONICS, CLOTHING, FOOD';
COMMENT ON TABLE inventory IS 'Inventory management with available and reserved quantities';
COMMENT ON TABLE "order" IS 'Customer orders with Flowable process instance tracking';
COMMENT ON TABLE order_item IS 'Line items for each order';
COMMENT ON TABLE approval IS 'Approval tracking for workflows (QA for electronics, safety for food)';
COMMENT ON TABLE shipment IS 'Shipping and tracking information';

COMMENT ON COLUMN "order".process_instance_id IS 'Links to Flowable BPMN process instance';
COMMENT ON COLUMN inventory.quantity_reserved IS 'Quantity reserved during order validation';
COMMENT ON COLUMN item.category IS 'Determines which BPMN process to trigger';
