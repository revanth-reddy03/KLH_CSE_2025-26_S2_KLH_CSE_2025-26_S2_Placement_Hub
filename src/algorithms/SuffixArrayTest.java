package algorithms;

import java.util.List;

public class SuffixArrayTest {
    public static void main(String[] args) {
        testBasicSearch();
        testSearchAll();
        testNotFound();
        System.out.println("✅ [PASS] All Suffix Array tests passed successfully!");
    }

    private static void testBasicSearch() {
        SuffixArray sa = new SuffixArray("banana");
        assert sa.contains("nan") : "Should contain 'nan'";
        assert sa.contains("ana") : "Should contain 'ana'";
        assert !sa.contains("apple") : "Should not contain 'apple'";
    }

    private static void testSearchAll() {
        SuffixArray sa = new SuffixArray("banana");
        List<Integer> matches = sa.searchAll("an");
        assert matches.size() == 2 : "Expected 2 matches for 'an', got " + matches.size();
        assert matches.contains(1) && matches.contains(3) : "Matches should be at indices 1 and 3";
    }

    private static void testNotFound() {
        SuffixArray sa = new SuffixArray("Placement Hub Corpus");
        assert sa.searchFirst("Zookeeper") == -1 : "Should return -1 when not found";
    }
}
