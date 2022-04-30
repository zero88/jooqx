--- Json DataType
INSERT INTO all_data_types ("id", "f_json_object", "f_json_array", "f_json_number", "f_json_string",
                            "f_json_boolean_true", "f_json_boolean_false",
                            "f_json_null_value", "f_json_null")
VALUES (51, '  {
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
INSERT INTO all_data_types ("id", "f_json_object", "f_json_array", "f_json_number", "f_json_string",
                            "f_json_boolean_true", "f_json_boolean_false",
                            "f_json_null_value", "f_json_null")
VALUES (52, '  {
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
INSERT INTO all_data_types ("id", "f_jsonb_object", "f_jsonb_array", "f_jsonb_number", "f_jsonb_string",
                            "f_jsonb_boolean_true",
                            "f_jsonb_boolean_false",
                            "f_jsonb_null_value", "f_jsonb_null")
VALUES (61, '  {"str":"blah", "int" : 1, "float" : 3.5, "object": {}, "array" : []   }', '[1,true,null,9.5,"Hi"]', '4',
        '"Hello World"', 'true', 'false', 'null', NULL);
INSERT INTO all_data_types ("id", "f_jsonb_object", "f_jsonb_array", "f_jsonb_number", "f_jsonb_string",
                            "f_jsonb_boolean_true",
                            "f_jsonb_boolean_false",
                            "f_jsonb_null_value", "f_jsonb_null")
VALUES (62, '  {"str":"blah", "int" : 1, "float" : 3.5, "object": {}, "array" : []   }', '[1,true,null,9.5,"Hi"]', '4',
        '"Hello World"', 'true', 'false', 'null', NULL);

--========================================================================--
--- Vertx Json DataType
INSERT INTO vertx_all_data_types ("id", "f_json_object", "f_json_array", "f_json_number", "f_json_string",
                                  "f_json_boolean_true", "f_json_boolean_false",
                                  "f_json_null_value", "f_json_null")
VALUES (51, '  {
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
INSERT INTO vertx_all_data_types ("id", "f_json_object", "f_json_array", "f_json_number", "f_json_string",
                                  "f_json_boolean_true", "f_json_boolean_false",
                                  "f_json_null_value", "f_json_null")
VALUES (52, '  {
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
INSERT INTO vertx_all_data_types ("id", "f_jsonb_object", "f_jsonb_array", "f_jsonb_number", "f_jsonb_string",
                                  "f_jsonb_boolean_true", "f_jsonb_boolean_false",
                                  "f_jsonb_null_value", "f_jsonb_null")
VALUES (61, '  {"str":"blah", "int" : 1, "float" : 3.5, "object": {}, "array" : []   }', '[1,true,null,9.5,"Hi"]', '4',
        '"Hello World"', 'true', 'false', 'null', NULL);
INSERT INTO vertx_all_data_types ("id", "f_jsonb_object", "f_jsonb_array", "f_jsonb_number", "f_jsonb_string",
                                  "f_jsonb_boolean_true", "f_jsonb_boolean_false",
                                  "f_jsonb_null_value", "f_jsonb_null")
VALUES (62, '  {"str":"blah", "int" : 1, "float" : 3.5, "object": {}, "array" : []   }', '[1,true,null,9.5,"Hi"]', '4',
        '"Hello World"', 'true', 'false', 'null', NULL);
