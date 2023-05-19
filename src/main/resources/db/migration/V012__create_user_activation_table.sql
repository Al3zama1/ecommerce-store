CREATE TABLE user_activation(
    token UUID,
    created_date TIMESTAMP NOT NULL,
    expiration_date TIMESTAMP,
    user_id BIGINT UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    PRIMARY KEY (token)
);
