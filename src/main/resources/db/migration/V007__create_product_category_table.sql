CREATE TABLE product_categories(
    id INTEGER GENERATED ALWAYS AS IDENTITY,
    product_id BIGINT NOT NULL,
    category_id INTEGER NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    UNIQUE (product_id, category_id),
    PRIMARY KEY (id)
)
