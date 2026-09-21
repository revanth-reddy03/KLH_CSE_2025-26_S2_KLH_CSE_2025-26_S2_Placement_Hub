package algorithms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CorpusLoader {

    public static class StudentRecord {
        private final String studentId;
        private final String name;
        private final String email;
        private final String phone;
        private final String department;
        private final double cgpa;
        private final int backlogs;
        private final int aptitudeScore;
        private final int codingScore;
        private final List<String> skills;
        private final String fullText;

        public StudentRecord(String studentId, String name, String email, String phone,
                             String department, double cgpa, int backlogs,
                             int aptitudeScore, int codingScore,
                             List<String> skills, String fullText) {
            this.studentId = studentId;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.department = department;
            this.cgpa = cgpa;
            this.backlogs = backlogs;
            this.aptitudeScore = aptitudeScore;
            this.codingScore = codingScore;
            this.skills = skills;
            this.fullText = fullText;
        }

        public String getStudentId() { return studentId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getDepartment() { return department; }
        public double getCgpa() { return cgpa; }
        public int getBacklogs() { return backlogs; }
        public int getAptitudeScore() { return aptitudeScore; }
        public int getCodingScore() { return codingScore; }
        public List<String> getSkills() { return skills; }
        public String getFullText() { return fullText; }

        @Override
        public String toString() {
            return String.format("[%s] %-18s | %-10s | CGPA: %.2f | Backlogs: %d | Skills: %d",
                    studentId, name, department, cgpa, backlogs, skills.size());
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

        // Sort files by name for consistent order
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
        String name = "";
        String email = "";
        String phone = "";
        String department = "";
        double cgpa = 0.0;
        int backlogs = 0;
        int aptitudeScore = 0;
        int codingScore = 0;
        List<String> skills = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean readingSkills = false;

            while ((line = br.readLine()) != null) {
                fullTextBuilder.append(line).append("\n");
                String trimmed = line.trim();

                if (trimmed.startsWith("Candidate Name")) {
                    name = extractValue(trimmed);
                } else if (trimmed.startsWith("Student ID")) {
                    studentId = extractValue(trimmed);
                } else if (trimmed.startsWith("Email")) {
                    email = extractValue(trimmed);
                } else if (trimmed.startsWith("Phone")) {
                    phone = extractValue(trimmed);
                } else if (trimmed.startsWith("Department")) {
                    department = extractValue(trimmed);
                } else if (trimmed.startsWith("CGPA")) {
                    String val = extractValue(trimmed).split("/")[0].trim();
                    try { cgpa = Double.parseDouble(val); } catch (Exception ignored) {}
                } else if (trimmed.startsWith("Active Backlogs")) {
                    String val = extractValue(trimmed);
                    try { backlogs = Integer.parseInt(val); } catch (Exception ignored) {}
                } else if (trimmed.startsWith("Aptitude Score")) {
                    String val = extractValue(trimmed).split("/")[0].trim();
                    try { aptitudeScore = Integer.parseInt(val); } catch (Exception ignored) {}
                } else if (trimmed.startsWith("Coding Score")) {
                    String val = extractValue(trimmed).split("/")[0].trim();
                    try { codingScore = Integer.parseInt(val); } catch (Exception ignored) {}
                } else if (trimmed.equals("TECHNICAL SKILLS:")) {
                    readingSkills = true;
                } else if (readingSkills && !trimmed.isEmpty()) {
                    String[] skillArr = trimmed.split(",");
                    for (String s : skillArr) {
                        if (!s.trim().isEmpty()) {
                            skills.add(s.trim());
                        }
                    }
                    readingSkills = false; // Next lines are projects
                }
            }

            if (studentId.isEmpty()) {
                studentId = file.getName().replace(".txt", "");
            }

            return new StudentRecord(studentId, name, email, phone, department, 
                                     cgpa, backlogs, aptitudeScore, codingScore, 
                                     skills, fullTextBuilder.toString());

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
}
