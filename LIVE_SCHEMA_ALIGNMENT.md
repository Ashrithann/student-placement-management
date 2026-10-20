# Live schema alignment notes

This project was checked against the live schema columns supplied from MySQL Workbench.

- The `jobs` insert/listing uses `job_title`, `job_role`, `min_cgpa`, and `package_lpa`.
- The `applications` features use `application_id`, `student_id`, `job_id`, `application_date`, and `status`.
- The live `placements` table stores `student_id`, `company_id`, `job_id`, `placement_date`, and `package` (not `application_id`, `offer_date`, or `offered_package`). `Main.java` has been updated so status changes and the placement report use those live columns.

Important: `CREATE TABLE IF NOT EXISTS` does not alter an existing database. Do not re-run schema creation expecting it to migrate existing tables. This updated schema is a reference for fresh installations; the current Java changes are designed to use the live table layout shown in the screenshots.

Compile check: run `mvn clean compile` from the project root. Then test menu options against your own MySQL connection. Compilation does not prove live runtime behavior, so verify each menu option using test records.
