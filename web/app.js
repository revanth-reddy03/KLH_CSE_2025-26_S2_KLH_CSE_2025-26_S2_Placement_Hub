/**
 * Placement Hub - Algorithmic Search & Allocation Engine
 * Client-Side DSA Engine & UI Orchestrator
 */

// Global State
let allStudents = [];
let filteredStudents = [];
let currentPage = 1;
const pageSize = 12;

let searchHistory = [];
let queryFrequencies = {};

// Default Roles for Allocation
let corporateRoles = [
  { id: 'R1', name: 'Computer Vision Engineer', req: 'Image Processing', quota: 2 },
  { id: 'R2', name: 'Machine Learning Researcher', req: 'Machine Learning', quota: 2 },
  { id: 'R3', name: 'Full Stack Engineer', req: 'JavaScript', quota: 2 },
  { id: 'R4', name: 'Systems & Cloud Architect', req: 'Distributed Systems', quota: 2 },
  { id: 'R5', name: 'Software Development Engineer', req: 'Software Engineering', quota: 2 }
];

// Vocabulary for Fuzzy Search
let vocabularySet = new Set();

// ============================================================================
// 1. DATA INITIALIZATION
// ============================================================================
async function initData() {
  try {
    const response = await fetch('students_data.json');
    allStudents = await response.json();
    filteredStudents = [...allStudents];
    
    // Build vocabulary
    allStudents.forEach(s => {
      s.skills.forEach(sk => vocabularySet.add(sk));
      s.languages.forEach(l => vocabularySet.add(l));
      s.techInterests.forEach(ti => vocabularySet.add(ti));
    });

    document.getElementById('total-students-count').innerText = allStudents.length;
    document.getElementById('hero-students-count').innerText = allStudents.length;
    
    renderCorpusTable();
    updateHistoryUI();
    renderAllocationRoles();
  } catch (err) {
    console.error("Failed to load students_data.json", err);
  }
}

// ============================================================================
// 2. ALGORITHMIC IMPLEMENTATIONS (JAVASCRIPT MIRROR OF JAVA DSA)
// ============================================================================

// --- KMP Algorithm ---
function computeLPS(pattern) {
  const m = pattern.length;
  const lps = new Array(m).fill(0);
  let len = 0;
  let i = 1;
  while (i < m) {
    if (pattern[i].toLowerCase() === pattern[len].toLowerCase()) {
      len++;
      lps[i] = len;
      i++;
    } else {
      if (len !== 0) {
        len = lps[len - 1];
      } else {
        lps[i] = 0;
        i++;
      }
    }
  }
  return lps;
}

function kmpSearch(text, pattern) {
  const matches = [];
  if (!pattern || !text || text.length < pattern.length) return matches;
  const lps = computeLPS(pattern);
  let i = 0, j = 0;
  const t = text.toLowerCase();
  const p = pattern.toLowerCase();

  while (i < t.length) {
    if (p[j] === t[i]) {
      i++;
      j++;
    }
    if (j === p.length) {
      matches.push(i - j);
      j = lps[j - 1];
    } else if (i < t.length && p[j] !== t[i]) {
      if (j !== 0) {
        j = lps[j - 1];
      } else {
        i++;
      }
    }
  }
  return matches;
}

// --- Z-Algorithm ---
function computeZ(s) {
  const n = s.length;
  const z = new Array(n).fill(0);
  let l = 0, r = 0;
  for (let i = 1; i < n; i++) {
    if (i > r) {
      l = r = i;
      while (r < n && s[r - l].toLowerCase() === s[r].toLowerCase()) r++;
      z[i] = r - l;
      r--;
    } else {
      let k = i - l;
      if (z[k] < r - i + 1) {
        z[i] = z[k];
      } else {
        l = i;
        while (r < n && s[r - l].toLowerCase() === s[r].toLowerCase()) r++;
        z[i] = r - l;
        r--;
      }
    }
  }
  return z;
}

function zSearch(text, pattern) {
  const matches = [];
  if (!pattern || !text || text.length < pattern.length) return matches;
  const concat = pattern + '$' + text;
  const z = computeZ(concat);
  const pLen = pattern.length;
  for (let i = 0; i < z.length; i++) {
    if (z[i] === pLen) {
      matches.push(i - pLen - 1);
    }
  }
  return matches;
}

// --- Aho-Corasick Automaton ---
class AhoCorasickNode {
  constructor() {
    this.children = {};
    this.fail = null;
    this.output = [];
  }
}

class AhoCorasick {
  constructor(keywords) {
    this.root = new AhoCorasickNode();
    keywords.forEach(kw => this.insert(kw.trim().toLowerCase()));
    this.build();
  }

