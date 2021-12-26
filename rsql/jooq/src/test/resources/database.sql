CREATE TABLE IF NOT EXISTS all_data_type (
    id INT AUTO_INCREMENT,
    f_bool bit,
    f_int int,
    f_real REAL,
    f_double DOUBLE,
    f_decimal DECIMAL(20, 2),
    f_str varchar(31),
    f_value_json text,
    f_date TIMESTAMP,
    f_timestamp timestamp,
    f_timestampz timestamp,
    f_date_1 date,
    f_time time,
    f_duration varchar(20),
    f_period varchar(20),
    CONSTRAINT all_data_type PRIMARY KEY ( id )
);