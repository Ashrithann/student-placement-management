import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final Scanner SC = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  STUDENT PLACEMENT MANAGEMENT SYSTEM");
        System.out.println("========================================");

        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("Connected to placement_db successfully.");

            boolean running = true;
            while (running) {
                printMenu();
                int choice = readInt("Choose an option: ");

                try {
                    switch (choice) {
                        case 1 -> addStudent(connection);
                        case 2 -> listStudents(connection);
                        case 3 -> updateStudent(connection);
                        case 4 -> addCompany(connection);
                        case 5 -> listCompanies(connection);
                        case 6 -> addJob(connection);
                        case 7 -> listJobs(connection);
                        case 8 -> applyForJob(connection);
                        case 9 -> listApplications(connection);
                        case 10 -> updateApplicationStatus(connection);
                        case 11 -> placementReport(connection);
                        case 0 -> {
                            running = false;
                            System.out.println("Goodbye!");
                        }
                        default -> System.out.println("Please choose a listed option.");
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Could not save: a duplicate value or related record may exist.");
                    System.out.println("Details: " + e.getMessage());
                } catch (SQLException e) {
                    System.out.println("Database operation failed: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                }

                if (running) {
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            System.out.println("\nCould not connect to MySQL.");
            System.out.println("Check that MySQL is running, schema.sql was executed,");
            System.out.println("and DB_USER / DB_PASSWORD are correct.");
            System.out.println("Details: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n--------------- MAIN MENU ---------------");
        System.out.println("1.  Add student");
        System.out.println("2.  List students");
        System.out.println("3.  Update student");
        System.out.println("4.  Add company");
        System.out.println("5.  List companies");
        System.out.println("6.  Add job opening");
        System.out.println("7.  List job openings");
        System.out.println("8.  Apply student for a job");
        System.out.println("9.  List applications");
        System.out.println("10. Update application status");
        System.out.println("11. Placement report");
        System.out.println("0.  Exit");
        System.out.println("-----------------------------------------");
    }

    private static void addStudent(Connection con) throws SQLException {
        System.out.println("\n-- Add Student --");
        String name = readRequired("Full name: ");
        String email = readRequired("Email: ");
        String department = readRequired("Department: ");
        BigDecimal cgpa = readDecimal("CGPA (0-10): ");
        if (cgpa.compareTo(BigDecimal.ZERO) < 0 ||
                cgpa.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("CGPA must be between 0 and 10.");
        }
        int year = readInt("Graduation year (e.g. 2027): ");

        String sql = """
            INSERT INTO students(name, email, department, cgpa, graduation_year)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, department);
            ps.setBigDecimal(4, cgpa);
            ps.setInt(5, year);
            ps.executeUpdate();
            System.out.println("Student added.");
        }
    }

    private static void listStudents(Connection con) throws SQLException {
        String sql = """
            SELECT student_id, name, email, department, cgpa, graduation_year
            FROM students ORDER BY student_id
            """;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n-- Students --");
            System.out.printf("%-5s %-24s %-30s %-16s %-6s %-6s%n",
                    "ID", "Name", "Email", "Department", "CGPA", "Year");
            while (rs.next()) {
                System.out.printf("%-5d %-24s %-30s %-16s %-6s %-6d%n",
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getBigDecimal("cgpa"),
                        rs.getInt("graduation_year"));
            }
        }
    }

    private static void updateStudent(Connection con) throws SQLException {
        System.out.println("\n-- Update Student --");
        int id = readInt("Student ID: ");
        String name = readRequired("New full name: ");
        String email = readRequired("New email: ");
        String department = readRequired("New department: ");
        BigDecimal cgpa = readDecimal("New CGPA (0-10): ");
        if (cgpa.compareTo(BigDecimal.ZERO) < 0 ||
                cgpa.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("CGPA must be between 0 and 10.");
        }
        int year = readInt("New graduation year: ");

        String sql = """
            UPDATE students
            SET name=?, email=?, department=?, cgpa=?, graduation_year=?
            WHERE student_id=?
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, department);
            ps.setBigDecimal(4, cgpa);
            ps.setInt(5, year);
            ps.setInt(6, id);
            int rows = ps.executeUpdate();
            System.out.println(rows == 1 ? "Student updated." : "No student found with that ID.");
        }
    }

    private static void addCompany(Connection con) throws SQLException {
        System.out.println("\n-- Add Company --");
        String name = readRequired("Company name: ");
        String location = readRequired("Location: ");

        String sql = "INSERT INTO companies(company_name, location) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, location);
            ps.executeUpdate();
            System.out.println("Company added.");
        }
    }

    private static void listCompanies(Connection con) throws SQLException {
        String sql = "SELECT company_id, company_name, location FROM companies ORDER BY company_id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n-- Companies --");
            System.out.printf("%-5s %-30s %-25s%n", "ID", "Company", "Location");
            while (rs.next()) {
                System.out.printf("%-5d %-30s %-25s%n",
                        rs.getInt("company_id"),
                        rs.getString("company_name"),
                        rs.getString("location"));
            }
        }
    }

    private static void addJob(Connection con) throws SQLException {
        System.out.println("\n-- Add Job Opening --");
        listCompanies(con);
        int companyId = readInt("Company ID for this job: ");
        String jobTitle = readRequired("Job title: ");
        String jobRole = readRequired("Job role: ");
        BigDecimal minCgpa = readDecimal("Minimum CGPA (0-10): ");
        BigDecimal packageLpa = readDecimal("Package in LPA: ");

        if (minCgpa.compareTo(BigDecimal.ZERO) < 0 ||
                minCgpa.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("Minimum CGPA must be between 0 and 10.");
        }
        if (packageLpa.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Package cannot be negative.");
        }

        
        String sql = """
        INSERT INTO jobs(company_id, job_title, job_role, min_cgpa, package_lpa)
         VALUES (?, ?, ?, ?, ?)
    """;

try (PreparedStatement ps = con.prepareStatement(sql)) {
    ps.setInt(1, companyId);
    ps.setString(2, jobTitle);
    ps.setString(3, jobRole);
    ps.setBigDecimal(4, minCgpa);
    ps.setDouble(5, packageLpa.doubleValue());

    ps.executeUpdate();

    System.out.println("Job opening added.");
}

    }

    private static void listJobs(Connection con) throws SQLException {
        String sql = """
            SELECT j.job_id, c.company_name, j.job_role,
                   j.min_cgpa, j.package_lpa, c.location
            FROM jobs j JOIN companies c ON c.company_id = j.company_id
            ORDER BY j.job_id
            """;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n-- Job Openings --");
            System.out.printf("%-5s %-24s %-22s %-9s %-10s %-18s%n",
                    "ID", "Company", "Role", "Min CGPA", "LPA", "Location");
            while (rs.next()) {
                System.out.printf("%-5d %-24s %-22s %-9s %-10s %-18s%n",
                        rs.getInt("job_id"), rs.getString("company_name"),
                        rs.getString("job_role"), rs.getBigDecimal("min_cgpa"),
                        rs.getBigDecimal("package_lpa"), rs.getString("location"));
            }
        }
    }

    private static void applyForJob(Connection con) throws SQLException {
        System.out.println("\n-- Apply for a Job --");
        listStudents(con);
        int studentId = readInt("Student ID: ");
        listJobs(con);
        int jobId = readInt("Job ID: ");

        String eligibilitySql = """
            SELECT s.name, s.cgpa, j.job_role, j.min_cgpa, c.company_name
            FROM students s
            JOIN jobs j ON j.job_id = ?
            JOIN companies c ON c.company_id = j.company_id
            WHERE s.student_id = ?
            """;
        try (PreparedStatement ps = con.prepareStatement(eligibilitySql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Student ID or Job ID was not found.");
                    return;
                }
                BigDecimal cgpa = rs.getBigDecimal("cgpa");
                BigDecimal minimum = rs.getBigDecimal("min_cgpa");
                if (cgpa.compareTo(minimum) < 0) {
                    System.out.printf("Not eligible: student CGPA %s is below the required %s.%n",
                            cgpa, minimum);
                    return;
                }
                System.out.printf("Eligible for %s at %s.%n",
                        rs.getString("job_role"), rs.getString("company_name"));
            }
        }

        String insert = """
            INSERT INTO applications(student_id, job_id, application_date, status)
            VALUES (?, ?, ?, 'Applied')
            """;
        try (PreparedStatement ps = con.prepareStatement(insert)) {
            ps.setInt(1, studentId);
            ps.setInt(2, jobId);
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            System.out.println("Application submitted.");
        }
    }

    private static void listApplications(Connection con) throws SQLException {
        String sql = """
            SELECT a.application_id, s.name, c.company_name, j.job_role,
                   a.application_date, a.status
            FROM applications a
            JOIN students s ON s.student_id = a.student_id
            JOIN jobs j ON j.job_id = a.job_id
            JOIN companies c ON c.company_id = j.company_id
            ORDER BY a.application_id
            """;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n-- Applications --");
            System.out.printf("%-5s %-22s %-22s %-18s %-12s %-14s%n",
                    "ID", "Student", "Company", "Role", "Date", "Status");
            while (rs.next()) {
                System.out.printf("%-5d %-22s %-22s %-18s %-12s %-14s%n",
                        rs.getInt("application_id"), rs.getString("name"),
                        rs.getString("company_name"), rs.getString("job_role"),
                        rs.getDate("application_date"), rs.getString("status"));
            }
        }
    }

    private static void updateApplicationStatus(Connection con) throws SQLException {
        System.out.println("\n-- Update Application Status --");
        listApplications(con);
        int applicationId = readInt("Application ID: ");
        System.out.println("1. Applied  2. Shortlisted  3. Rejected  4. Selected");
        int statusChoice = readInt("New status: ");
        String status = switch (statusChoice) {
            case 1 -> "Applied";
            case 2 -> "Shortlisted";
            case 3 -> "Rejected";
            case 4 -> "Selected";
            default -> throw new IllegalArgumentException("Choose status 1 to 4.");
        };

        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            String update = "UPDATE applications SET status=? WHERE application_id=?";
            int rows;
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setString(1, status);
                ps.setInt(2, applicationId);
                rows = ps.executeUpdate();
            }
            if (rows == 0) {
                con.rollback();
                System.out.println("No application found with that ID.");
                return;
            }

            if ("Selected".equals(status)) {
                String offer = """
                    INSERT INTO placements(student_id, company_id, job_id, placement_date, package)
                    SELECT a.student_id, j.company_id, a.job_id, CURRENT_DATE, j.package_lpa
                    FROM applications a JOIN jobs j ON j.job_id = a.job_id
                    WHERE a.application_id = ?
                    ON DUPLICATE KEY UPDATE
                        company_id = VALUES(company_id),
                        job_id = VALUES(job_id),
                        placement_date = VALUES(placement_date),
                        package = VALUES(package)
                    """;
                try (PreparedStatement ps = con.prepareStatement(offer)) {
                    ps.setInt(1, applicationId);
                    ps.executeUpdate();
                }
            } else {
                String deletePlacement = """
                    DELETE FROM placements
                    WHERE student_id = (SELECT student_id FROM applications WHERE application_id = ?)
                    """;
                try (PreparedStatement ps = con.prepareStatement(deletePlacement)) {
                    ps.setInt(1, applicationId);
                    ps.executeUpdate();
                }
            }
            con.commit();
            System.out.println("Application status updated to " + status + ".");
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    private static void placementReport(Connection con) throws SQLException {
        System.out.println("\n-- Placement Report --");
        String sql = """
            SELECT s.student_id, s.name, s.department, c.company_name,
                   j.job_role, p.package, p.placement_date
            FROM placements p
            JOIN students s ON s.student_id = p.student_id
            JOIN jobs j ON j.job_id = p.job_id
            JOIN companies c ON c.company_id = p.company_id
            ORDER BY s.name
            """;
        int count = 0;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.printf("%-5s %-22s %-16s %-22s %-18s %-10s %-12s%n",
                    "ID", "Student", "Department", "Company", "Role", "LPA", "Offer date");
            while (rs.next()) {
                count++;
                System.out.printf("%-5d %-22s %-16s %-22s %-18s %-10s %-12s%n",
                        rs.getInt("student_id"), rs.getString("name"),
                        rs.getString("department"), rs.getString("company_name"),
                        rs.getString("job_role"), rs.getBigDecimal("package"),
                        rs.getDate("placement_date"));
            }
        }

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM students")) {
            rs.next();
            int totalStudents = rs.getInt("total");
            System.out.println("\nTotal students: " + totalStudents);
            System.out.println("Recorded placement offers: " + count);
            if (totalStudents > 0) {
                System.out.printf("Placement offer rate: %.2f%%%n",
                        (count * 100.0) / totalStudents);
                System.out.println("Note: offer rate counts offers, so one student with multiple offers may be counted more than once.");
            }
        }
    }

    private static String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SC.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SC.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    private static BigDecimal readDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SC.nextLine().trim();
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number, such as 8.25.");
            }
        }
    }
}
