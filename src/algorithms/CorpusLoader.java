package algorithms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CorpusLoader {

    public static class StudentRecord {
        private final String studentId;
        private final String rollNumber;
        private final String name;
        private final String department;
        private final String program;
        private final int year;
        private final String section;
        private final String academicStatus;
        private final String email;
        private final String location;
        private final List<String> skills;
        private final List<String> programmingLanguages;
        private final List<String> technicalInterests;
        private final List<String> academicInterests;
        private final List<String> researchInterests;
        private final String fullText;

        public StudentRecord(String studentId, String rollNumber, String name, 
                             String department, String program, int year, 
                             String section, String academicStatus, String email, 
                             String location, List<String> skills, 
                             List<String> programmingLanguages,
                             List<String> technicalInterests,
                             List<String> academicInterests,
                             List<String> researchInterests,
                             String fullText) {
            this.studentId = studentId;
            this.rollNumber = rollNumber;
            this.name = name;
            this.department = department;
            this.program = program;
            this.year = year;
            this.section = section;
            this.academicStatus = academicStatus;
            this.email = email;
            this.location = location;
            this.skills = skills;
            this.programmingLanguages = programmingLanguages;
            this.technicalInterests = technicalInterests;
            this.academicInterests = academicInterests;
            this.researchInterests = researchInterests;
            this.fullText = fullText;
        }

        public String getStudentId() { return studentId; }
        public String getRollNumber() { return rollNumber; }
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public String getProgram() { return program; }
        public int getYear() { return year; }
        public String getSection() { return section; }
        public String getAcademicStatus() { return academicStatus; }
        public String getEmail() { return email; }
        public String getLocation() { return location; }
        public List<String> getSkills() { return skills; }
        public List<String> getProgrammingLanguages() { return programmingLanguages; }
        public List<String> getTechnicalInterests() { return technicalInterests; }
        public List<String> getAcademicInterests() { return academicInterests; }
        public List<String> getResearchInterests() { return researchInterests; }
        public String getFullText() { return fullText; }

        @Override
        public String toString() {
            return String.format("[%s] %-18s | Roll: %-10s | %-25s | Year: %d | Skills: %d",
                    studentId, name, rollNumber, department, year, skills.size());
        }
    }

    public static List<StudentRecord> loadStudentRecords(String dirPath) {
        List<StudentRecord> records = new ArrayList<>();
        File folder = new File(dirPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Directory not found: " + dirPath);
            return records;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) return records;

        Arrays.sort(files, Comparator.comparing(File::getName));

        for (File file : files) {
            StudentRecord record = parseFile(file);
            if (record != null) {
                records.add(record);
            }
        }

        return records;
    }

    private static StudentRecord parseFile(File file) {
        StringBuilder fullTextBuilder = new StringBuilder();
        String studentId = "";
        String rollNumber = "";
        String name = "";
        String department = "";
        String program = "B.Tech";
        int year = 2;
        String section = "A";
        String academicStatus = "Active";
        String email = "";
        String location = "";
        List<String> skills = new ArrayList<>();
        List<String> programmingLanguages = new ArrayList<>();
        List<String> technicalInterests = new ArrayList<>();
        List<String> academicInterests = new ArrayList<>();
        List<String> researchInterests = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                fullTextBuilder.append(line).append("\n");
                String trimmed = line.trim();

                if (trimmed.startsWith("Student Record ID:")) {
                    studentId = extractValue(trimmed);
                } else if (trimmed.startsWith("Roll Number:")) {
                    rollNumber = extractValue(trimmed);
                } else if (trimmed.startsWith("Name:")) {
                    name = extractValue(trimmed);
                } else if (trimmed.startsWith("Department:")) {
                    department = extractValue(trimmed);
                } else if (trimmed.startsWith("Program:")) {
                    program = extractValue(trimmed);
                } else if (trimmed.startsWith("Year:")) {
                    try { year = Integer.parseInt(extractValue(trimmed)); } catch (Exception ignored) {}
                } else if (trimmed.startsWith("Section:")) {
                    section = extractValue(trimmed);
                } else if (trimmed.startsWith("Academic Status:")) {
                    academicStatus = extractValue(trimmed);
                } else if (trimmed.startsWith("Email:")) {
                    email = extractValue(trimmed);
                } else if (trimmed.startsWith("Location:")) {
                    location = extractValue(trimmed);
                } else if (trimmed.startsWith("Skills:")) {
                    skills.addAll(parseList(extractValue(trimmed)));
                } else if (trimmed.startsWith("Programming Languages:")) {
                    programmingLanguages.addAll(parseList(extractValue(trimmed)));
                } else if (trimmed.startsWith("Technical Interests:")) {
                    technicalInterests.addAll(parseList(extractValue(trimmed)));
                } else if (trimmed.startsWith("Academic Interests:")) {
                    academicInterests.addAll(parseList(extractValue(trimmed)));
                } else if (trimmed.startsWith("Research Interests:")) {
                    researchInterests.addAll(parseList(extractValue(trimmed)));
                }
            }

            if (studentId.isEmpty()) {
                studentId = file.getName().replace(".txt", "");
            }

            return new StudentRecord(studentId, rollNumber, name, department, program, 
                                     year, section, academicStatus, email, location, 
                                     skills, programmingLanguages, technicalInterests, 
                                     academicInterests, researchInterests, 
                                     fullTextBuilder.toString());

        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
            return null;
        }
    }

    private static String extractValue(String line) {
        int colonIdx = line.indexOf(':');
        if (colonIdx != -1 && colonIdx < line.length() - 1) {
            return line.substring(colonIdx + 1).trim();
        }
        return "";
    }

    private static List<String> parseList(String commaSeparated) {
        List<String> list = new ArrayList<>();
        if (commaSeparated == null || commaSeparated.isEmpty()) return list;
        String[] parts = commaSeparated.split(",");
        for (String p : parts) {
            if (!p.trim().isEmpty()) {
                list.add(p.trim());
            }
        }
        return list;
    }
}
