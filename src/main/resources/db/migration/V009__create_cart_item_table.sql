CREATE TABLE cart_items(
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    price FLOAT NOT NULL,
    PRIMARY KEY (cart_id, product_id)
);
