INSERT INTO users (id, username, email, password, gender, created_at)
VALUES (1, 'musty', 'musty@example.com', '1234', 'MALE', CURRENT_TIMESTAMP);

INSERT INTO tasks (id, title, description, status, user_id, created_at)
VALUES (2, 'Initial Task', 'Setup test environment', 'PENDING', 1, CURRENT_TIMESTAMP);