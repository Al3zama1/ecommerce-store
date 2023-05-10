CREATE TABLE users(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    email VARCHAR(30) UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
