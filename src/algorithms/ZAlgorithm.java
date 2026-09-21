package algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-Algorithm for linear time string matching.
 * Time Complexity: O(N + M).
 */
public class ZAlgorithm {

    public static int[] computeZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i > r) {
                l = r = i;
                while (r < n && s.charAt(r - l) == s.charAt(r)) {
                    r++;
                }
                z[i] = r - l;
                r--;
            } else {
                int k = i - l;
                if (z[k] < r - i + 1) {
                    z[i] = z[k];
                } else {
                    l = i;
                    while (r < n && s.charAt(r - l) == s.charAt(r)) {
                        r++;
                    }
                    z[i] = r - l;
                    r--;
                }
            }
        }
        return z;
    }

    public static List<Integer> search(String text, String pattern, boolean caseSensitive) {
        List<Integer> occurrences = new ArrayList<>();
        if (pattern == null || text == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return occurrences;
        }

        String t = caseSensitive ? text : text.toLowerCase();
        String p = caseSensitive ? pattern : pattern.toLowerCase();

        // Concatenate pattern + delimiter + text
        String concat = p + "$" + t;
        int[] z = computeZArray(concat);
        int pLen = p.length();

        for (int i = 0; i < z.length; i++) {
            if (z[i] == pLen) {
                occurrences.add(i - pLen - 1);
            }
        }
        return occurrences;
    }

    public static List<Integer> search(String text, String pattern) {
        return search(text, pattern, false);
    }
}
