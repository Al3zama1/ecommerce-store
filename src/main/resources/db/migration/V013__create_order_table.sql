CREATE TABLE orders(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    total_cost FLOAT NOT NULL,
    date_placed TIMESTAMP NOT NULL,
    date_shipped TIMESTAMP NOT NULL,
    date_delivered TIMESTAMP NOT NULL,
    order_status_id SMALLINT NOT NULL,
    customer_id BIGINT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id),
    FOREIGN KEY (order_status_id) REFERENCES order_status (id),
    PRIMARY KEY (id)
);
