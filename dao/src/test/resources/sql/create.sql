CREATE TABLE IF NOT EXISTS certificates
(
    id               INT PRIMARY KEY AUTO_INCREMENT,
    name             VARCHAR(50) NOT NULL,
    description      TEXT        NOT NULL,
    price            INT CHECK ( price >= 0 ),
    duration         INT CHECK ( duration > 0 ),
    last_update_date DATETIME    NOT NULL,
    create_date      DATETIME    NOT NULL
);

CREATE TABLE IF NOT EXISTS tag
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(25) NOT NULL
);

CREATE TABLE IF NOT EXISTS certificate_tag
(
    certificate_id INT,
    tag_id         INT,
    FOREIGN KEY (certificate_id) REFERENCES certificates (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);

CREATE TABLE IF NOT EXISTS user
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(25) NOT NULL,
    last_name  VARCHAR(25) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_order
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    user_id        INT,
    certificate_id INT,
    cost           INT CHECK ( cost >= 0 ),
    timestamp      DATETIME NOT NULL,
    UNIQUE (user_id, certificate_id)
);