  insert(word) {
    if (!word) return;
    let curr = this.root;
    for (let ch of word) {
      if (!curr.children[ch]) curr.children[ch] = new AhoCorasickNode();
      curr = curr.children[ch];
    }
    curr.output.push(word);
  }

  build() {
    const queue = [];
    for (let ch in this.root.children) {
      const child = this.root.children[ch];
      child.fail = this.root;
      queue.push(child);
    }
    while (queue.length > 0) {
      const curr = queue.shift();
      for (let ch in curr.children) {
        const child = curr.children[ch];
        queue.push(child);
        let f = curr.fail;
        while (f && !f.children[ch]) f = f.fail;
        child.fail = (f && f.children[ch]) ? f.children[ch] : this.root;
        child.output = child.output.concat(child.fail.output);
      }
    }
  }

  search(text) {
    const results = {};
    let curr = this.root;
    const lower = text.toLowerCase();
    for (let i = 0; i < lower.length; i++) {
      const ch = lower[i];
      while (curr !== this.root && !curr.children[ch]) curr = curr.fail;
      curr = curr.children[ch] || this.root;
      for (let word of curr.output) {
        results[word] = (results[word] || 0) + 1;
      }
    }
    return results;
  }
}

// --- Suffix Array ---
function buildSuffixArray(text) {
  const n = text.length;
  const suffixes = [];
  for (let i = 0; i < n; i++) {
    suffixes.push(i);
  }
  suffixes.sort((a, b) => {
    const s1 = text.substring(a);
    const s2 = text.substring(b);
    return s1.localeCompare(s2);
  });
  return suffixes;
}

function suffixArraySearch(text, pattern, suffixArray) {
  const matches = [];
  if (!pattern || !text) return matches;
  const p = pattern.toLowerCase();
  const t = text.toLowerCase();
  
  let low = 0, high = suffixArray.length - 1;
  let first = -1;

  while (low <= high) {
    let mid = Math.floor((low + high) / 2);
    let suffix = t.substring(suffixArray[mid]);
    if (suffix.startsWith(p)) {
      first = mid;
      high = mid - 1;
    } else if (p.localeCompare(suffix) < 0) {
      high = mid - 1;
    } else {
      low = mid + 1;
    }
  }

  if (first === -1) return matches;
  for (let i = first; i < suffixArray.length; i++) {
    if (t.substring(suffixArray[i]).startsWith(p)) {
      matches.push(suffixArray[i]);
    } else {
      break;
    }
  }
  return matches;
}

// --- Levenshtein Edit Distance ---
function computeLevenshtein(s1, s2) {
  const m = s1.length;
  const n = s2.length;
  const dp = Array.from({ length: m + 1 }, () => new Array(n + 1).fill(0));

  for (let i = 0; i <= m; i++) dp[i][0] = i;
  for (let j = 0; j <= n; j++) dp[0][j] = j;

  for (let i = 1; i <= m; i++) {
    for (let j = 1; j <= n; j++) {
      if (s1[i - 1].toLowerCase() === s2[j - 1].toLowerCase()) {
        dp[i][j] = dp[i - 1][j - 1];
      } else {
        dp[i][j] = 1 + Math.min(
          dp[i - 1][j - 1], // replace
          dp[i - 1][j],     // delete
          dp[i][j - 1]      // insert
        );
      }
    }
  }
  return { distance: dp[m][n], matrix: dp };
}

function findFuzzySuggestions(query, vocabulary, threshold = 2) {
  const suggestions = [];
  const q = query.trim();
  vocabulary.forEach(word => {
    const res = computeLevenshtein(q, word);
    if (res.distance <= threshold) {
      suggestions.push({ word, distance: res.distance });
    }
  });
  suggestions.sort((a, b) => a.distance - b.distance);
  return suggestions;
}

// --- Bipartite Matching (Kuhn's Augmenting Path) ---
function runBipartiteMatching(studentsSubset, roles) {
  const nStudents = studentsSubset.length;
  const nRoles = roles.length;
  const matchRoleToStudent = new Array(nRoles).fill(-1);

  // Adjacency matrix
  const adj = Array.from({ length: nStudents }, () => new Array(nRoles).fill(false));
  for (let s = 0; s < nStudents; s++) {
    for (let r = 0; r < nRoles; r++) {
      if (studentsSubset[s].fullText.includes(roles[r].req)) {
        adj[s][r] = true;
      }
    }
  }

  function dfs(s, visited) {
    for (let r = 0; r < nRoles; r++) {
      if (adj[s][r] && !visited[r]) {
        visited[r] = true;
        if (matchRoleToStudent[r] < 0 || dfs(matchRoleToStudent[r], visited)) {
          matchRoleToStudent[r] = s;
          return true;
        }
      }
    }
    return false;
  }

  let matchCount = 0;
  for (let s = 0; s < nStudents; s++) {
    const visited = new Array(nRoles).fill(false);
    if (dfs(s, visited)) {
      matchCount++;
    }
  }

  const studentMatches = new Array(nStudents).fill(-1);
  for (let r = 0; r < nRoles; r++) {
    if (matchRoleToStudent[r] !== -1) {
      studentMatches[matchRoleToStudent[r]] = r;
    }
  }

  return { matchCount, matchRoleToStudent, studentMatches, adj };
}

