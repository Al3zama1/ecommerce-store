CREATE TABLE user_roles(
    user_id BIGINT NOT NULL,
    role_id SMALLINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
