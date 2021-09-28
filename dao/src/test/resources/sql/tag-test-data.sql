INSERT INTO tag
VALUES (1, 'first');
INSERT INTO tag
VALUES (2, 'second');
INSERT INTO tag
VALUES (3, 'third');
INSERT INTO tag
VALUES (4, 'forth');

INSERT INTO certificate(id, name, description, price, duration, last_update_date, create_date)
VALUES (1, 'first', 'first', 5, 5, '2021-09-16 19:57:35.000000', '2021-09-16 19:57:40.000000');
INSERT INTO certificate(id, name, description, price, duration, last_update_date, create_date)
VALUES (2, 'second', 'second', 5, 5, '2021-09-16 19:57:35.000000', '2021-09-16 19:57:40.000000');
INSERT INTO certificate(id, name, description, price, duration, last_update_date, create_date)
VALUES (3, 'third', 'third', 5, 5, '2021-09-16 19:57:35.000000', '2021-09-16 19:57:40.000000');
INSERT INTO certificate(id, name, description, price, duration, last_update_date, create_date)
VALUES (4, 'forth', 'forth', 5, 5, '2021-09-16 19:57:35.000000', '2021-09-16 19:57:40.000000');
INSERT INTO certificate(id, name, description, price, duration, last_update_date, create_date)
VALUES (5, 'fifth', 'fifth', 5, 5, '2021-09-16 19:57:35.000000', '2021-09-16 19:57:40.000000');
INSERT INTO certificate(id, name, description, price, duration, last_update_date, create_date)
VALUES (6, 'sixth', 'sixth', 5, 5, '2021-09-16 19:57:35.000000', '2021-09-16 19:57:40.000000');

INSERT INTO certificate_tag
VALUES (1, 1);
INSERT INTO certificate_tag
VALUES (1, 2);
INSERT INTO certificate_tag
VALUES (1, 3);
INSERT INTO certificate_tag
VALUES (1, 4);
INSERT INTO certificate_tag
VALUES (2, 1);
INSERT INTO certificate_tag
VALUES (2, 2);
INSERT INTO certificate_tag
VALUES (2, 4);
INSERT INTO certificate_tag
VALUES (3, 4);
INSERT INTO certificate_tag
VALUES (4, 2);

INSERT INTO user_order
VALUES (1, 1, 1, 5, '2021-09-21 15:33:20.000000');
INSERT INTO user_order
VALUES (2, 1, 2, 5, '2021-09-21 15:33:20.000000');
INSERT INTO user_order
VALUES (3, 1, 3, 5, '2021-09-21 15:33:20.000000');
INSERT INTO user_order
VALUES (4, 2, 4, 5, '2021-09-21 15:33:20.000000');
INSERT INTO user_order
VALUES (5, 2, 5, 5, '2021-09-21 15:33:20.000000');
INSERT INTO user_order
VALUES (6, 3, 3, 5, '2021-09-21 15:33:20.000000');
INSERT INTO user_order
VALUES (7, 3, 2, 20, '2021-09-21 15:33:20.000000');
INSERT INTO user_order
VALUES (8, 2, 1, 20, '2021-09-21 15:33:20.000000');