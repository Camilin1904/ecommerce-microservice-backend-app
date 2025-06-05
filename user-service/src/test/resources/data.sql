
INSERT INTO users (user_id, first_name, last_name, image_url, email, phone) VALUES 
(1, 'Admin', 'User', 'https://example.com/images/admin.jpg', 'admin@ecommerce.com', '+1234567890'),
(2, 'John', 'Doe', 'https://example.com/images/john.jpg', 'john.doe@example.com', '+1234567891'),
(3, 'Jane', 'Smith', 'https://example.com/images/jane.jpg', 'jane.smith@example.com', '+1234567892'),
(4, 'Mike', 'Manager', 'https://example.com/images/mike.jpg', 'mike.manager@ecommerce.com', '+1234567893');


INSERT INTO credentials (credential_id, username, password, role, is_enabled, is_account_non_expired, is_credentials_non_expired, is_account_non_locked, user_id) VALUES 
(1, 'admin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ROLE_ADMIN', true, true, true, true, 1),
(2, 'user1', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ROLE_USER', true, true, true, true, 2),
(3, 'user2', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ROLE_USER', true, true, true, true, 3),
(4, 'manager', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ROLE_ADMIN', true, true, true, true, 4);

-- Initialize Credentials and Users



-- Initialize Addresses
INSERT INTO address (address_id, full_address, postal_code, city, user_id) VALUES 
(1, '123 Admin Street', '12345', 'Admin City', 1),
(2, '456 Main Street', '54321', 'New York', 2),
(3, '789 Oak Avenue', '67890', 'Los Angeles', 3),
(4, '321 Pine Street', '09876', 'Chicago', 4),
(5, '654 Elm Street', '11111', 'Houston', 2);
