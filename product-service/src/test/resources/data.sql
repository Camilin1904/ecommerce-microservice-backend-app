-- Initialize Categories
INSERT INTO categories (category_id, category_title, image_url, parent_category_id) VALUES 
(1, 'Electronics', 'https://example.com/images/electronics.jpg', NULL),
(2, 'Clothing', 'https://example.com/images/clothing.jpg', NULL),
(3, 'Home & Kitchen', 'https://example.com/images/home.jpg', NULL),
(4, 'Smartphones', 'https://example.com/images/smartphones.jpg', 1),
(5, 'Laptops', 'https://example.com/images/laptops.jpg', 1),
(6, 'Men''s Clothing', 'https://example.com/images/mens.jpg', 2),
(7, 'Women''s Clothing', 'https://example.com/images/womens.jpg', 2);

-- Initialize Products
INSERT INTO products (product_id, product_title, image_url, sku, price_unit, quantity, category_id) VALUES 
(1, 'iPhone 13 Pro', 'https://example.com/images/iphone13.jpg', 'SKU-IPH-13', 999.99, 50, 4),
(2, 'Samsung Galaxy S21', 'https://example.com/images/s21.jpg', 'SKU-SAM-S21', 799.99, 30, 4),
(3, 'MacBook Pro 16"', 'https://example.com/images/macbookpro.jpg', 'SKU-MAC-16', 2399.99, 25, 5),
(4, 'Dell XPS 15', 'https://example.com/images/xps15.jpg', 'SKU-DEL-XPS15', 1799.99, 15, 5),
(5, 'Men''s Denim Jacket', 'https://example.com/images/denim_jacket.jpg', 'SKU-MEN-DJ1', 59.99, 100, 6),
(6, 'Women''s Summer Dress', 'https://example.com/images/summer_dress.jpg', 'SKU-WMN-SD1', 49.99, 120, 7),
(7, 'Coffee Maker', 'https://example.com/images/coffee_maker.jpg', 'SKU-HOM-CM1', 89.99, 40, 3),
(8, 'Blender', 'https://example.com/images/blender.jpg', 'SKU-HOM-BL1', 49.99, 35, 3);
