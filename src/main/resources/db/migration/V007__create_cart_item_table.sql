CREATE TABLE cart_items(
   cart_id BIGINT NOT NULL,
   product_id BIGINT NOT NULL,
   quantity SMALLINT NOT NULL,
   PRIMARY KEY (cart_id, product_id)
);
