CREATE TABLE orders(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    total_cost FLOAT NOT NULL,
    date_placed TIMESTAMP NOT NULL,
    date_shipped TIMESTAMP,
    date_delivered TIMESTAMP,
    status_id SMALLINT NOT NULL,
    customer_id BIGINT NOT NULL,
    FOREIGN KEY (id) REFERENCES order_status (id),
    FOREIGN KEY (customer_id) REFERENCES customers (id),
    PRIMARY KEY (id)
);
