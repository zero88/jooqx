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
    city   TEXT,
    street TEXT,
    home   BOOLEAN
);

CREATE TABLE numeric_data_type
(
    "id"          INTEGER NOT NULL PRIMARY KEY,
    "Short"       INT2,
    "Integer"     INT4,
    "Long"        INT8,
    "Float"       FLOAT4,
    "Double"      FLOAT8,
    "BigDecimal"  NUMERIC,
    "Boolean"     BOOLEAN,
    "SmallSerial" SERIAL2,
    "Serial"      SERIAL4,
    "BigSerial"   SERIAL8
);

CREATE TABLE temporal_data_type
(
    "id"          INTEGER NOT NULL PRIMARY KEY,
    "Date"        date,
    "Time"        time without time zone,
    "TimeTz"      time with time zone,
    "Timestamp"   timestamp without time zone,
    "TimestampTz" timestamp with time zone,
    "Interval"    interval
);

CREATE TABLE character_data_type
(
    "id"           INTEGER NOT NULL PRIMARY KEY,
    "Name"         NAME,
    "SingleChar"   CHAR,
    "FixedChar"    CHAR(3),
    "Text"         TEXT,
    "VarCharacter" VARCHAR,
    "uuid"         UUID,
    "bytea"        bytea
);

CREATE TABLE json_data_type
(
    "id"           INTEGER NOT NULL PRIMARY KEY,
    "JsonObject"   JSON,
    "JsonArray"    JSON,
    "Number"       JSON,
    "String"       JSON,
    "BooleanTrue"  JSON,
    "BooleanFalse" JSON,
    "NullValue"    JSON,
    "Null"         JSON
);

CREATE TABLE jsonb_data_type
(
    "id"           INTEGER NOT NULL PRIMARY KEY,
    "JsonObject"   JSONB,
    "JsonArray"    JSONB,
    "Number"       JSONB,
    "String"       JSONB,
    "BooleanTrue"  JSONB,
    "BooleanFalse" JSONB,
    "NullValue"    JSONB,
    "Null"         JSONB
);

CREATE TABLE geometric_data_type
(
    "id"         INTEGER NOT NULL PRIMARY KEY,
    "Point"      POINT,
    "Line"       LINE,
    "Lseg"       LSEG,
    "Box"        BOX,
    "ClosedPath" PATH,
    "OpenPath"   PATH,
    "Polygon"    POLYGON,
    "Circle"     CIRCLE
);

CREATE TABLE enum_data_type
(
    "id"             INTEGER NOT NULL PRIMARY KEY,
    "currentMood"    mood,
    "currentWeather" weather
);

CREATE TABLE udt_data_type
(
    "id"  INTEGER NOT NULL PRIMARY KEY,
    "address" full_address
);

CREATE TABLE all_data_types
(
    id            SERIAL PRIMARY KEY,
    f_boolean     BOOLEAN,
    --number
    f_int2        INT2,
    f_int4        INT4,
    f_int8        INT8,
    f_float4      FLOAT4,
    f_float8      FLOAT8,
    f_decimal     DECIMAL(10, 10),
    f_numeric     NUMERIC(10, 5),
    -- char
    f_char        CHAR,
    f_varchar     VARCHAR,
    f_text        TEXT,
    -- misc
    f_name        NAME,
    f_uuid        UUID,
    f_bytea       BYTEA,
    -- date/time
    f_date        DATE,
    f_time        TIME,
    f_timetz      TIMETZ,
    f_timestamp   TIMESTAMP,
    f_timestamptz TIMESTAMPTZ,
    f_interval    INTERVAL,
    -- enumerated
    f_enum        mood,
    -- json
    f_json        JSON,
    f_jsonb       JSONB,
    --Geometric Types
    f_point       POINT,
    f_line        LINE,
    f_lseg        LSEG,
    f_box         BOX,
    f_path        PATH,
    f_polygon     POLYGON,
    f_circle      CIRCLE
);
