package algorithms;

import java.util.*;

/**
 * Edit Distance (Levenshtein Distance) algorithm using Dynamic Programming.
 * Time Complexity: O(M * N), Space Complexity: O(M * N) or O(min(M, N)).
 * Used for typo tolerance, fuzzy search on skills, student names, and role titles.
 */
public class EditDistance {

    public static int computeDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (Character.toLowerCase(s1.charAt(i - 1)) == Character.toLowerCase(s2.charAt(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j - 1], // substitution
                            Math.min(dp[i - 1][j], // deletion
                                     dp[i][j - 1]) // insertion
                    );
                }
            }
        }
        return dp[m][n];
    }

    public static double similarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        int dist = computeDistance(s1, s2);
        return 1.0 - ((double) dist / maxLen);
    }

    public static List<String> findClosestMatches(String query, Collection<String> candidates, int maxThreshold) {
        List<String> results = new ArrayList<>();
        if (query == null || candidates == null) return results;

        // Pair candidate with distance
        List<Map.Entry<String, Integer>> matches = new ArrayList<>();
        for (String c : candidates) {
            int d = computeDistance(query, c);
            if (d <= maxThreshold) {
                matches.add(new AbstractMap.SimpleEntry<>(c, d));
            }
        }

        matches.sort(Comparator.comparingInt(Map.Entry::getValue));
        for (Map.Entry<String, Integer> e : matches) {
            results.add(e.getKey());
        }
        return results;
    }
}
