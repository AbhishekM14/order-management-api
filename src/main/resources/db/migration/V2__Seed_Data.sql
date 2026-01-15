-- V2__Seed_Data.sql

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@example.com', '$2a$10$xZnJ5CXJ8gXHvN5xXH5xXeY5xXH5xXH5xXH5xXH5xXH5xXH5xXH5x', 'ADMIN'),
('user1', 'user1@example.com', '$2a$10$xZnJ5CXJ8gXHvN5xXH5xXeY5xXH5xXH5xXH5xXH5xXH5xXH5xXH5x', 'USER'),
('premium1', 'premium1@example.com', '$2a$10$xZnJ5CXJ8gXHvN5xXH5xXeY5xXH5xXH5xXH5xXH5xXH5xXH5xXH5x', 'PREMIUM_USER');

-- Insert sample products
INSERT INTO products (name, description, price, quantity) VALUES
('realme t3 ultra', 'High quality camera with 6.5-inch display'', 129.99, 50),
('samsung s24', 'High quality camera with gorilla glass', 29.99, 200),
('samsung s24 ultra', 'High quality hd camera with 6.5-inch display', 89.99, 150),
('vivo t4', '16 gb ram with 256 gb rom', 399.99, 75),
('oppo k3', 'High quality camera with gorilla display', 49.99, 300),
('redmi note 7', 'with HDR display', 79.99, 100),
('redmi note 8', 'with 500 mah battery', 34.99, 180),
('iphone 16 pro', 'High quality hd camera with 6.5-inch display', 19.99, 250),
('iphone 17', 'High quality hd camera with 6.5-inch display', 159.99, 120),
('iphone 17 pro max', 'High quality hd camera with 6.5-inch display', 239.99, 200);
