package algorithms;

import java.util.List;

public class KMPTest {
    public static void main(String[] args) {
        testBasicMatch();
        testNoMatch();
        testOverlappingPatterns();
        testCaseSensitivity();
        System.out.println("✅ [PASS] All KMP tests passed successfully!");
    }

    private static void testBasicMatch() {
        String text = "Java and Python are great. I love Java.";
        String pattern = "Java";
        List<Integer> matches = KMP.search(text, pattern, true);
        assert matches.size() == 2 : "Expected 2 matches, got " + matches.size();
        assert matches.get(0) == 0 : "Expected first match at index 0";
        assert matches.get(1) == 34 : "Expected second match at index 34";
    }

    private static void testNoMatch() {
        String text = "React and Node.js developer";
        String pattern = "Golang";
        List<Integer> matches = KMP.search(text, pattern);
        assert matches.isEmpty() : "Expected 0 matches, got " + matches.size();
    }

    private static void testOverlappingPatterns() {
        String text = "AAAAAB";
        String pattern = "AAA";
        List<Integer> matches = KMP.search(text, pattern, true);
        assert matches.size() == 3 : "Expected 3 matches, got " + matches.size();
    }

    private static void testCaseSensitivity() {
        String text = "Machine Learning and deep learning";
        String pattern = "learning";
        List<Integer> matchesInsensitive = KMP.search(text, pattern, false);
        List<Integer> matchesSensitive = KMP.search(text, pattern, true);
        assert matchesInsensitive.size() == 2 : "Case insensitive should find 2";
        assert matchesSensitive.size() == 1 : "Case sensitive should find 1";
    }
}
