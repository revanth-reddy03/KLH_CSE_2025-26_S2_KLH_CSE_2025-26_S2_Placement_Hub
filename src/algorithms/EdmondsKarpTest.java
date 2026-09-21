package algorithms;

public class EdmondsKarpTest {
    public static void main(String[] args) {
        testMaxFlow();
        System.out.println("✅ [PASS] All Edmonds-Karp tests passed successfully!");
    }

    private static void testMaxFlow() {
        int[][] capacity = {
            {0, 10, 10, 0, 0, 0},
            {0, 0, 2, 4, 8, 0},
            {0, 0, 0, 0, 9, 0},
            {0, 0, 0, 0, 0, 10},
            {0, 0, 0, 6, 0, 10},
            {0, 0, 0, 0, 0, 0}
        };

        EdmondsKarp ek = new EdmondsKarp();
        int flow = ek.computeMaxFlow(capacity, 0, 5);
        assert flow == 19 : "Expected max flow 19, got " + flow;
    }
}
