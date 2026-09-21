package algorithms;

import java.util.List;

public class ZAlgorithmTest {
    public static void main(String[] args) {
        testBasicMatch();
        testNoMatch();
        testMultipleMatches();
        System.out.println("✅ [PASS] All Z-Algorithm tests passed successfully!");
    }

    private static void testBasicMatch() {
        String text = "Campus recruitment for Software Engineering roles";
        String pattern = "Software";
        List<Integer> matches = ZAlgorithm.search(text, pattern, true);
        assert matches.size() == 1 : "Expected 1 match, got " + matches.size();
        assert matches.get(0) == 23 : "Expected match at index 23, got " + matches.get(0);
    }

    private static void testNoMatch() {
        String text = "Database Management System";
        String pattern = "Kubernetes";
        List<Integer> matches = ZAlgorithm.search(text, pattern);
        assert matches.isEmpty() : "Expected 0 matches, got " + matches.size();
    }

    private static void testMultipleMatches() {
        String text = "banana";
        String pattern = "an";
        List<Integer> matches = ZAlgorithm.search(text, pattern, true);
        assert matches.size() == 2 : "Expected 2 matches, got " + matches.size();
        assert matches.get(0) == 1 && matches.get(1) == 3 : "Expected matches at 1 and 3";
    }
}
