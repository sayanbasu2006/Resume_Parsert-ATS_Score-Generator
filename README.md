# Resume Parser & ATS Score Generator

This project is a Java Swing application that parses resumes and calculates ATS (Applicant Tracking System) scores based on job descriptions.

## Features
- Upload and parse resumes using Apache Tika
- Enter job descriptions and calculate ATS score
- Simple keyword matching for ATS scoring
- Extensible for fuzzy matching and database integration

## Dependencies
- Java 17
- Apache Tika
- Apache Commons Text
- MySQL Connector/J

## Setup
1. Ensure Java 17 and Maven are installed.
2. Clone this repository.
3. Install dependencies:
	```sh
	mvn clean install
	```
4. Run the application:
	```sh
	mvn exec:java
	```

## Usage
1. Click "Upload Resume" to select and parse a resume file.
2. Enter the job description in the provided area.
3. Click "Calculate ATS Score" to view the match percentage.

## Extending
- Add fuzzy matching using Apache Commons Text for improved scoring.
- Integrate MySQL to store parsed resumes and scores.

Made By Sayan, Rashal and Akshat.
