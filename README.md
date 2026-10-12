# Campus Placement Hub (Java DSA Algorithms Suite)

A high-performance algorithmic campus placement and recruitment search engine implemented in Java. The system leverages advanced **String Matching**, **Fuzzy Search**, **Text Indexing**, and **Network Flow / Bipartite Graph Matching** algorithms to search and match student profile records with corporate job profiles and vacancies.

---

## 👥 Team Members

| Roll Number | Name |
| :---: | :--- |
| **2520030391** | Subhash |
| **2520030423** | Akhil Kumar |
| **2520030424** | Revanth Reddy |

---

## 📁 Project Structure

```text
KLH_CSE_2025-26_S2_KLH_CSE_2025-26_S2_Placement_Hub/
├── README.md
├── corpus/
│   └── student-records/            # Corpus of 500 student records (SR001.txt ... SR500.txt)
│       ├── SR001.txt
│       ├── SR002.txt               # Manish Sinha profile record
│       ├── ...
│       └── SR500.txt
├── src/
│   └── algorithms/                 # Core algorithms & test suite
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
│       ├── WebServer.java          # Embedded zero-dependency HTTP server & REST API
│       ├── ZAlgorithm.java         # Linear-time pattern matching (Z-array)
│       └── ZAlgorithmTest.java
└── web/                            # Interactive Web Dashboard & DSA Playground
    ├── index.html                  # Responsive 8-tab SPA interface
    ├── styles.css                  # Dark-theme glassmorphic UI styles
    ├── app.js                      # Client-side DSA visualizer & API client
    └── students_data.json          # 500-student structured corpus dataset
```

---

## 🧠 Algorithmic Mapping & Placement Use-Cases

### 1. Resume Screening & Text Processing
* **Aho-Corasick (`AhoCorasick.java`)**: 
  Scans an entire student resume against a dictionary of required skills (e.g., `["Java", "Spring Boot", "Docker", "AWS", "Kafka"]`) simultaneously in $\mathcal{O}(\text{Text Length} + \text{Matches})$ time.
* **KMP Algorithm (`KMP.java`)**: 
  Exact keyword search (e.g., finding certifications, specific projects) in $\mathcal{O}(N + M)$ using the Longest Proper Prefix which is also Suffix (LPS) table.
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
  $$\text{Source} \xrightarrow{\text{cap } 1} \text{Students} \xrightarrow{\text{cap } 1} \text{Roles} \xrightarrow{\text{cap } V_j} \text{Sink}$$
  where $V_j$ is the hiring quota for that role.

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

---

## 🌐 Interactive Web Dashboard

The project includes a web application for recruiters and faculty to interact with the 500-student corpus, test string algorithms with live visualizers, run fuzzy search with dynamic programming matrices, allocate students to roles via Dinic Max Flow, and monitor query analytics.

### Option 1: Embedded Java Web Server (Recommended)
Compile and launch the zero-dependency Java HTTP server:
```powershell
javac -d bin src/algorithms/*.java
java -cp bin algorithms.WebServer
```
Then navigate to: **`http://localhost:8080/`**

### Option 2: Direct Browser Launch
Open `web/index.html` directly in any web browser (Chrome, Edge, Firefox, Safari). The client-side DSA engine will load `web/students_data.json` directly with full offline functionality!

