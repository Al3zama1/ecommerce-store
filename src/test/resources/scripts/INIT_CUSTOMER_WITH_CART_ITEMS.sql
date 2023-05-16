INSERT INTO users (email, password, is_enabled)
VALUES ('john.last@gmail.com', '12345678', TRUE);

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1);

INSERT INTO cart(total_cost)
VALUES
(0);

INSERT INTO customers (first_name, last_name, phone_number, street, city, state, postal_code, cart_id, user_id)
VALUES ('John', 'Doe', '1234567890', '123 Main St', 'New York', 'NY', '10001', 1, 1);

INSERT INTO products (name, description, price, stock_quantity)
VALUES ('Product A', 'Description A', 9.99, 50),
       ('Product B', 'Description B', 19.99, 100);

INSERT INTO cart_items(cart_id, product_id, quantity)
VALUES
(1, 1, 4),
(1, 2, 3);
