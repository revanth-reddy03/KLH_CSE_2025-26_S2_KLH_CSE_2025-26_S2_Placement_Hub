package algorithms;

import java.util.*;

/**
 * SearchHistory manages user search history for the Placement Hub.
 * Combines a Deque (for MRU recent query retrieval) and a HashMap (for frequency analytics).
 */
public class SearchHistory {

    private final Deque<String> historyStack;
    private final Map<String, Integer> frequencyMap;
    private final int capacity;

    public SearchHistory(int capacity) {
        this.capacity = capacity;
        this.historyStack = new ArrayDeque<>(capacity);
        this.frequencyMap = new HashMap<>();
    }

    public SearchHistory() {
        this(50);
    }

    public synchronized void recordSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;
        String cleanQuery = query.trim();

        // Update frequency
        frequencyMap.put(cleanQuery, frequencyMap.getOrDefault(cleanQuery, 0) + 1);

        // Maintain MRU order: remove existing if present
        historyStack.remove(cleanQuery);
        if (historyStack.size() >= capacity) {
            historyStack.removeLast();
        }
        historyStack.addFirst(cleanQuery);
    }

    public synchronized List<String> getRecentSearches(int limit) {
        List<String> list = new ArrayList<>();
        int count = 0;
        for (String q : historyStack) {
            if (count++ >= limit) break;
            list.add(q);
        }
        return list;
    }

    public synchronized String undoLastSearch() {
        if (historyStack.isEmpty()) return null;
        return historyStack.removeFirst();
    }

    public synchronized Map<String, Integer> getPopularQueries(int topN) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(frequencyMap.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        Map<String, Integer> result = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, Integer> entry : list) {
            if (count++ >= topN) break;
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public synchronized void clear() {
        historyStack.clear();
        frequencyMap.clear();
    }

    public synchronized int size() {
        return historyStack.size();
    }
}
