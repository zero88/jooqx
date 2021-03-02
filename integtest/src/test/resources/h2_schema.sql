CREATE TABLE language
(
    id          int     NOT NULL PRIMARY KEY AUTO_INCREMENT,
    cd          CHAR(2) NOT NULL,
    description VARCHAR2(50)
);

CREATE TABLE author
(
    id            int          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    first_name    VARCHAR2(50),
    last_name     VARCHAR2(50) NOT NULL,
    date_of_birth DATE         NOT NULL,
    distinguished BIT
);

CREATE TABLE book
(
    id           int           NOT NULL PRIMARY KEY AUTO_INCREMENT,
    author_id    NUMBER(7)     NOT NULL,
    title        VARCHAR2(400) NOT NULL,
    published_in TIMESTAMP     NOT NULL,
    language_id  NUMBER(7)     NOT NULL,

    CONSTRAINT fk_book_author FOREIGN KEY (author_id) REFERENCES author (id),
    CONSTRAINT fk_book_language FOREIGN KEY (language_id) REFERENCES language (id)
);

CREATE TABLE book_store
(
    name VARCHAR2(400) NOT NULL UNIQUE
);

CREATE TABLE book_to_book_store
(
    name    VARCHAR2(400) NOT NULL,
    book_id INTEGER       NOT NULL,
    stock   INTEGER,

    PRIMARY KEY (name, book_id),
    CONSTRAINT fk_b2bs_book_store FOREIGN KEY (name) REFERENCES book_store (name) ON DELETE CASCADE,
    CONSTRAINT fk_b2bs_book FOREIGN KEY (book_id) REFERENCES book (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS AllDataTypes
(
    id           INT AUTO_INCREMENT,
    f_bool       bit,
    f_date       TIMESTAMP,
    f_str        varchar(31),
    f_value_json text,
    f_timestamp  timestamp,
    f_timestampz timestamp,
    f_date_1     date,
    f_time       time,
    f_duration   varchar(20),
    f_period     varchar(20),
    CONSTRAINT AllDataTypes PRIMARY KEY (id)
);
