-- MySQL setup for Resume ATS Score Generator

-- 1. Create the database
CREATE DATABASE resume_ats;
USE resume_ats;

-- 2. Create the resumes table
CREATE TABLE resumes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255),
    resume_text TEXT,
    job_description TEXT,
    ats_score INT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. (Optional) View all resumes
SELECT * FROM resumes;
