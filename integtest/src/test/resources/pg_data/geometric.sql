--- GeometricDataType
INSERT INTO geometric_data_type ("id", "Point", "Line", "Lseg", "Box", "ClosedPath", "OpenPath", "Polygon", "Circle")
VALUES (1, '(1.0,2.0)':: POINT, '{1.0,2.0,3.0}':: LINE, '((1.0,1.0),(2.0,2.0))':: LSEG, '((2.0,2.0),(1.0,1.0))':: BOX,
        '((1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0))':: PATH, '[(1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0)]':: PATH,
        '((1.0,1.0),(2.0,2.0),(3.0,1.0))':: POLYGON, '<(1.0,1.0),1.0>':: CIRCLE);
INSERT INTO geometric_data_type ("id", "Point", "Line", "Lseg", "Box", "ClosedPath", "OpenPath", "Polygon", "Circle")
VALUES (2, '(1.0,2.0)':: POINT, '{1.0,2.0,3.0}':: LINE, '((1.0,1.0),(2.0,2.0))':: LSEG, '((2.0,2.0),(1.0,1.0))':: BOX,
        '((1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0))':: PATH, '[(1.0,1.0),(2.0,1.0),(2.0,2.0),(2.0,1.0)]':: PATH,
        '((1.0,1.0),(2.0,2.0),(3.0,1.0))':: POLYGON, '<(1.0,1.0),1.0>':: CIRCLE);
