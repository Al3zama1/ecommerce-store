CREATE TABLE cart(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    total_cost FLOAT NOT NULL DEFAULT 0,
    customer_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (customer_id) REFERENCES customers (id),
    PRIMARY KEY (id)
);
