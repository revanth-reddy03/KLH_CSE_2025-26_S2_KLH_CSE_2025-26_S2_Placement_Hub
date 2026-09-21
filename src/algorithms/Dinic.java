package algorithms;

import java.util.*;

/**
 * Dinic's Algorithm for Maximum Flow.
 * Time Complexity: O(V^2 * E), and O(E * sqrt(V)) for unit networks / bipartite matching.
 * Uses BFS for Level Graphs and DFS for finding Blocking Flows.
 */
public class Dinic {

    public static class Edge {
        int to;
        int capacity;
        int flow;
        int rev; // index of reverse edge

        public Edge(int to, int capacity, int rev) {
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
            this.rev = rev;
        }
    }

    private final int n;
    private final List<List<Edge>> graph;
    private final int[] level;
    private final int[] ptr;

    public Dinic(int n) {
        this.n = n;
        this.graph = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        this.level = new int[n];
        this.ptr = new int[n];
    }

    public void addEdge(int from, int to, int capacity) {
        Edge forward = new Edge(to, capacity, graph.get(to).size());
        Edge backward = new Edge(from, 0, graph.get(from).size());
        graph.get(from).add(forward);
        graph.get(to).add(backward);
    }

    private boolean bfs(int source, int sink) {
        Arrays.fill(level, -1);
        level[source] = 0;
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Edge edge : graph.get(u)) {
                if (edge.capacity - edge.flow > 0 && level[edge.to] == -1) {
                    level[edge.to] = level[u] + 1;
                    queue.offer(edge.to);
                }
            }
        }
        return level[sink] != -1;
    }

    private int dfs(int u, int sink, int pushed) {
        if (pushed == 0) return 0;
        if (u == sink) return pushed;

        for (int cid = ptr[u]; cid < graph.get(u).size(); cid = ++ptr[u]) {
            Edge edge = graph.get(u).get(cid);
            int tr = edge.to;

            if (level[u] + 1 != level[tr] || edge.capacity - edge.flow == 0) {
                continue;
            }

            int trPushed = dfs(tr, sink, Math.min(pushed, edge.capacity - edge.flow));
            if (trPushed == 0) continue;

            edge.flow += trPushed;
            graph.get(tr).get(edge.rev).flow -= trPushed;
            return trPushed;
        }

        return 0;
    }

    public int computeMaxFlow(int source, int sink) {
        int flow = 0;
        while (bfs(source, sink)) {
            Arrays.fill(ptr, 0);
            while (true) {
                int pushed = dfs(source, sink, Integer.MAX_VALUE);
                if (pushed == 0) break;
                flow += pushed;
            }
        }
        return flow;
    }

    public List<List<Edge>> getGraph() {
        return graph;
    }
}
