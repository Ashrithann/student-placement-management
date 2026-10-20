CREATE DATABASE IF NOT EXISTS placement_db;
USE placement_db;

CREATE TABLE IF NOT EXISTS students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    cgpa DECIMAL(4,2) NOT NULL,
    graduation_year INT NOT NULL DEFAULT 2027,
    CONSTRAINT chk_student_cgpa CHECK (cgpa BETWEEN 0 AND 10)
);

CREATE TABLE IF NOT EXISTS companies (
    company_id INT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(150) NOT NULL,
    location VARCHAR(150) NULL
);

CREATE TABLE IF NOT EXISTS jobs (
    job_id INT PRIMARY KEY AUTO_INCREMENT,
    company_id INT NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    min_cgpa DECIMAL(4,2) NOT NULL,
    package_lpa DECIMAL(12,2) NULL,
    job_role VARCHAR(150) NOT NULL DEFAULT 'Not specified',
    FOREIGN KEY (company_id) REFERENCES companies(company_id),
    CONSTRAINT chk_job_min_cgpa CHECK (min_cgpa BETWEEN 0 AND 10),
    CONSTRAINT chk_job_package CHECK (package_lpa IS NULL OR package_lpa >= 0)
);

CREATE TABLE IF NOT EXISTS applications (
    application_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    job_id INT NOT NULL,
    application_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Applied',
    UNIQUE KEY unique_student_job (student_id, job_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (job_id) REFERENCES jobs(job_id)
);

CREATE TABLE IF NOT EXISTS placements (
    placement_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL UNIQUE,
    company_id INT NOT NULL,
    job_id INT NOT NULL,
    placement_date DATE NOT NULL,
    package DECIMAL(12,2) NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (company_id) REFERENCES companies(company_id),
    FOREIGN KEY (job_id) REFERENCES jobs(job_id)
);
