-- Seed data for users
INSERT INTO users (id, username, email, password, role, created_at, updated_at) VALUES (1, 'admin', 'admin@example.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, username, email, password, role, created_at, updated_at) VALUES (2, 'user1', 'user1@example.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, username, email, password, role, created_at, updated_at) VALUES (3, 'premium1', 'premium1@example.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'PREMIUM_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed data for products
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (1, 'Laptop Pro 15', 'High-performance laptop with 15-inch display', 1299.99, 50, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (2, 'Wireless Mouse', 'Ergonomic wireless mouse with precision tracking', 29.99, 150, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (3, 'Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches', 89.99, 75, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (4, '4K Monitor', '27-inch 4K IPS monitor with HDR support', 399.99, 30, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (5, 'USB-C Hub', '7-in-1 USB-C hub with multiple ports', 49.99, 200, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (6, 'Webcam HD', '1080p HD webcam with auto-focus', 69.99, 80, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (7, 'Desk Lamp', 'LED desk lamp with adjustable brightness', 34.99, 100, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (8, 'External SSD 1TB', 'Portable 1TB SSD with USB 3.2', 129.99, 60, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (9, 'Noise Cancelling Headphones', 'Premium headphones with active noise cancellation', 199.99, 40, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (10, 'Standing Desk', 'Electric height-adjustable standing desk', 449.99, 20, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
