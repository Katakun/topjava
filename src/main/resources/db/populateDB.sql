DELETE
FROM user_role;
DELETE
FROM meals;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, date_time, description, calories)
VALUES (100000, '2023-01-08 08:05:06', 'Завтрак user_id 100_000', 500),
       (100000, '2023-01-08 13:05:06', 'Обед user_id 100_000', 1000),
       (100000, '2023-01-08 20:05:06', 'Ужин user_id 100_000', 500),
       (100001, '2023-01-08 07:45:06', 'Завтрак user_id 100_001', 1000),
       (100001, '2023-01-08 13:15:06', 'Обед user_id 100_001', 501),
       (100001, '2023-01-08 19:25:06', 'Ужин user_id 100_001', 500);