CREATE TABLE products(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(100),
    price FLOAT NOT NULL,
    stock_quantity SMALLINT NOT NULL,
    PRIMARY KEY (id)
);
