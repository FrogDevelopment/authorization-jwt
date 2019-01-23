INSERT INTO users (username, password, enabled)
VALUES ('admin', '$2a$10$gtaM/RhJYNt2TnACBjVUJuffKfFwz.PF9IjPjFoUdGeSliU8qxV6K', 1);
INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_USER');
