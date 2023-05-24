CREATE TABLE products(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(100),
    price FLOAT NOT NULL,
    stock_quantity SMALLINT NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO products (name, description, price, stock_quantity)
VALUES
    ('iPhone 12', 'A high-end smartphone with advanced features.', 999.99, 50),
    ('Samsung Galaxy S21', 'A flagship Android smartphone with a powerful camera.', 899.99, 30),
    ('MacBook Pro', 'A premium laptop for professional users.', 1999.99, 20),
    ('Sony PlayStation 5', 'A next-generation gaming console for immersive gaming experiences.', 499.99, 10),
    ('Bose QuietComfort 35 II', 'Wireless noise-canceling headphones for excellent audio quality.', 299.99, 15);
