package algorithms;

import java.util.Arrays;

/**
 * Maximum Bipartite Matching algorithm using Augmenting Paths (Kuhn's Algorithm).
 * Solves optimal candidate-to-company / student-to-interview-slot assignments.
 * Time Complexity: O(V * E) where V = Students + Companies.
 */
public class BipartiteMatching {

    private final int numStudents;
    private final int numCompanies;
    private final boolean[][] eligibilityMatrix;
    private final int[] matchCompanyToStudent; // which student is assigned to company j

    public BipartiteMatching(int numStudents, int numCompanies, boolean[][] eligibilityMatrix) {
        this.numStudents = numStudents;
        this.numCompanies = numCompanies;
        this.eligibilityMatrix = eligibilityMatrix;
        this.matchCompanyToStudent = new int[numCompanies];
        Arrays.fill(this.matchCompanyToStudent, -1);
    }

    private boolean dfs(int student, boolean[] visited) {
        for (int company = 0; company < numCompanies; company++) {
            // Check if student is eligible for company and company not yet considered in this dfs path
            if (eligibilityMatrix[student][company] && !visited[company]) {
                visited[company] = true;

                // If company is not assigned OR previously assigned student can find alternate company
                if (matchCompanyToStudent[company] < 0 || dfs(matchCompanyToStudent[company], visited)) {
                    matchCompanyToStudent[company] = student;
                    return true;
                }
            }
        }
        return false;
    }

    public int computeMaxMatching() {
        Arrays.fill(matchCompanyToStudent, -1);
        int matchingCount = 0;

        for (int s = 0; s < numStudents; s++) {
            boolean[] visited = new boolean[numCompanies];
            if (dfs(s, visited)) {
                matchingCount++;
            }
        }
        return matchingCount;
    }

    public int[] getCompanyMatches() {
        return matchCompanyToStudent.clone();
    }

    public int[] getStudentMatches() {
        int[] studentToCompany = new int[numStudents];
        Arrays.fill(studentToCompany, -1);
        for (int c = 0; c < numCompanies; c++) {
            if (matchCompanyToStudent[c] != -1) {
                studentToCompany[matchCompanyToStudent[c]] = c;
            }
        }
        return studentToCompany;
    }
}
