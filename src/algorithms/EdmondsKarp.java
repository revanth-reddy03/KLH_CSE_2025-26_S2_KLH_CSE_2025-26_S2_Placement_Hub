package algorithms;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Edmonds-Karp algorithm for Maximum Flow using BFS.
 * Time Complexity: O(V * E^2).
 */
public class EdmondsKarp {

    private boolean bfs(int[][] residualGraph, int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        parent[source] = -2; // Mark source as visited

        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < residualGraph.length; v++) {
                if (parent[v] == -1 && residualGraph[u][v] > 0) {
                    parent[v] = u;
                    if (v == sink) {
                        return true;
                    }
                    queue.offer(v);
                }
            }
        }
        return false;
    }

    public int computeMaxFlow(int[][] capacity, int source, int sink) {
        int n = capacity.length;
        int[][] residualGraph = new int[n][n];

        for (int i = 0; i < n; i++) {
            System.arraycopy(capacity[i], 0, residualGraph[i], 0, n);
        }

        int[] parent = new int[n];
        int maxFlow = 0;

        while (bfs(residualGraph, source, sink, parent)) {
            // Find bottleneck capacity along the BFS shortest path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            // Update residual capacities
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }
}
