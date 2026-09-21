# Campus Placement Hub (Java DSA Algorithms Suite)

A high-performance algorithmic campus placement and recruitment engine implemented in Java. The system leverages advanced **String Matching**, **Fuzzy Search**, **Text Indexing**, and **Network Flow / Bipartite Graph Matching** algorithms to match student resumes with corporate job profiles and vacancies.

---

## 📁 Project Structure

```text
Placement_Hub/
├── corpus/
│   └── student-records/            # Corpus of 60+ detailed student resumes/records
│       ├── STU1001.txt
│       ├── STU1002.txt
│       └── ...
├── src/
│   └── algorithms/                 # Core algorithms & test suite
│       ├── .gitkeep
│       ├── AhoCorasick.java        # Multi-pattern string searching automaton
│       ├── AhoCorasickTest.java    # Unit tests for Aho-Corasick
│       ├── BipartiteMatching.java  # Maximum Bipartite Matching (Kuhn's Augmenting Path)
│       ├── BipartiteMatchingTest.java
│       ├── CorpusLoader.java       # Parser & loader for student records corpus
│       ├── Dinic.java              # Dinic's blocking flow max-flow algorithm
│       ├── DinicTest.java
│       ├── EditDistance.java       # Levenshtein distance for fuzzy search & typo tolerance
│       ├── EditDistanceTest.java
│       ├── EdmondsKarp.java        # Edmonds-Karp BFS max-flow algorithm
│       ├── EdmondsKarpTest.java
│       ├── FordFulkerson.java      # Ford-Fulkerson DFS max-flow algorithm
│       ├── FordFulkersonTest.java
│       ├── KMP.java                # Knuth-Morris-Pratt pattern searching
│       ├── KMPTest.java
│       ├── Main.java               # End-to-end integration demo & runner
│       ├── SearchHistory.java      # Recruiter recent & popular query manager
│       ├── SuffixArray.java        # Substring search & indexing
│       ├── SuffixArrayTest.java
│       ├── ZAlgorithm.java         # Linear-time pattern matching (Z-array)
│       └── ZAlgorithmTest.java
└── README.md
```

---

## 🧠 Algorithmic Mapping & Placement Use-Cases

### 1. Resume Screening & Text Processing
* **Aho-Corasick (`AhoCorasick.java`)**: 
  Scans an entire student resume against a dictionary of required skills (e.g., `["Java", "Spring Boot", "Docker", "AWS", "Kafka"]`) simultaneously in $\mathcal{O}(\text{Text Length} + \text{Matches})$ time.
* **KMP Algorithm (`KMP.java`)**: 
  Exact keyword search (e.g., finding certifications, specific projects) in $\mathcal{O}(N + M)$ using the Longest Prefix Suffix (LPS) table.
* **Z-Algorithm (`ZAlgorithm.java`)**: 
  Linear-time substring search by constructing the Z-array on `Pattern + '$' + Text`.
* **Suffix Array (`SuffixArray.java`)**: 
  Lexicographically sorts all suffixes of a student resume for fast binary-search substring queries in $\mathcal{O}(M \log N)$.
* **Edit Distance (`EditDistance.java`)**: 
  Dynamic programming Levenshtein distance providing typo tolerance and fuzzy suggestions when recruiters misspell skills or names (e.g., `"Kubernets"` $\to$ `"Kubernetes"`).
* **Search History (`SearchHistory.java`)**: 
  Maintains recruiter recent search history (using a Deque for MRU) and tracks query popularity (using a frequency map).

### 2. Candidate-Company Allocation & Matching
* **Bipartite Matching (`BipartiteMatching.java`)**: 
  Matches eligible candidates to company vacancies using augmenting paths to maximize overall placement cardinality.
* **Network Flow Algorithms (`FordFulkerson.java`, `EdmondsKarp.java`, `Dinic.java`)**: 
  Models multi-capacity hiring networks:
  $$\text{Source} \xrightarrow{\text{cap } 1} \text{Students} \xrightarrow{\text{cap } 1} \text{Companies} \xrightarrow{\text{cap } V_j} \text{Sink}$$
  where $V_j$ is the company's hiring quota.

---

## 🚀 How to Compile and Run

### Run the Complete Algorithms Suite:
```powershell
javac -d bin src/algorithms/*.java
java -cp bin algorithms.Main
```

### Run Automated Unit Tests:
```powershell
# Run with Java assertions enabled (-ea)
java -ea -cp bin algorithms.AhoCorasickTest
java -ea -cp bin algorithms.BipartiteMatchingTest
java -ea -cp bin algorithms.DinicTest
java -ea -cp bin algorithms.EditDistanceTest
java -ea -cp bin algorithms.EdmondsKarpTest
java -ea -cp bin algorithms.FordFulkersonTest
java -ea -cp bin algorithms.KMPTest
java -ea -cp bin algorithms.SuffixArrayTest
java -ea -cp bin algorithms.ZAlgorithmTest
```
