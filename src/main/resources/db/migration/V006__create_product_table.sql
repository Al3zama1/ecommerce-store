CREATE TABLE products(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(30) NOT NULL,
    description varchar(50),
    price FLOAT NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
