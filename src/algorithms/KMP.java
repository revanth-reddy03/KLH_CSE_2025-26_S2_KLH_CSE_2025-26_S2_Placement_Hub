package algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) String Searching Algorithm.
 * Time Complexity: O(N + M) where N = text length, M = pattern length.
 */
public class KMP {

    public static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static List<Integer> search(String text, String pattern, boolean caseSensitive) {
        List<Integer> occurrences = new ArrayList<>();
        if (pattern == null || text == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return occurrences;
        }

        String t = caseSensitive ? text : text.toLowerCase();
        String p = caseSensitive ? pattern : pattern.toLowerCase();

        int[] lps = computeLPS(p);
        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < t.length()) {
            if (p.charAt(j) == t.charAt(i)) {
                i++;
                j++;
            }
            if (j == p.length()) {
                occurrences.add(i - j);
                j = lps[j - 1];
            } else if (i < t.length() && p.charAt(j) != t.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return occurrences;
    }

    public static List<Integer> search(String text, String pattern) {
        return search(text, pattern, false); // Default case-insensitive
    }
}
