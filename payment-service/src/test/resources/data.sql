-- Initialize Payments
INSERT INTO payments (payment_id, payment_status, is_payed, order_id) VALUES 
(1, 'COMPLETED', true, 1),
(2, 'NOT_STARTED', false, 2),
(3, 'COMPLETED', true, 3),
(4, 'NOT_STARTED', false, 4),
(5, 'IN_PROGRESS', false, 1),
(6, 'COMPLETED', true, 2);