// --- Dinic's Max Flow Algorithm ---
class DinicEdge {
  constructor(to, cap, rev) {
    this.to = to;
    this.cap = cap;
    this.flow = 0;
    this.rev = rev;
  }
}

class DinicMaxFlow {
  constructor(n) {
    this.n = n;
    this.adj = Array.from({ length: n }, () => []);
    this.level = new Array(n).fill(-1);
    this.ptr = new Array(n).fill(0);
  }

  addEdge(from, to, cap) {
    const a = new DinicEdge(to, cap, this.adj[to].length);
    const b = new DinicEdge(from, 0, this.adj[from].length);
    this.adj[from].push(a);
    this.adj[to].push(b);
  }

  bfs(s, t) {
    this.level.fill(-1);
    this.level[s] = 0;
    const q = [s];
    while (q.length > 0) {
      const v = q.shift();
      for (let edge of this.adj[v]) {
        if (edge.cap - edge.flow > 0 && this.level[edge.to] === -1) {
          this.level[edge.to] = this.level[v] + 1;
          q.push(edge.to);
        }
      }
    }
    return this.level[t] !== -1;
  }

  dfs(v, t, pushed) {
    if (pushed === 0) return 0;
    if (v === t) return pushed;
    for (let cid = this.ptr[v]; cid < this.adj[v].length; cid = ++this.ptr[v]) {
      const edge = this.adj[v][cid];
      const tr = edge.to;
      if (this.level[v] + 1 !== this.level[tr] || edge.cap - edge.flow === 0) continue;
      const trPushed = this.dfs(tr, t, Math.min(pushed, edge.cap - edge.flow));
      if (trPushed === 0) continue;
      edge.flow += trPushed;
      this.adj[tr][edge.rev].flow -= trPushed;
      return trPushed;
    }
    return 0;
  }

  computeMaxFlow(s, t) {
    let flow = 0;
    while (this.bfs(s, t)) {
      this.ptr.fill(0);
      while (true) {
        const pushed = this.dfs(s, t, Infinity);
        if (pushed === 0) break;
        flow += pushed;
      }
    }
    return flow;
  }
}

// ============================================================================
// 3. UI RENDERING & EVENT HANDLERS
// ============================================================================

// Switch Navigation Tabs
function showTab(tabId) {
  document.querySelectorAll('.tab-content').forEach(el => el.classList.add('hidden'));
  document.getElementById(`tab-${tabId}`).classList.remove('hidden');

  document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.classList.remove('bg-blue-600', 'text-white');
    btn.classList.add('text-slate-300', 'hover:bg-slate-800');
  });
  const activeBtn = document.getElementById(`btn-${tabId}`);
  if (activeBtn) {
    activeBtn.classList.remove('text-slate-300', 'hover:bg-slate-800');
    activeBtn.classList.add('bg-blue-600', 'text-white');
  }

  if (tabId === 'allocation') {
    runPlacementAllocation();
  }
}

