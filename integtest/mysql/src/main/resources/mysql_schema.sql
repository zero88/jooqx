CREATE TABLE authors
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(70)  NOT NULL,
    country     VARCHAR(100) NOT NULL
);

CREATE TABLE books
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(50) NOT NULL
);

CREATE TABLE books_authors
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    author_id   INT NOT NULL,
    book_id     INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors (id),
    FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TABLE all_data_types
(
    id              SERIAL PRIMARY KEY,
    f_boolean       BOOL,
    f_bit           BIT,
    f_int1          TINYINT,
    f_int2          SMALLINT,
    f_int3          MEDIUMINT,
    f_int4          INT,
    f_int8          BIGINT,
    f_decimal       DECIMAL(10,5),
    f_float         FLOAT(5,5),
    f_double        DOUBLE(10,10),
    f_char          CHAR,
    f_varchar       VARCHAR(50),
    f_text          TEXT,
    f_enum          ENUM('x-small', 'small', 'medium', 'large', 'x-large'),
    f_binary        BINARY(3),
    f_set           SET('a', 'b', 'c', 'd'),
    f_json          JSON,
    f_date          DATE,
    f_time          TIME,
    f_datetime      DATETIME,
    f_timestamp     TIMESTAMP
);
