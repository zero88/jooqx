CREATE TABLE authors
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(70)  NOT NULL,
    country VARCHAR(100) NOT NULL
);

CREATE TABLE books
(
    id    INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE books_authors
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    author_id INT NOT NULL,
    book_id   INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors (id),
    FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TABLE all_data_types
(
    id          SERIAL PRIMARY KEY,
    f_boolean   BOOL,
    f_bit       BIT,
    f_int1      TINYINT,
    f_int2      SMALLINT,
    f_int3      MEDIUMINT,
    f_int4      INT,
    f_int8      BIGINT,
    f_decimal   DECIMAL(10, 5),
    f_float     FLOAT(5, 5),
    f_double    DOUBLE(10, 10),
    f_char      CHAR,
    f_varchar   VARCHAR(50),
    f_text      TEXT,
    f_enum      ENUM ('x-small', 'small', 'medium', 'large', 'x-large'),
    f_binary    BINARY(3),
    f_set       SET ('a', 'b', 'c', 'd'),
    f_json      JSON,
    f_date      DATE,
    f_time      TIME,
    f_datetime  DATETIME,
    f_timestamp TIMESTAMP
);

-- Function
CREATE FUNCTION hello(s CHAR(20))
    RETURNS CHAR(50)
    DETERMINISTIC
    RETURN CONCAT('Hello, ', s, '!');

CREATE FUNCTION dup(n int)
    RETURNS JSON
    DETERMINISTIC
    RETURN json_object(
        'f1', n,
        'f2', CONCAT(n, ' is text'));

-- Procedure
DROP PROCEDURE IF EXISTS count_author_by_country;
CREATE PROCEDURE count_author_by_country(IN country VARCHAR(100), OUT count INT)
SELECT COUNT(*) INTO count FROM authors WHERE authors.country = country;

DROP PROCEDURE IF EXISTS select_books_by_author;
CREATE PROCEDURE select_books_by_author(author VARCHAR(100))
SELECT * FROM books WHERE books.id in (SELECT books_authors.book_id FROM books_authors WHERE author_id = (SELECT authors.id FROM authors where authors.name = author limit 1));

DROP PROCEDURE IF EXISTS remove_author;
CREATE PROCEDURE remove_author(IN author_name varchar(100))
BEGIN
    DELETE books_authors, books FROM authors
        LEFT JOIN books_authors ON authors.id = books_authors.author_id
        LEFT JOIN books ON books_authors.book_id = books.id WHERE authors.name = author_name;
    DELETE authors FROM authors WHERE authors.name = author_name;
END;
--