// Render Corpus Cards & Table
function renderCorpusTable() {
  const container = document.getElementById('corpus-cards-container');
  if (!container) return;

  const start = (currentPage - 1) * pageSize;
  const paged = filteredStudents.slice(start, start + pageSize);

  container.innerHTML = paged.map(s => `
    <div class="glass-panel p-5 rounded-xl border border-slate-700/60 hover:border-blue-500/50 transition-all flex flex-col justify-between">
      <div>
        <div class="flex items-center justify-between mb-2">
          <span class="text-xs font-mono font-bold px-2 py-0.5 rounded bg-blue-500/10 text-blue-400 border border-blue-500/20">${s.id}</span>
          <span class="text-xs text-slate-400">${s.roll}</span>
        </div>
        <h3 class="text-lg font-bold text-slate-100 mb-1">${s.name}</h3>
        <p class="text-xs text-blue-300 mb-2 font-medium">${s.dept}</p>
        <p class="text-xs text-slate-400 mb-3 line-clamp-2">${s.summary}</p>
        
        <div class="flex flex-wrap gap-1 mb-3">
          ${s.skills.slice(0, 4).map(sk => `<span class="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded-full border border-slate-700">${sk}</span>`).join('')}
          ${s.skills.length > 4 ? `<span class="text-[10px] text-slate-500 px-1 py-0.5">+${s.skills.length - 4} more</span>` : ''}
        </div>
      </div>
      
      <div class="pt-3 border-t border-slate-800 flex items-center justify-between">
        <span class="text-xs text-slate-400">Year ${s.year} • Sec ${s.section}</span>
        <button onclick="viewStudentModal('${s.id}')" class="text-xs font-semibold text-blue-400 hover:text-blue-300 flex items-center gap-1">
          View Profile &rarr;
        </button>
      </div>
    </div>
  `).join('');

  // Pagination UI
  const totalPages = Math.ceil(filteredStudents.length / pageSize) || 1;
  document.getElementById('corpus-page-info').innerText = `Page ${currentPage} of ${totalPages} (${filteredStudents.length} candidates)`;
  document.getElementById('prev-page-btn').disabled = currentPage === 1;
  document.getElementById('next-page-btn').disabled = currentPage === totalPages;
}

function prevPage() {
  if (currentPage > 1) {
    currentPage--;
    renderCorpusTable();
  }
}

function nextPage() {
  const totalPages = Math.ceil(filteredStudents.length / pageSize);
  if (currentPage < totalPages) {
    currentPage++;
    renderCorpusTable();
  }
}

function filterCorpus() {
  const query = document.getElementById('corpus-search-input').value.toLowerCase().trim();
  const dept = document.getElementById('corpus-dept-filter').value;

  filteredStudents = allStudents.filter(s => {
    const matchesQuery = !query || 
      s.name.toLowerCase().includes(query) ||
      s.roll.toLowerCase().includes(query) ||
      s.id.toLowerCase().includes(query) ||
      s.skills.some(sk => sk.toLowerCase().includes(query));
    
    const matchesDept = !dept || s.dept === dept;
    return matchesQuery && matchesDept;
  });

  currentPage = 1;
  renderCorpusTable();
}

// Student Modal
function viewStudentModal(studentId) {
  const student = allStudents.find(s => s.id === studentId);
  if (!student) return;

  document.getElementById('modal-student-name').innerText = student.name;
  document.getElementById('modal-student-id').innerText = `${student.id} (${student.roll})`;
  document.getElementById('modal-student-raw').innerText = student.fullText;
  document.getElementById('student-modal').classList.remove('hidden');
}

function closeStudentModal() {
  document.getElementById('student-modal').classList.add('hidden');
}

