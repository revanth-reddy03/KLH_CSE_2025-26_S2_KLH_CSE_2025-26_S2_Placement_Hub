package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Suffix Array implementation for fast substring searches and text indexing.
 */
public class SuffixArray {

    private final String text;
    private final Integer[] suffixArray;

    public SuffixArray(String text) {
        this.text = text != null ? text : "";
        int n = this.text.length();
        this.suffixArray = new Integer[n];

        for (int i = 0; i < n; i++) {
            suffixArray[i] = i;
        }

        // Sort suffixes lexicographically
        Arrays.sort(suffixArray, (a, b) -> {
            int len = Math.min(n - a, n - b);
            for (int i = 0; i < len; i++) {
                char c1 = this.text.charAt(a + i);
                char c2 = this.text.charAt(b + i);
                if (c1 != c2) {
                    return Character.compare(c1, c2);
                }
            }
            return Integer.compare(n - a, n - b);
        });
    }

    public Integer[] getSuffixArray() {
        return suffixArray.clone();
    }

    public boolean contains(String pattern) {
        return searchFirst(pattern) != -1;
    }

    public int searchFirst(String pattern) {
        if (pattern == null || pattern.isEmpty() || text.isEmpty()) return -1;
        int low = 0;
        int high = suffixArray.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = suffixArray[mid];
            int cmp = comparePrefix(pattern, suffixStart);

            if (cmp == 0) {
                return suffixStart;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

    public List<Integer> searchAll(String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (pattern == null || pattern.isEmpty() || text.isEmpty()) return occurrences;

        int low = 0, high = suffixArray.length - 1;
        int first = -1;

        // Find lower bound
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = comparePrefix(pattern, suffixArray[mid]);
            if (cmp == 0) {
                first = mid;
                high = mid - 1; // Keep searching left
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        if (first == -1) return occurrences;

        // Collect all matching adjacent suffixes in the sorted array
        for (int i = first; i < suffixArray.length; i++) {
            if (comparePrefix(pattern, suffixArray[i]) == 0) {
                occurrences.add(suffixArray[i]);
            } else {
                break;
            }
        }
        return occurrences;
    }

    private int comparePrefix(String pattern, int suffixStart) {
        int pLen = pattern.length();
        int tLen = text.length();

        for (int i = 0; i < pLen; i++) {
            if (suffixStart + i >= tLen) {
                return 1; // Pattern is longer than suffix
            }
            char pc = pattern.charAt(i);
            char sc = text.charAt(suffixStart + i);
            if (pc != sc) {
                return Character.compare(pc, sc);
            }
        }
        return 0; // Pattern matches prefix of suffix
    }
}
