INSERT INTO products (name, description, price, stock_quantity)
VALUES
    ('Product 1', 'Description 1', 9.99, 10),
    ('Product 2', 'Description 2', 19.99, 5),
    ('Product 3', 'Description 3', 29.99, 15),
    ('Product 4', 'Description 4', 39.99, 20),
    ('Product 5', 'Description 5', 49.99, 8),
    ('Product 6', 'Description 6', 59.99, 12),
    ('Product 7', 'Description 7', 69.99, 3),
    ('Product 8', 'Description 8', 79.99, 18),
    ('Product 9', 'Description 9', 89.99, 6),
    ('Product 10', 'Description 10', 99.99, 14),
    ('Product 11', 'Description 11', 109.99, 9),
    ('Product 12', 'Description 12', 119.99, 22),
    ('Product 13', 'Description 13', 129.99, 17),
    ('Product 14', 'Description 14', 139.99, 11),
    ('Product 15', 'Description 15', 149.99, 4),
    ('Product 16', 'Description 16', 159.99, 13),
    ('Product 17', 'Description 17', 169.99, 7),
    ('Product 18', 'Description 18', 179.99, 16),
    ('Product 19', 'Description 19', 189.99, 2),
    ('Product 20', 'Description 20', 199.99, 19);

INSERT INTO categories (category)
VALUES
('EDUCATION'),
('TECHNOLOGY');

INSERT INTO product_categories (product_id, category_id)
VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 1),
(11, 2),
(12, 2),
(13, 2),
(14, 2),
(15, 2),
(16, 2),
(17, 2),
(18, 2),
(19, 2),
(20, 2);
