CREATE TABLE authors
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(70)  NOT NULL,
    country VARCHAR(100) NOT NULL
);

CREATE TABLE books
(
    id    SERIAL PRIMARY KEY,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE books_authors
(
    id       SERIAL PRIMARY KEY,
    author_id INT NOT NULL,
    book_id   INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors (id),
    FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TABLE all_data_types
(
    id          SERIAL PRIMARY KEY,
    boolean     BOOLEAN,
    int2        INT2,
    int4        INT4,
    int8        INT8,
    float4      FLOAT4,
    float8      FLOAT8,
    char        CHAR,
    varchar     VARCHAR,
    text        TEXT,
--     enum        mood,
    name        NAME,
    numeric     NUMERIC,
    uuid        UUID,
    date        DATE,
    time        TIME,
    timetz      TIMETZ,
    timestamp   TIMESTAMP,
    timestamptz TIMESTAMPTZ,
    interval    INTERVAL,
    bytea       BYTEA,
    json        JSON,
    jsonb       JSONB,
    point       POINT,
    line        LINE,
    lseg        LSEG,
    box         BOX,
    path        PATH,
    polygon     POLYGON,
    circle      CIRCLE
);
