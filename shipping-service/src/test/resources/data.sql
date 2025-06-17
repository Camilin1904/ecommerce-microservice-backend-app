-- Initialize test data for OrderItems
INSERT INTO order_items (product_id, order_id, ordered_quantity, created_at) VALUES 
(1, 1, 2, CURRENT_TIMESTAMP()),
(2, 1, 1, CURRENT_TIMESTAMP()),
(3, 2, 3, CURRENT_TIMESTAMP()),
(4, 3, 1, CURRENT_TIMESTAMP()),
(5, 4, 5, CURRENT_TIMESTAMP());