// ============================================================================
// 4. STRING SEARCH SUITE TAB
// ============================================================================
function runStringSearch() {
  const query = document.getElementById('search-query-input').value.trim();
  if (!query) return;

  const algo = document.getElementById('search-algo-select').value;
  recordSearchQuery(query);

  const startTime = performance.now();
  let matchesCount = 0;
  const matchedStudents = [];

  allStudents.forEach(s => {
    let positions = [];
    if (algo === 'kmp') {
      positions = kmpSearch(s.fullText, query);
    } else if (algo === 'z') {
      positions = zSearch(s.fullText, query);
    } else if (algo === 'suffix') {
      const sa = buildSuffixArray(s.fullText);
      positions = suffixArraySearch(s.fullText, query, sa);
    }
    if (positions.length > 0) {
      matchesCount++;
      matchedStudents.push({ student: s, positions });
    }
  });

  const duration = (performance.now() - startTime).toFixed(2);

  // Render Metrics
  document.getElementById('search-results-stats').innerHTML = `
    <div class="grid grid-cols-3 gap-4 mb-4">
      <div class="glass-panel p-3 rounded-lg text-center">
        <div class="text-xs text-slate-400">Algorithm</div>
        <div class="text-sm font-bold text-blue-400 uppercase">${algo.toUpperCase()}</div>
      </div>
      <div class="glass-panel p-3 rounded-lg text-center">
        <div class="text-xs text-slate-400">Total Matches</div>
        <div class="text-sm font-bold text-emerald-400">${matchesCount} / ${allStudents.length} Students</div>
      </div>
      <div class="glass-panel p-3 rounded-lg text-center">
        <div class="text-xs text-slate-400">Execution Time</div>
        <div class="text-sm font-bold text-indigo-400">${duration} ms</div>
      </div>
    </div>
  `;

  // Render LPS or Z Array Visualizer
  const vizContainer = document.getElementById('search-algo-viz');
  if (algo === 'kmp') {
    const lps = computeLPS(query);
    vizContainer.innerHTML = `
      <div class="glass-panel p-4 rounded-xl mb-4 border border-blue-500/30">
        <h4 class="text-xs font-bold uppercase tracking-wider text-blue-400 mb-2">KMP LPS Table (Longest Proper Prefix which is Suffix)</h4>
        <div class="flex flex-wrap gap-2">
          ${query.split('').map((ch, idx) => `
            <div class="text-center p-2 rounded bg-slate-800 border border-slate-700 min-w-[36px]">
              <div class="text-xs text-slate-400 font-mono">${idx}</div>
              <div class="text-sm font-bold text-yellow-400 font-mono">${ch}</div>
              <div class="text-xs font-mono font-bold text-emerald-400 mt-1">${lps[idx]}</div>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  } else if (algo === 'z') {
    vizContainer.innerHTML = `
      <div class="glass-panel p-4 rounded-xl mb-4 border border-indigo-500/30">
        <h4 class="text-xs font-bold uppercase tracking-wider text-indigo-400 mb-2">Z-Algorithm Window Matching</h4>
        <p class="text-xs text-slate-400">Linear time substring search over concatenated string <code>${query}$[Text]</code> with prefix-box propagation.</p>
      </div>
    `;
  } else {
    vizContainer.innerHTML = '';
  }

  // Render Result Cards
  const resultsContainer = document.getElementById('search-results-list');
  resultsContainer.innerHTML = matchedStudents.slice(0, 10).map(({ student, positions }) => {
    // Generate Snippet
    const firstPos = positions[0];
    const snippetStart = Math.max(0, firstPos - 40);
    const snippetEnd = Math.min(student.fullText.length, firstPos + query.length + 50);
    let snippet = student.fullText.substring(snippetStart, snippetEnd);
    const regex = new RegExp(`(${query})`, 'gi');
    snippet = snippet.replace(regex, `<mark class="search-highlight">$1</mark>`);

    return `
      <div class="glass-panel p-4 rounded-xl border border-slate-800 hover:border-blue-500/40 transition-all">
        <div class="flex items-center justify-between mb-1">
          <span class="text-sm font-bold text-slate-200">${student.name} <span class="text-xs text-slate-500 font-mono">(${student.id})</span></span>
          <span class="text-xs font-mono px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">${positions.length} Occurrence(s)</span>
        </div>
        <p class="text-xs text-slate-400 mb-2">${student.dept}</p>
        <div class="text-xs font-mono bg-slate-900/80 p-2.5 rounded-lg border border-slate-800 text-slate-300">
          ...${snippet}...
        </div>
      </div>
    `;
  }).join('');
}

// ============================================================================
// 5. AHO-CORASICK MULTI-PATTERN SCREENER TAB
// ============================================================================
function runAhoCorasickScreening() {
  const input = document.getElementById('aho-keywords-input').value.trim();
  if (!input) return;

  const keywords = input.split(',').map(s => s.trim()).filter(Boolean);
  if (keywords.length === 0) return;

  const ac = new AhoCorasick(keywords);
  const startTime = performance.now();

  const screened = allStudents.map(student => {
    const freqs = ac.search(student.fullText);
    const matchedCount = Object.keys(freqs).length;
    const matchRatio = Math.round((matchedCount / keywords.length) * 100);
    return { student, freqs, matchedCount, matchRatio };
  });

  screened.sort((a, b) => b.matchRatio - a.matchRatio || b.matchedCount - a.matchedCount);
  const duration = (performance.now() - startTime).toFixed(2);

  document.getElementById('aho-stats').innerHTML = `
    <div class="text-xs text-slate-400">Screened <span class="text-slate-100 font-bold">${allStudents.length}</span> resumes across <span class="text-slate-100 font-bold">${keywords.length}</span> target skills in <span class="text-emerald-400 font-bold">${duration} ms</span>.</div>
  `;

  const container = document.getElementById('aho-results-container');
  container.innerHTML = screened.slice(0, 12).map(({ student, freqs, matchRatio }) => `
    <div class="glass-panel p-4 rounded-xl border border-slate-800 flex flex-col justify-between">
      <div>
        <div class="flex items-center justify-between mb-1">
          <span class="text-xs font-mono text-blue-400 font-bold">${student.id}</span>
          <span class="text-xs font-bold px-2 py-0.5 rounded-full ${matchRatio >= 80 ? 'bg-emerald-500/20 text-emerald-400' : matchRatio >= 50 ? 'bg-yellow-500/20 text-yellow-400' : 'bg-slate-700 text-slate-300'}">
            ${matchRatio}% Match
          </span>
        </div>
        <h4 class="text-base font-bold text-slate-100">${student.name}</h4>
        <p class="text-xs text-slate-400 mb-3">${student.dept}</p>
        
        <div class="space-y-1">
          ${keywords.map(kw => {
            const found = freqs[kw.toLowerCase()] || 0;
            return `
              <div class="flex items-center justify-between text-xs py-0.5">
                <span class="${found > 0 ? 'text-slate-200 font-semibold' : 'text-slate-500'}">${kw}</span>
                <span class="font-mono text-xs ${found > 0 ? 'text-emerald-400 font-bold' : 'text-slate-600'}">${found > 0 ? `✓ (${found})` : '✗'}</span>
              </div>
            `;
          }).join('')}
        </div>
      </div>
    </div>
  `).join('');
}

