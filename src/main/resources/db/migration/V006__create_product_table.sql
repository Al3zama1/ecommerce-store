CREATE TABLE products(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(30) NOT NULL,
    description varchar(50),
    price FLOAT NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

INSERT INTO products (name, description, price, stock_quantity)
VALUES ('Product A', 'Description A', 9.99, 50),
       ('Product B', 'Description B', 19.99, 100),
       ('Product C', 'Description C', 14.50, 75),
       ('Product D', 'Description D', 8.75, 30),
       ('Product E', 'Description E', 12.99, 60);
