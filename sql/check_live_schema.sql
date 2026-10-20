-- READ-ONLY diagnostic: run this in MySQL Workbench to compare the live DB
-- with sql/schema.sql. It does not alter or delete any data.
USE placement_db;

SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_KEY
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'placement_db'
  AND TABLE_NAME IN ('students', 'companies', 'jobs', 'applications', 'placements')
ORDER BY TABLE_NAME, ORDINAL_POSITION;

SELECT 'students' AS table_name, COUNT(*) AS row_count FROM students
UNION ALL SELECT 'companies', COUNT(*) FROM companies
UNION ALL SELECT 'jobs', COUNT(*) FROM jobs
UNION ALL SELECT 'applications', COUNT(*) FROM applications
UNION ALL SELECT 'placements', COUNT(*) FROM placements;
