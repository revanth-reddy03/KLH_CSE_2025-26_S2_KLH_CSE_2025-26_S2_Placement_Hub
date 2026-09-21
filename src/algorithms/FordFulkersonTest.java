package algorithms;

public class FordFulkersonTest {
    public static void main(String[] args) {
        testMaxFlow();
        System.out.println("✅ [PASS] All Ford-Fulkerson tests passed successfully!");
    }

    private static void testMaxFlow() {
        // Standard 6-node network
        // 0: Source, 5: Sink
        int[][] capacity = {
            {0, 16, 13, 0, 0, 0},
            {0, 0, 10, 12, 0, 0},
            {0, 4, 0, 0, 14, 0},
            {0, 0, 9, 0, 0, 20},
            {0, 0, 0, 7, 0, 4},
            {0, 0, 0, 0, 0, 0}
        };

        FordFulkerson ff = new FordFulkerson();
        int flow = ff.computeMaxFlow(capacity, 0, 5);
        assert flow == 23 : "Expected max flow 23, got " + flow;
    }
}
