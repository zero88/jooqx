--- Json DataType
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

--- Jsonb DataType
INSERT INTO jsonb_data_type ("id", "JsonObject", "JsonArray", "Number", "String", "BooleanTrue", "BooleanFalse",
                             "NullValue", "Null")
VALUES (1, '  {"str":"blah", "int" : 1, "float" : 3.5, "object": {}, "array" : []   }', '[1,true,null,9.5,"Hi"]', '4',
        '"Hello World"', 'true', 'false', 'null', NULL);
INSERT INTO jsonb_data_type ("id", "JsonObject", "JsonArray", "Number", "String", "BooleanTrue", "BooleanFalse",
                             "NullValue", "Null")
VALUES (2, '  {"str":"blah", "int" : 1, "float" : 3.5, "object": {}, "array" : []   }', '[1,true,null,9.5,"Hi"]', '4',
        '"Hello World"', 'true', 'false', 'null', NULL);
