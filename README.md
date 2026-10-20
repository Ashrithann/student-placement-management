# Student Placement Management System

A console-based Java + JDBC + MySQL project for a college placement cell.

## Features
- Add, list, and update student records
- Register and list companies
- Create and list job openings
- Check CGPA eligibility and submit applications
- List applications and update their status
- Automatically create/update a placement offer when an application is marked Selected
- View a placement report

## Requirements
- JDK 17+
- MySQL Server 8+
- VS Code and Extension Pack for Java
- Maven (recommended)

## 1. Create the database
Open MySQL Workbench or the MySQL command-line client and execute the complete file:
`sql/schema.sql`

This creates `placement_db` and its five tables.

## 2. Set database credentials
The app reads these environment variables:
- `DB_URL` (optional; default `jdbc:mysql://localhost:3306/placement_db`)
- `DB_USER` (optional; default `root`)
- `DB_PASSWORD` (recommended; your local MySQL password)

Windows PowerShell, for the current terminal only:
```powershell
$env:DB_USER = "root"
$env:DB_PASSWORD = "your_local_mysql_password"
```

macOS/Linux, for the current terminal only:
```bash
export DB_USER=root
export DB_PASSWORD='your_local_mysql_password'
```

If your MySQL root account has no password, `DB_PASSWORD` can be left unset. Do not put real passwords in source code or commit them to Git.

## 3. Open and run in VS Code
1. Open the `student-placement-management` folder in VS Code.
2. Install the Extension Pack for Java if it is not installed.
3. Ensure Maven is installed (`mvn -version`).
4. In the terminal, from the project root, run:
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass=Main
   ```

The Maven Exec Plugin is configured in `pom.xml`, so `mvn exec:java` should work directly.

## Suggested demo sequence
1. Add a student with CGPA 8.50.
2. Add a company.
3. Add a job with minimum CGPA 7.00 and package 6.50 LPA.
4. Apply the student to that job.
5. List applications.
6. Update the application to Selected.
7. Open the placement report.

## Database design
- `students`: student information
- `companies`: recruiter information
- `jobs`: roles offered by companies
- `applications`: student-job applications and statuses
- `placements`: selected application offers

The application uses `PreparedStatement` for user-supplied values, foreign keys to maintain relationships, and a transaction when updating application status and placement offers.


## Checking an existing database

`CREATE TABLE IF NOT EXISTS` does not update tables that already exist. If this project was run against an older `placement_db`, use MySQL Workbench to execute `sql/check_live_schema.sql` first. It is read-only and lists the actual columns and row counts. Compare those results with `sql/schema.sql` before applying any ALTER TABLE statements; do not rerun the schema file expecting it to migrate existing tables.
