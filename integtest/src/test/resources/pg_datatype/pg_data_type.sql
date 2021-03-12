--- NumericDataType
INSERT INTO numeric_data_type ("id", "Short", "Integer", "Long", "Float", "Double", "BigDecimal", "Boolean")
VALUES (1, 32767, 2147483647, 9223372036854775807, 3.4028235E38, 1.7976931348623157E308,
        '9.99999999999999999999999999999999999', true);
INSERT INTO numeric_data_type ("id", "Short", "Integer", "Long", "Float", "Double", "BigDecimal", "Boolean")
VALUES (2, 32767, 2147483647, 9223372036854775807, 3.4028235E38, 1.7976931348623157E308,
        '9.99999999999999999999999999999999999', true);

--- CharacterDataType
INSERT INTO character_data_type ("id", "Name", "SingleChar", "FixedChar", "Text", "VarCharacter", "uuid")
VALUES (1, 'What is my name ?', 'A', 'YES', 'Hello World', 'Great!', '6f790482-b5bd-438b-a8b7-4a0bed747011');
INSERT INTO character_data_type ("id", "Name", "SingleChar", "FixedChar", "Text", "VarCharacter", "uuid")
VALUES (2, 'What is my name ?', 'A', 'YES', 'Hello World', 'Great!', '6f790482-b5bd-438b-a8b7-4a0bed747011');

--- JsonDataType
INSERT INTO json_data_type ("id", "JsonObject", "JsonArray", "Number", "String", "BooleanTrue", "BooleanFalse",
                            "NullValue", "Null")
VALUES (1, '  {
  "str": "blah",
  "int": 1,
  "float": 3.5,
  "object": {},
  "array": []
}', '[
  1,
  true,
  null,
  9.5,
  "Hi"
]', '4', '"Hello World"', 'true', 'false', 'null', NULL);
INSERT INTO json_data_type ("id", "JsonObject", "JsonArray", "Number", "String", "BooleanTrue", "BooleanFalse",
                            "NullValue", "Null")
VALUES (2, '  {
  "str": "blah",
  "int": 1,
  "float": 3.5,
  "object": {},
  "array": []
}', '[
  1,
  true,
  null,
  9.5,
  "Hi"
]', '4', '"Hello World"', 'true', 'false', 'null', NULL);

--- JsonDataType
INSERT INTO jsonb_data_type ("id", "JsonObject", "JsonArray", "Number", "String", "BooleanTrue", "BooleanFalse",
                             "NullValue", "Null")
VALUES (1, '  {"str":"blah", "int" : 1, "float" : 3.5, "object": {}, "array" : []   }', '[1,true,null,9.5,"Hi"]', '4',
        '"Hello World"', 'true', 'false', 'null', NULL);
INSERT INTO jsonb_data_type ("id", "JsonObject", "JsonArray", "Number", "String", "BooleanTrue", "BooleanFalse",
                             "NullValue", "Null")
VALUES (2, '  {"str":"blah", "int" : 1, "float" : 3.5, "object": {}, "array" : []   }', '[1,true,null,9.5,"Hi"]', '4',
        '"Hello World"', 'true', 'false', 'null', NULL);

--- GeometricDataType
INSERT INTO geometric_data_type ("id", "Point", "Line", "Lseg", "Box", "ClosedPath", "OpenPath", "Polygon", "Circle")
VALUES (1, '(1.0,2.0)':: POINT, '{1.0,2.0,3.0}':: LINE, '((1.0,1.0),(2.0,2.0))':: LSEG, '((2.0,2.0),(1.0,1.0))':: BOX,
        '((1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0))':: PATH, '[(1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0)]':: PATH,
        '((1.0,1.0),(2.0,2.0),(3.0,1.0))':: POLYGON, '<(1.0,1.0),1.0>':: CIRCLE);
INSERT INTO geometric_data_type ("id", "Point", "Line", "Lseg", "Box", "ClosedPath", "OpenPath", "Polygon", "Circle")
VALUES (2, '(1.0,2.0)':: POINT, '{1.0,2.0,3.0}':: LINE, '((1.0,1.0),(2.0,2.0))':: LSEG, '((2.0,2.0),(1.0,1.0))':: BOX,
        '((1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0))':: PATH, '[(1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0)]':: PATH,
        '((1.0,1.0),(2.0,2.0),(3.0,1.0))':: POLYGON, '<(1.0,1.0),1.0>':: CIRCLE);

--- EnumDataType
INSERT INTO enum_data_type ("id", "currentMood", "currentWeather") VALUES (1, 'ok', 'sunny');
INSERT INTO enum_data_type ("id", "currentMood", "currentWeather") VALUES (2, 'unhappy', 'cloudy');
INSERT INTO enum_data_type ("id", "currentMood", "currentWeather") VALUES (3, 'happy', 'rainy');
INSERT INTO enum_data_type ("id", "currentMood", "currentWeather") VALUES (4, null, null);
INSERT INTO enum_data_type ("id", "currentMood", "currentWeather") VALUES (5, 'ok', 'sunny');

--- CustomDataType
INSERT INTO udt_data_type ("id", "address") VALUES (1, ('Anytown', 'Main St', true));
INSERT INTO udt_data_type ("id", "address") VALUES (2, ('Anytown', 'First St', false));
