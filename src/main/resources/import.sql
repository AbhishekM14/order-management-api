-- Seed data for users
INSERT INTO users (id, username, email, password, role, created_at, updated_at) VALUES (1, 'admin', 'admin@test.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, username, email, password, role, created_at, updated_at) VALUES (2, 'user1', 'user1@test.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, username, email, password, role, created_at, updated_at) VALUES (3, 'premium1', 'premium1@test.com', '$2a$10$8K1p/a0dL1LXMBNFPWOn/.x8jEKmJ5qP3hB.4VnGrPqZHqXDLpEJa', 'PREMIUM_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed data for products
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (1, 'realme t3 ultra', 'High quality camera with 6.5-inch display', 1299.99, 50, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (2, 'samsung s24', 'High quality camera with gorilla display', 29.99, 150, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (3, 'samsung s24 ultra', 'High quality hd camera with 6.5-inch display', 89.99, 75, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (4, 'vivo t4', '16 gb ram with 256 gb rom', 399.99, 30, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (5, 'oppo k3', 'High quality camera with gorilla display', 49.99, 200, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (6, 'redmi note 7', 'with HDR display', 69.99, 80, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (7, 'redmi note 8', 'with 500 mah battery', 34.99, 100, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (8, 'iphone 16 pro', 'High quality camera with 6.5-inch display', 129.99, 60, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (9, 'iphone 17', 'High quality hd camera with 6.5-inch display', 199.99, 40, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO products (id, name, description, price, quantity, deleted, created_at, updated_at) VALUES (10, 'iphone 17 pro max', 'High quality camera with 6.5-inch display', 449.99, 20, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
