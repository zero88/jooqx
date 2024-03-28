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
    id        SERIAL PRIMARY KEY,
    author_id INT NOT NULL,
    book_id   INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors (id),
    FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TYPE mood AS ENUM ('sad', 'ok', 'happy', 'unhappy');
CREATE TYPE weather AS ENUM ('sunny', 'cloudy', 'rainy');
CREATE TYPE full_address AS
(
    state    VARCHAR(100),
    city     TEXT,
    street   TEXT,
    noA      INT4,
    home     BOOLEAN
);

CREATE TABLE all_data_types
(
    id                      INTEGER NOT NULL PRIMARY KEY,
    -- boolean & number | id: 1-20
    f_boolean               BOOLEAN,
    f_num_short             INT2,
    f_num_int               INT4,
    f_num_long              INT8,
    f_num_float4            FLOAT4,
    f_num_double            FLOAT8,
    f_num_decimal           DECIMAL(10, 10),
    f_num_numeric           NUMERIC(10, 5),
    f_num_small_serial      SERIAL2,
    f_num_serial            SERIAL4,
    f_num_big_serial        SERIAL8,

    -- string | id: 21-30
    f_str_char              CHAR,
    f_str_fixed_char        CHAR(3),
    f_str_varchar           VARCHAR,
    f_str_text              TEXT,
    f_misc_name             NAME,
    f_misc_uuid             UUID,
    f_misc_bytea            BYTEA,

    -- date/time | id: 31-40
    f_date                  DATE,
    f_time                  TIME,
    f_timetz                TIMETZ,
    f_timestamp             TIMESTAMP,
    f_timestamptz           TIMESTAMPTZ,
    f_interval              INTERVAL,
    f_duration              interval,

    -- udt | id: 41-50
    f_udt_mood              mood,
    f_udt_weather           weather,
    f_udt_address           full_address,

    -- json | id: 51-60
    f_json_object           JSON,
    f_json_array            JSON,
    f_json_number           JSON,
    f_json_string           JSON,
    f_json_boolean_true     JSON,
    f_json_boolean_false    JSON,
    f_json_null_value        JSON,
    f_json_null             JSON,

    -- jsonb | id: 61-70
    f_jsonb_object          JSONB,
    f_jsonb_array           JSONB,
    f_jsonb_number          JSONB,
    f_jsonb_string          JSONB,
    f_jsonb_boolean_true    JSONB,
    f_jsonb_boolean_false   JSONB,
    f_jsonb_null_value       JSONB,
    f_jsonb_null            JSONB,

    -- geometric | id: 71-80
    f_point                 POINT,
    f_line                  LINE,
    f_lseg                  LSEG,
    f_box                   BOX,
    f_closed_path           PATH,
    f_opened_path           PATH,
    f_polygon               POLYGON,
    f_circle                CIRCLE,

    -- array | id: 81 - 90
    f_one_dimension  integer[],
    f_two_dimension  text[][]
);

