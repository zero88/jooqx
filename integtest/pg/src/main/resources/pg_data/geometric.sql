--- GeometricDataType
INSERT INTO all_data_types ("id", f_point, f_line, f_lseg, f_box, f_closed_path, f_opened_path, f_polygon, f_circle)
VALUES (71, '(1.0,2.0)':: POINT, '{1.0,2.0,3.0}':: LINE, '((1.0,1.0),(2.0,2.0))':: LSEG, '((2.0,2.0),(1.0,1.0))':: BOX,
        '((1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0))':: PATH, '[(1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0)]':: PATH,
        '((1.0,1.0),(2.0,2.0),(3.0,1.0))':: POLYGON, '<(1.0,1.0),1.0>':: CIRCLE);
INSERT INTO all_data_types ("id", f_point, f_line, f_lseg, f_box, f_closed_path, f_opened_path, f_polygon, f_circle)
VALUES (72, '(1.0,2.0)':: POINT, '{1.0,2.0,3.0}':: LINE, '((1.0,1.0),(2.0,2.0))':: LSEG, '((2.0,2.0),(1.0,1.0))':: BOX,
        '((1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0))':: PATH, '[(1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0)]':: PATH,
        '((1.0,1.0),(2.0,2.0),(3.0,1.0))':: POLYGON, '<(1.0,1.0),1.0>':: CIRCLE);
