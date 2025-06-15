-- Initialize Orders
INSERT INTO orders (order_id, order_date, order_desc, order_fee, order_status, user_id) VALUES 
(1, '2025-06-01 10:30:00', 'Electronics order', 25.99, 'PENDING', 1),
(2, '2025-06-02 14:15:00', 'Clothing order', 15.50, 'CONFIRMED', 2),
(3, '2025-06-03 09:45:00', 'Mixed items order', 30.00, 'DELIVERED', 3),
(4, '2025-06-04 16:20:00', 'Home appliances order', 20.75, 'SHIPPED', 4);

-- Initialize Carts
INSERT INTO carts (cart_id, user_id) VALUES 
(1, 1),
(2, 2),
(3, 3),
(4, 4);

-- Initialize Cart Items
INSERT INTO cart_items (cart_item_id, quantity, unit_price, cart_id, product_id) VALUES 
(1, 2, 999.99, 1, 1),
(2, 1, 799.99, 1, 2),
(3, 1, 59.99, 2, 5),
(4, 2, 49.99, 2, 6),
(5, 1, 2399.99, 3, 3),
(6, 3, 89.99, 4, 7);
