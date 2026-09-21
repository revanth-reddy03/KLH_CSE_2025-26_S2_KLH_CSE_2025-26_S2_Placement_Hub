package algorithms;

import java.util.Arrays;
import java.util.List;

public class EditDistanceTest {
    public static void main(String[] args) {
        testDistance();
        testFuzzySuggestions();
        System.out.println("✅ [PASS] All Edit Distance tests passed successfully!");
    }

    private static void testDistance() {
        assert EditDistance.computeDistance("kitten", "sitting") == 3 : "kitten -> sitting should be 3";
        assert EditDistance.computeDistance("Java", "Java") == 0 : "Identical strings should be 0";
        assert EditDistance.computeDistance("Python", "Pythn") == 1 : "Missing 'o' should be 1";
    }

    private static void testFuzzySuggestions() {
        List<String> skills = Arrays.asList("Java", "Python", "JavaScript", "React", "Docker", "Kubernetes");
        // Typo: "Pythn"
        List<String> suggestions = EditDistance.findClosestMatches("Pythn", skills, 2);
        assert !suggestions.isEmpty() : "Should find suggestions";
        assert suggestions.get(0).equalsIgnoreCase("Python") : "First suggestion should be Python";
    }
}