// ============================================================================
// 6. EDIT DISTANCE (FUZZY SEARCH & DP VISUALIZER)
// ============================================================================
function runEditDistanceVisualizer() {
  const query = document.getElementById('fuzzy-query-input').value.trim();
  if (!query) return;

  const suggestions = findFuzzySuggestions(query, Array.from(vocabularySet), 3);
  const targetWord = suggestions.length > 0 ? suggestions[0].word : 'JavaScript';

  const { distance, matrix } = computeLevenshtein(query, targetWord);

  // Render Suggestions
  document.getElementById('fuzzy-suggestions-list').innerHTML = `
    <div class="text-xs text-slate-400 mb-2">Did you mean?</div>
    <div class="flex flex-wrap gap-2">
      ${suggestions.map(s => `
        <button onclick="applyFuzzySuggestion('${s.word}')" class="px-3 py-1 rounded-lg bg-blue-600/20 text-blue-400 border border-blue-500/30 text-xs font-semibold hover:bg-blue-600/30 transition-all flex items-center gap-1.5">
          <span>${s.word}</span>
          <span class="text-[10px] px-1.5 py-0.2 rounded-full bg-blue-500/30 text-blue-300">dist: ${s.distance}</span>
        </button>
      `).join('')}
      ${suggestions.length === 0 ? '<span class="text-xs text-slate-500">No suggestions within distance 3</span>' : ''}
    </div>
  `;

  // Render DP Matrix Grid
  const gridContainer = document.getElementById('dp-matrix-container');
  let tableHtml = `<div class="overflow-x-auto"><table class="border-collapse text-center font-mono text-xs">`;
  
  // Header row
  tableHtml += `<tr><th class="p-2 border border-slate-700 bg-slate-900 text-slate-400">ε</th><th class="p-2 border border-slate-700 bg-slate-900 text-slate-400">ε</th>`;
  for (let j = 0; j < targetWord.length; j++) {
    tableHtml += `<th class="p-2 border border-slate-700 bg-slate-900 text-yellow-400">${targetWord[j]}</th>`;
  }
  tableHtml += `</tr>`;

  // Rows
  for (let i = 0; i <= query.length; i++) {
    tableHtml += `<tr>`;
    const rowChar = i === 0 ? 'ε' : query[i - 1];
    tableHtml += `<th class="p-2 border border-slate-700 bg-slate-900 text-blue-400">${rowChar}</th>`;
    for (let j = 0; j <= targetWord.length; j++) {
      const isTarget = i === query.length && j === targetWord.length;
      tableHtml += `<td class="p-2 border border-slate-800 ${isTarget ? 'bg-emerald-600/30 font-bold text-emerald-400' : 'bg-slate-900/50 text-slate-300'} dp-cell">${matrix[i][j]}</td>`;
    }
    tableHtml += `</tr>`;
  }
  tableHtml += `</table></div>`;

  gridContainer.innerHTML = tableHtml;
  document.getElementById('dp-calc-summary').innerText = `Levenshtein distance between "${query}" and "${targetWord}" is ${distance}.`;
}

function applyFuzzySuggestion(word) {
  document.getElementById('fuzzy-query-input').value = word;
  runEditDistanceVisualizer();
}

// ============================================================================
// 7. PLACEMENT ALLOCATION & NETWORK FLOW SIMULATOR TAB
// ============================================================================
function renderAllocationRoles() {
  const container = document.getElementById('allocation-roles-config');
  if (!container) return;
  container.innerHTML = corporateRoles.map((role, idx) => `
    <div class="glass-panel p-3 rounded-lg border border-slate-800 flex items-center justify-between text-xs">
      <div>
        <div class="font-bold text-slate-200">${role.name}</div>
        <div class="text-slate-400 text-[11px]">Requirement: <span class="text-blue-400 font-semibold">${role.req}</span></div>
      </div>
      <div class="flex items-center gap-2">
        <label class="text-[11px] text-slate-400">Quota:</label>
        <input type="number" min="1" max="10" value="${role.quota}" onchange="updateRoleQuota(${idx}, this.value)" class="w-12 bg-slate-900 border border-slate-700 rounded px-1.5 py-0.5 text-center text-xs font-mono font-bold text-emerald-400">
      </div>
    </div>
  `).join('');
}

