CREATE TABLE order_items(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    quantity SMALLINT NOT NULL,
    price FLOAT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (order_id) REFERENCES orders (id),
    PRIMARY KEY (id)
);
