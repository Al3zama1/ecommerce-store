CREATE TABLE employee_roles(
    employee_id BIGINT NOT NULL,
    role_id SMALLINT NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    PRIMARY KEY (employee_id, role_id)
);
