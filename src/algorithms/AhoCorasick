package algorithms;

import java.util.*;

/**
 * Aho-Corasick multi-pattern string matching automaton.
 * Scans text in O(Text Length + Matches) time across multiple keywords.
 * Essential for resume keyword screening (e.g. matching 20 required skills at once).
 */
public class AhoCorasick {

    public static class MatchResult {
        private final String keyword;
        private final int startIndex;
        private final int endIndex;

        public MatchResult(String keyword, int startIndex, int endIndex) {
            this.keyword = keyword;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getKeyword() { return keyword; }
        public int getStartIndex() { return startIndex; }
        public int getEndIndex() { return endIndex; }

        @Override
        public String toString() {
            return String.format("%s @ [%d-%d]", keyword, startIndex, endIndex);
        }
    }

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        Node failureLink = null;
        List<String> output = new ArrayList<>();
    }

    private final Node root;
    private boolean built = false;

    public AhoCorasick() {
        this.root = new Node();
    }

    public AhoCorasick(Collection<String> keywords) {
        this.root = new Node();
        for (String kw : keywords) {
            addKeyword(kw);
        }
        build();
    }

    public void addKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        String kw = keyword.toLowerCase().trim();
        Node curr = root;
        for (char c : kw.toCharArray()) {
            curr = curr.children.computeIfAbsent(c, k -> new Node());
        }
        curr.output.add(kw);
        built = false;
    }

    public void build() {
        Queue<Node> queue = new LinkedList<>();

        // Level 1 nodes failure link to root
        for (Node child : root.children.values()) {
            child.failureLink = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            Node curr = queue.poll();

            for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();
                queue.add(child);

                Node f = curr.failureLink;
                while (f != null && !f.children.containsKey(ch)) {
                    f = f.failureLink;
                }
                child.failureLink = (f != null) ? f.children.get(ch) : root;
                child.output.addAll(child.failureLink.output);
            }
        }
        built = true;
    }

    public List<MatchResult> search(String text) {
        if (!built) build();
        List<MatchResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) return results;

        String lowerText = text.toLowerCase();
        Node curr = root;

        for (int i = 0; i < lowerText.length(); i++) {
            char ch = lowerText.charAt(i);

            while (curr != root && !curr.children.containsKey(ch)) {
                curr = curr.failureLink;
            }

            curr = curr.children.getOrDefault(ch, root);

            for (String matchedWord : curr.output) {
                int start = i - matchedWord.length() + 1;
                results.add(new MatchResult(matchedWord, start, i));
            }
        }
        return results;
    }

    public Map<String, Integer> getKeywordFrequencies(String text) {
        List<MatchResult> matches = search(text);
        Map<String, Integer> freqMap = new HashMap<>();
        for (MatchResult m : matches) {
            freqMap.put(m.getKeyword(), freqMap.getOrDefault(m.getKeyword(), 0) + 1);
        }
        return freqMap;
    }
}