-- For Vertx data type
CREATE TABLE vertx_all_data_types
(
    id                      INTEGER NOT NULL PRIMARY KEY,
    -- boolean & number | id: 1-20
    f_boolean               BOOLEAN,
    f_num_short             INT2,
    f_num_int               INT4,
    f_num_long              INT8,
    f_num_float4            FLOAT4,
    f_num_double            FLOAT8,
    f_num_decimal           DECIMAL(10, 10),
    f_num_numeric           NUMERIC(10, 5),
    f_num_small_serial      SERIAL2,
    f_num_serial            SERIAL4,
    f_num_big_serial        SERIAL8,

    -- string | id: 21-30
    f_str_char              CHAR,
    f_str_fixed_char        CHAR(3),
    f_str_varchar           VARCHAR,
    f_str_text              TEXT,
    f_misc_name             NAME,
    f_misc_uuid             UUID,
    f_misc_bytea            BYTEA,

    -- date/time | id: 31-40
    f_date                  DATE,
    f_time                  TIME,
    f_timetz                TIMETZ,
    f_timestamp             TIMESTAMP,
    f_timestamptz           TIMESTAMPTZ,
    f_interval              INTERVAL,
    f_duration              interval,

    -- udt | id: 41-50
    f_udt_mood              mood,
    f_udt_weather           weather,
    f_udt_address           full_address,

    -- json | id: 51-60
    f_json_object           JSON,
    f_json_array            JSON,
    f_json_number           JSON,
    f_json_string           JSON,
    f_json_boolean_true     JSON,
    f_json_boolean_false    JSON,
    f_json_null_value        JSON,
    f_json_null             JSON,

    -- jsonb | id: 61-70
    f_jsonb_object          JSONB,
    f_jsonb_array           JSONB,
    f_jsonb_number          JSONB,
    f_jsonb_string          JSONB,
    f_jsonb_boolean_true    JSONB,
    f_jsonb_boolean_false   JSONB,
    f_jsonb_null_value       JSONB,
    f_jsonb_null            JSONB,

    -- geometric | id: 71-80
    f_point                 POINT,
    f_line                  LINE,
    f_lseg                  LSEG,
    f_box                   BOX,
    f_closed_path           PATH,
    f_opened_path           PATH,
    f_polygon               POLYGON,
    f_circle                CIRCLE,

    -- array | id: 81 - 90
    f_one_dimension  integer[],
    f_two_dimension  text[][]
);

COMMENT ON TABLE vertx_all_data_types IS 'Vertx data type';


-- https://www.postgresql.org/docs/10/sql-createfunction.html
CREATE FUNCTION add(integer, integer) RETURNS integer
AS 'select $1 + $2;'
    LANGUAGE SQL
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;

--
CREATE OR REPLACE FUNCTION increment(i integer) RETURNS integer
AS '
    BEGIN
        RETURN i + 1;
    END;
' LANGUAGE plpgsql;

--
CREATE FUNCTION dup(in int, out f1 int, out f2 text)
AS $$ SELECT $1, CAST($1 AS text) || ' is text' $$
    LANGUAGE SQL;

--
CREATE TYPE dup_result AS (f1 int, f2 text);

CREATE FUNCTION dup2(int) RETURNS dup_result
AS $$ SELECT $1, CAST($1 AS text) || ' is text' $$
    LANGUAGE SQL;

--
CREATE OR REPLACE FUNCTION find_authors(like_name varchar) RETURNS SETOF authors
    AS '
        DECLARE r authors%rowtype;
        BEGIN
            FOR r IN
                SELECT * FROM authors WHERE authors.name like (''%'' || like_name || ''%'')
                    LOOP
                        -- can do some processing here
                        RETURN NEXT r; -- return current row of SELECT
                    END LOOP;
            RETURN;
        END;
    ' LANGUAGE plpgsql;

--
CREATE OR REPLACE FUNCTION remove_author(author_name varchar) RETURNS void
    AS '
        DECLARE aId authors.id%TYPE;
        DECLARE bId books_authors.book_id%TYPE;
        BEGIN
            SELECT authors.id FROM authors WHERE authors.name = author_name LIMIT 1 into aId;
            FOR bId IN
                DELETE FROM books_authors WHERE author_id = aId RETURNING books_authors.book_id
                LOOP
                    DELETE FROM books WHERE books.id = cast(bId as int);
                END LOOP;
            DELETE FROM authors WHERE authors.id = aId;
        END;
    ' LANGUAGE plpgsql;

-- Create PROCEDURE from pg11, that supported from jooq v3.16 https://github.com/jOOQ/jOOQ/issues/8431
-- CREATE OR REPLACE PROCEDURE SelectAllAuthors()
-- LANGUAGE SQL
-- AS '
--     SELECT * FROM authors
-- ';
--
-- CREATE PROCEDURE SelectBooks(_title varchar)
-- LANGUAGE SQL
-- AS $$ SELECT * FROM books WHERE books.title = _title $$;