function updateRoleQuota(index, val) {
  corporateRoles[index].quota = parseInt(val) || 1;
  runPlacementAllocation();
}

function runPlacementAllocation() {
  const sampleStudents = allStudents.slice(0, 10);
  const algo = document.getElementById('allocation-algo-select').value;

  let allocatedCount = 0;
  let matches = [];

  if (algo === 'bipartite') {
    const res = runBipartiteMatching(sampleStudents, corporateRoles);
    allocatedCount = res.matchCount;
    sampleStudents.forEach((stud, sIdx) => {
      const rIdx = res.studentMatches[sIdx];
      if (rIdx !== -1) {
        matches.push({ student: stud, role: corporateRoles[rIdx] });
      }
    });
  } else {
    // Dinic Max Flow Setup
    const nS = sampleStudents.length;
    const nR = corporateRoles.length;
    const totalNodes = 1 + nS + nR + 1;
    const source = 0;
    const sink = totalNodes - 1;
    const dinic = new DinicMaxFlow(totalNodes);

    // Source -> Students
    for (let i = 0; i < nS; i++) dinic.addEdge(source, 1 + i, 1);

    // Students -> Roles
    for (let i = 0; i < nS; i++) {
      for (let j = 0; j < nR; j++) {
        if (sampleStudents[i].fullText.includes(corporateRoles[j].req)) {
          dinic.addEdge(1 + i, 1 + nS + j, 1);
        }
      }
    }

    // Roles -> Sink
    for (let j = 0; j < nR; j++) {
      dinic.addEdge(1 + nS + j, sink, corporateRoles[j].quota);
    }

    allocatedCount = dinic.computeMaxFlow(source, sink);

    // Extract assignments
    for (let i = 0; i < nS; i++) {
      const edges = dinic.adj[1 + i];
      for (let edge of edges) {
        if (edge.to >= 1 + nS && edge.to < sink && edge.flow > 0) {
          const rIdx = edge.to - (1 + nS);
          matches.push({ student: sampleStudents[i], role: corporateRoles[rIdx] });
          break;
        }
      }
    }
  }

  // Render Allocation Metrics
  const totalQuotas = corporateRoles.reduce((acc, r) => acc + r.quota, 0);
  document.getElementById('allocation-stats-badge').innerHTML = `
    <span class="text-xs px-3 py-1 rounded-full font-bold ${allocatedCount >= totalQuotas ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' : 'bg-blue-500/20 text-blue-400 border border-blue-500/30'}">
      ${allocatedCount} / ${totalQuotas} Vacancies Saturated (${Math.round((allocatedCount / totalQuotas) * 100)}%)
    </span>
  `;

  // Render Visual Assignment Cards
  const canvasContainer = document.getElementById('allocation-results-list');
  canvasContainer.innerHTML = matches.map(m => `
    <div class="glass-panel p-3.5 rounded-xl border border-slate-800 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 rounded-full bg-blue-600/20 text-blue-400 flex items-center justify-center font-bold text-xs">
          ${m.student.name.charAt(0)}
        </div>
        <div>
          <div class="text-sm font-bold text-slate-200">${m.student.name}</div>
          <div class="text-[11px] text-slate-400">${m.student.roll} • ${m.student.dept}</div>
        </div>
      </div>
      <div class="text-right">
        <div class="text-xs font-bold text-emerald-400">Matched &rarr; ${m.role.name}</div>
        <div class="text-[10px] text-slate-500">Skill: ${m.role.req}</div>
      </div>
    </div>
  `).join('');
}

// ============================================================================
// 8. SEARCH HISTORY & ANALYTICS
// ============================================================================
function recordSearchQuery(query) {
  if (!query) return;
  const q = query.trim();
  queryFrequencies[q] = (queryFrequencies[q] || 0) + 1;

  searchHistory = searchHistory.filter(item => item !== q);
  searchHistory.unshift(q);
  if (searchHistory.length > 20) searchHistory.pop();

  updateHistoryUI();
}

