-- Sample data for Order Management System
SET search_path TO app_schema;

-- Insert sample customers
INSERT INTO customer (first_name, last_name, email, phone, address_line1, city, state, zip_code, country, created_at, updated_at)
VALUES
    ('John', 'Doe', 'john.doe@email.com', '555-0101', '123 Main St', 'New York', 'NY', '10001', 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Jane', 'Smith', 'jane.smith@email.com', '555-0102', '456 Oak Ave', 'Los Angeles', 'CA', '90001', 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Bob', 'Johnson', 'bob.johnson@email.com', '555-0103', '789 Pine Rd', 'Chicago', 'IL', '60601', 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Alice', 'Williams', 'alice.williams@email.com', '555-0104', '321 Elm St', 'Houston', 'TX', '77001', 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Charlie', 'Brown', 'charlie.brown@email.com', '555-0105', '654 Maple Dr', 'Phoenix', 'AZ', '85001', 'USA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample electronics items
INSERT INTO item (sku, name, description, category, price, weight_kg, requires_signature, warranty_months, created_at, updated_at)
VALUES
    ('ELEC-001', 'Laptop Pro 15"', 'High-performance laptop with 16GB RAM', 'ELECTRONICS', 1299.99, 2.5, true, 24, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ELEC-002', 'Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 'ELECTRONICS', 29.99, 0.2, false, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ELEC-003', 'Bluetooth Headphones', 'Noise-cancelling over-ear headphones', 'ELECTRONICS', 199.99, 0.5, false, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ELEC-004', '4K Monitor 27"', 'Ultra HD display with HDR support', 'ELECTRONICS', 449.99, 6.0, true, 36, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ELEC-005', 'Smartphone X', 'Latest flagship smartphone with 5G', 'ELECTRONICS', 899.99, 0.3, true, 24, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample clothing items
INSERT INTO item (sku, name, description, category, price, weight_kg, size, created_at, updated_at)
VALUES
    ('CLO-001', 'Cotton T-Shirt', 'Comfortable cotton t-shirt', 'CLOTHING', 19.99, 0.2, 'M', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CLO-002', 'Denim Jeans', 'Classic fit denim jeans', 'CLOTHING', 49.99, 0.6, 'L', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CLO-003', 'Winter Jacket', 'Insulated winter jacket', 'CLOTHING', 129.99, 1.2, 'XL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CLO-004', 'Running Shoes', 'Lightweight running shoes', 'CLOTHING', 89.99, 0.8, '10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CLO-005', 'Baseball Cap', 'Adjustable baseball cap', 'CLOTHING', 14.99, 0.1, 'One Size', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample food items
INSERT INTO item (sku, name, description, category, price, weight_kg, requires_refrigeration, created_at, updated_at)
VALUES
    ('FOOD-001', 'Organic Milk 1L', 'Fresh organic whole milk', 'FOOD', 4.99, 1.0, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('FOOD-002', 'Gourmet Cheese', 'Aged cheddar cheese block', 'FOOD', 12.99, 0.5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('FOOD-003', 'Granola Bars Box', 'Box of 12 granola bars', 'FOOD', 8.99, 0.4, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('FOOD-004', 'Ice Cream 1L', 'Premium vanilla ice cream', 'FOOD', 6.99, 1.0, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('FOOD-005', 'Pasta Package', 'Italian pasta 500g', 'FOOD', 3.99, 0.5, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert inventory for all items
INSERT INTO inventory (item_id, quantity_available, quantity_reserved, reorder_level, reorder_quantity, warehouse_location, created_at, updated_at)
SELECT
    id,
    CASE
        WHEN category = 'ELECTRONICS' THEN 50
        WHEN category = 'CLOTHING' THEN 100
        ELSE 200
    END,
    0,
    10,
    50,
    CASE
        WHEN category = 'ELECTRONICS' THEN 'WAREHOUSE-A'
        WHEN category = 'CLOTHING' THEN 'WAREHOUSE-B'
        ELSE 'WAREHOUSE-C'
    END,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM item;

-- Display confirmation
DO $$
BEGIN
    RAISE NOTICE 'Sample data inserted successfully:';
    RAISE NOTICE '  - 5 customers';
    RAISE NOTICE '  - 15 items (5 electronics, 5 clothing, 5 food)';
    RAISE NOTICE '  - 15 inventory records';
END $$;
