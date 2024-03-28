--- UDT
INSERT INTO all_data_types ("id", "f_udt_address") VALUES (41, ('US Open', 'Any,town', null, 10, true));
INSERT INTO all_data_types ("id", "f_udt_address") VALUES (42, ('Any""town', 'special","town', '',10, false));

--- EnumDataType
INSERT INTO all_data_types ("id", "f_udt_mood", "f_udt_weather")
    VALUES (43, 'ok', 'sunny');
INSERT INTO all_data_types ("id", "f_udt_mood", "f_udt_weather")
    VALUES (44, 'unhappy', 'cloudy');
INSERT INTO all_data_types ("id", "f_udt_mood", "f_udt_weather")
    VALUES (45, 'happy', 'rainy');
INSERT INTO all_data_types ("id", "f_udt_mood", "f_udt_weather")
    VALUES (46, null, null);
INSERT INTO all_data_types ("id", "f_udt_mood", "f_udt_weather")
    VALUES (47, 'ok', 'sunny');
