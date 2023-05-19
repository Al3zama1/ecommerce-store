CREATE TABLE user_activation(
    token UUID,
    created_date TIMESTAMP NOT NULL,
    expiration_date TIMESTAMP,
    PRIMARY KEY (token)
);
