package algorithms;

import java.util.Arrays;

/**
 * Ford-Fulkerson algorithm for Maximum Flow using DFS.
 * Time Complexity: O(E * max_flow).
 */
public class FordFulkerson {

    private boolean dfs(int[][] residualGraph, int u, int sink, boolean[] visited, int[] parent) {
        visited[u] = true;
        if (u == sink) return true;

        for (int v = 0; v < residualGraph.length; v++) {
            if (!visited[v] && residualGraph[u][v] > 0) {
                parent[v] = u;
                if (dfs(residualGraph, v, sink, visited, parent)) {
                    return true;
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

        boolean[] visited = new boolean[n];
        while (dfs(residualGraph, source, sink, visited, parent)) {
            // Find bottleneck capacity along path
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
            Arrays.fill(visited, false);
        }

        return maxFlow;
    }
}
