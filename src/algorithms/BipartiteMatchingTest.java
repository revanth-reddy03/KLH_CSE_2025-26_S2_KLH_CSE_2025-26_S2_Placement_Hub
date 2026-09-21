package algorithms;

public class BipartiteMatchingTest {
    public static void main(String[] args) {
        testMatching();
        System.out.println("✅ [PASS] All Bipartite Matching tests passed successfully!");
    }

    private static void testMatching() {
        // 4 students, 4 companies/roles
        // Student 0 eligible for Company 0, 1
        // Student 1 eligible for Company 1, 2
        // Student 2 eligible for Company 0, 2
        // Student 3 eligible for Company 2, 3
        boolean[][] graph = {
            {true, true, false, false},
            {false, true, true, false},
            {true, false, true, false},
            {false, false, true, true}
        };

        BipartiteMatching bm = new BipartiteMatching(4, 4, graph);
        int maxMatch = bm.computeMaxMatching();
        assert maxMatch == 4 : "Expected maximum matching 4, got " + maxMatch;

        int[] assignments = bm.getStudentMatches();
        for (int s = 0; s < 4; s++) {
            assert assignments[s] != -1 : "Student " + s + " should be matched";
        }
    }
}
