package algorithms;

public class DinicTest {
    public static void main(String[] args) {
        testMaxFlow();
        System.out.println("✅ [PASS] All Dinic tests passed successfully!");
    }

    private static void testMaxFlow() {
        Dinic dinic = new Dinic(6);
        dinic.addEdge(0, 1, 16);
        dinic.addEdge(0, 2, 13);
        dinic.addEdge(1, 2, 10);
        dinic.addEdge(1, 3, 12);
        dinic.addEdge(2, 1, 4);
        dinic.addEdge(2, 4, 14);
        dinic.addEdge(3, 2, 9);
        dinic.addEdge(3, 5, 20);
        dinic.addEdge(4, 3, 7);
        dinic.addEdge(4, 5, 4);

        int flow = dinic.computeMaxFlow(0, 5);
        assert flow == 23 : "Expected Dinic max flow 23, got " + flow;
    }
}
