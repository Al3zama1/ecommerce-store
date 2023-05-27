CREATE TABLE order_items(
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity SMALLINT NOT NULL,
    price FLOAT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES products (id),
    PRIMARY KEY (order_id, product_id)
);
