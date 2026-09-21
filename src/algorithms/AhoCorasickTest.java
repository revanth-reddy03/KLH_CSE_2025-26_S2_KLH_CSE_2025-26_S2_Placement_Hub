package algorithms;

import java.util.*;

public class AhoCorasickTest {
    public static void main(String[] args) {
        testMultiPatternMatch();
        testOverlappingKeywords();
        testFrequencyCount();
        System.out.println("✅ [PASS] All Aho-Corasick tests passed successfully!");
    }

    private static void testMultiPatternMatch() {
        AhoCorasick ac = new AhoCorasick(Arrays.asList("java", "python", "sql", "dsa"));
        String resumeSnippet = "Candidate knows Java, DSA algorithms, and SQL database querying.";

        List<AhoCorasick.MatchResult> matches = ac.search(resumeSnippet);
        assert matches.size() == 3 : "Expected 3 matches (java, dsa, sql), got " + matches.size();
    }

    private static void testOverlappingKeywords() {
        AhoCorasick ac = new AhoCorasick(Arrays.asList("he", "she", "his", "hers"));
        String text = "ushers";

        List<AhoCorasick.MatchResult> matches = ac.search(text);
        // "ushers" contains "she", "he", "hers"
        Set<String> found = new HashSet<>();
        for (AhoCorasick.MatchResult m : matches) {
            found.add(m.getKeyword());
        }

        assert found.contains("he") : "Should contain 'he'";
        assert found.contains("she") : "Should contain 'she'";
        assert found.contains("hers") : "Should contain 'hers'";
    }

    private static void testFrequencyCount() {
        AhoCorasick ac = new AhoCorasick(Arrays.asList("docker", "kubernetes"));
        String text = "Docker containerization with Docker compose and Kubernetes cluster orchestration.";

        Map<String, Integer> freqs = ac.getKeywordFrequencies(text);
        assert freqs.get("docker") == 2 : "Expected 2 dockers, got " + freqs.get("docker");
        assert freqs.get("kubernetes") == 1 : "Expected 1 kubernetes, got " + freqs.get("kubernetes");
    }
}
