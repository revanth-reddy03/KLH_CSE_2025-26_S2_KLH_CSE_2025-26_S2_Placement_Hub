package algorithms;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("               CAMPUS PLACEMENT HUB - ALGORITHMS SUITE                          ");
        System.out.println("================================================================================");

        // 1. Load Corpus
        String corpusPath = "corpus/student-records";
        System.out.println("\n[1] Loading Student Records from: " + corpusPath);
        List<CorpusLoader.StudentRecord> records = CorpusLoader.loadStudentRecords(corpusPath);
        System.out.println("    Successfully loaded " + records.size() + " student profile documents.\n");

        if (records.isEmpty()) {
            System.err.println("No records found in " + corpusPath);
            return;
        }

        // 2. KMP String Matching
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("[2] KMP Algorithm: Exact Pattern Search");
        System.out.println("--------------------------------------------------------------------------------");
        String kmpKeyword = "Distributed Systems";
        System.out.println("Searching student resumes for keyword: \"" + kmpKeyword + "\"");
        int kmpMatches = 0;
        for (CorpusLoader.StudentRecord rec : records) {
            List<Integer> pos = KMP.search(rec.getFullText(), kmpKeyword);
            if (!pos.isEmpty()) {
                System.out.printf("  ✓ Found in %s (%s) at index %s\n", rec.getStudentId(), rec.getName(), pos.toString());
                kmpMatches++;
            }
        }
        System.out.println("  Total students mentioning \"" + kmpKeyword + "\": " + kmpMatches);

        // 3. Z-Algorithm String Matching
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[3] Z-Algorithm: Linear-Time Pattern Matching");
        System.out.println("--------------------------------------------------------------------------------");
        String zKeyword = "Machine Learning";
        System.out.println("Searching student resumes for: \"" + zKeyword + "\"");
        int zMatches = 0;
        for (CorpusLoader.StudentRecord rec : records) {
            List<Integer> pos = ZAlgorithm.search(rec.getFullText(), zKeyword);
            if (!pos.isEmpty()) {
                zMatches++;
            }
        }
        System.out.printf("  Total students with \"%s\" in resume: %d / %d\n", zKeyword, zMatches, records.size());

        // 4. Aho-Corasick Multi-Pattern Automaton
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[4] Aho-Corasick Automaton: Simultaneous Multi-Keyword Resume Screening");
        System.out.println("--------------------------------------------------------------------------------");
        List<String> companyTechStack = Arrays.asList("java", "spring boot", "docker", "kubernetes", "aws", "microservices");
        System.out.println("Company Target Tech Stack: " + companyTechStack);
        AhoCorasick ac = new AhoCorasick(companyTechStack);

        CorpusLoader.StudentRecord sampleStudent = records.get(0);
        System.out.println("Screening Resume for: " + sampleStudent.getName() + " (" + sampleStudent.getStudentId() + ")");
        Map<String, Integer> skillFreqs = ac.getKeywordFrequencies(sampleStudent.getFullText());
        skillFreqs.forEach((skill, count) -> System.out.printf("  • Skill: %-15s | Found: %d time(s)\n", skill.toUpperCase(), count));

        // 5. Edit Distance (Fuzzy Search & Typo Tolerance)
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[5] Edit Distance (Levenshtein): Fuzzy Search for Recruiter Queries");
        System.out.println("--------------------------------------------------------------------------------");
        String typoQuery = "Kubernets"; // Missing 'e'
        Set<String> allVocab = new HashSet<>();
        for (CorpusLoader.StudentRecord r : records) {
            allVocab.addAll(r.getSkills());
        }
        System.out.println("Recruiter typed with typo: \"" + typoQuery + "\"");
        List<String> suggestions = EditDistance.findClosestMatches(typoQuery, allVocab, 2);
        System.out.println("Did you mean? " + suggestions);

        // 6. Search History (Stack & Frequency Tracking)
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[6] Search History: Recruiter Recent & Popular Queries");
        System.out.println("--------------------------------------------------------------------------------");
        SearchHistory history = new SearchHistory();
        history.recordSearch("Java");
        history.recordSearch("Cloud AWS");
        history.recordSearch("Docker");
        history.recordSearch("Java");
        history.recordSearch("Machine Learning");
        System.out.println("Recent Searches (MRU): " + history.getRecentSearches(3));
        System.out.println("Popular Search Analytics: " + history.getPopularQueries(3));

        // 7. Suffix Array Substring Indexing
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[7] Suffix Array: Indexing Student Profile & Substring Search");
        System.out.println("--------------------------------------------------------------------------------");
        SuffixArray sa = new SuffixArray(sampleStudent.getFullText());
        String queryTerm = "KMP Implementation";
        boolean hasTerm = sa.contains(queryTerm);
        System.out.printf("Does %s's profile contain '%s'? %s\n", sampleStudent.getName(), queryTerm, hasTerm ? "YES" : "NO");

        // 8. Bipartite Matching & Network Flow (Role Allocation)
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[8] Bipartite Matching & Network Flow: Student-to-Role Placement Allocation");
        System.out.println("--------------------------------------------------------------------------------");

        // Consider top 10 students and 5 technical roles
        int nStudents = Math.min(10, records.size());
        String[] roles = {
            "Computer Vision Engineer",
            "Machine Learning Researcher",
            "Full Stack Engineer",
            "Systems & Cloud Architect",
            "Software Development Engineer"
        };
        String[] requiredSkills = {
            "Image Processing",
            "Machine Learning",
            "JavaScript",
            "Distributed Systems",
            "Software Engineering"
        };

        boolean[][] bipartiteMatrix = new boolean[nStudents][roles.length];
        for (int i = 0; i < nStudents; i++) {
            CorpusLoader.StudentRecord s = records.get(i);
            for (int j = 0; j < roles.length; j++) {
                if (s.getFullText().contains(requiredSkills[j])) {
                    bipartiteMatrix[i][j] = true;
                }
            }
        }

        BipartiteMatching bm = new BipartiteMatching(nStudents, roles.length, bipartiteMatrix);
        int matchedCount = bm.computeMaxMatching();
        System.out.printf("Maximum Bipartite Matching Result: %d / %d roles filled.\n", matchedCount, roles.length);
        int[] studentAllocations = bm.getStudentMatches();
        for (int i = 0; i < nStudents; i++) {
            if (studentAllocations[i] != -1) {
                System.out.printf("  ✓ %-18s (Roll: %s) matched with -> %s\n",
                        records.get(i).getName(), records.get(i).getRollNumber(), roles[studentAllocations[i]]);
            }
        }

        // 9. Dinic / Edmonds-Karp Max Flow
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[9] Max Flow Algorithms (Dinic, Edmonds-Karp, Ford-Fulkerson)");
        System.out.println("--------------------------------------------------------------------------------");
        // Network: Source (0) -> Students (1..nStudents) -> Roles -> Sink
        int totalNodes = 1 + nStudents + roles.length + 1;
        int source = 0;
        int sink = totalNodes - 1;
        int compOffset = 1 + nStudents;

        int[][] capacity = new int[totalNodes][totalNodes];
        Dinic dinic = new Dinic(totalNodes);

        for (int i = 0; i < nStudents; i++) {
            // Source to student (capacity 1)
            capacity[source][1 + i] = 1;
            dinic.addEdge(source, 1 + i, 1);

            // Student to role
            for (int j = 0; j < roles.length; j++) {
                if (bipartiteMatrix[i][j]) {
                    capacity[1 + i][compOffset + j] = 1;
                    dinic.addEdge(1 + i, compOffset + j, 1);
                }
            }
        }

        // Role to sink (quota: e.g. 2 per role)
        int quotaPerRole = 2;
        for (int j = 0; j < roles.length; j++) {
            capacity[compOffset + j][sink] = quotaPerRole;
            dinic.addEdge(compOffset + j, sink, quotaPerRole);
        }

        int dinicFlow = dinic.computeMaxFlow(source, sink);
        int ekFlow = new EdmondsKarp().computeMaxFlow(capacity, source, sink);
        int ffFlow = new FordFulkerson().computeMaxFlow(capacity, source, sink);

        System.out.printf("Dinic Max Flow       : %d candidates allocated\n", dinicFlow);
        System.out.printf("Edmonds-Karp Max Flow: %d candidates allocated\n", ekFlow);
        System.out.printf("Ford-Fulkerson Flow  : %d candidates allocated\n", ffFlow);

        System.out.println("\n================================================================================");
        System.out.println("                     ALL ALGORITHM DEMONSTRATIONS COMPLETE                      ");
        System.out.println("================================================================================");
    }
}