function updateHistoryUI() {
  const container = document.getElementById('search-history-chips');
  if (!container) return;

  container.innerHTML = searchHistory.slice(0, 8).map(q => `
    <button onclick="quickSearch('${q}')" class="text-xs bg-slate-800/80 hover:bg-slate-700 text-slate-300 px-3 py-1 rounded-full border border-slate-700 flex items-center gap-1.5 transition-all">
      <span>${q}</span>
      <span class="text-[10px] text-blue-400 font-bold">(${queryFrequencies[q]})</span>
    </button>
  `).join('');

  renderHistoryChart();
}

function quickSearch(q) {
  document.getElementById('search-query-input').value = q;
  showTab('search');
  runStringSearch();
}

let historyChartInstance = null;
function renderHistoryChart() {
  const ctx = document.getElementById('historyChart');
  if (!ctx) return;

  const labels = Object.keys(queryFrequencies).slice(0, 6);
  const data = labels.map(k => queryFrequencies[k]);

  if (historyChartInstance) {
    historyChartInstance.destroy();
  }

  historyChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels.length ? labels : ['Distributed Systems', 'Machine Learning', 'Python', 'Java', 'Docker'],
      datasets: [{
        label: 'Search Frequency',
        data: data.length ? data : [12, 19, 8, 15, 6],
        backgroundColor: 'rgba(59, 130, 246, 0.7)',
        borderColor: '#3b82f6',
        borderRadius: 4
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: { beginAtZero: true, grid: { color: 'rgba(255, 255, 255, 0.05)' } },
        x: { grid: { display: false } }
      }
    }
  });
}

// ============================================================================
// 9. BENCHMARK COMPARISON TAB
// ============================================================================
let benchmarkChartInstance = null;
function runAlgorithmBenchmark() {
  const query = document.getElementById('benchmark-query-input').value.trim() || 'Distributed Systems';
  
  // 1. KMP Benchmark
  const t0 = performance.now();
  let kmpHits = 0;
  for (let s of allStudents) {
    if (kmpSearch(s.fullText, query).length > 0) kmpHits++;
  }
  const kmpTime = (performance.now() - t0).toFixed(2);

  // 2. Z-Algorithm Benchmark
  const t1 = performance.now();
  let zHits = 0;
  for (let s of allStudents) {
    if (zSearch(s.fullText, query).length > 0) zHits++;
  }
  const zTime = (performance.now() - t1).toFixed(2);

  // 3. Naive Substring Benchmark (includes)
  const t2 = performance.now();
  let naiveHits = 0;
  for (let s of allStudents) {
    if (s.fullText.toLowerCase().includes(query.toLowerCase())) naiveHits++;
  }
  const naiveTime = (performance.now() - t2).toFixed(2);

  document.getElementById('benchmark-stats-panel').innerHTML = `
    <div class="grid grid-cols-3 gap-4">
      <div class="glass-panel p-4 rounded-xl text-center border border-blue-500/30">
        <div class="text-xs text-slate-400">KMP Algorithm</div>
        <div class="text-xl font-bold text-blue-400">${kmpTime} ms</div>
        <div class="text-[11px] text-slate-500">Hits: ${kmpHits} • O(N+M)</div>
      </div>
      <div class="glass-panel p-4 rounded-xl text-center border border-indigo-500/30">
        <div class="text-xs text-slate-400">Z-Algorithm</div>
        <div class="text-xl font-bold text-indigo-400">${zTime} ms</div>
        <div class="text-[11px] text-slate-500">Hits: ${zHits} • O(N+M)</div>
      </div>
      <div class="glass-panel p-4 rounded-xl text-center border border-slate-700">
        <div class="text-xs text-slate-400">Naive String Scan</div>
        <div class="text-xl font-bold text-slate-300">${naiveTime} ms</div>
        <div class="text-[11px] text-slate-500">Hits: ${naiveHits} • O(N·M)</div>
      </div>
    </div>
  `;

  // Render Bar Chart
  const ctx = document.getElementById('benchmarkChart');
  if (benchmarkChartInstance) benchmarkChartInstance.destroy();

  benchmarkChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['KMP Algorithm', 'Z-Algorithm', 'Naive Brute Force'],
      datasets: [{
        label: 'Execution Latency (ms)',
        data: [kmpTime, zTime, naiveTime],
        backgroundColor: ['rgba(59, 130, 246, 0.8)', 'rgba(99, 102, 241, 0.8)', 'rgba(148, 163, 184, 0.5)'],
        borderRadius: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: { beginAtZero: true, title: { display: true, text: 'Milliseconds (lower is better)' }, grid: { color: 'rgba(255, 255, 255, 0.05)' } },
        x: { grid: { display: false } }
      }
    }
  });
}

// Window load trigger
window.addEventListener('DOMContentLoaded', () => {
  initData();
});
