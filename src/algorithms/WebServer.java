package algorithms;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Lightweight Zero-Dependency Embedded HTTP Server & REST API for Placement Hub.
 * Serves the web dashboard at http://localhost:8080 and provides live algorithmic endpoints.
 */
public class WebServer {

    private static final int PORT = 8080;
    private static List<CorpusLoader.StudentRecord> records = new ArrayList<>();
    private static final SearchHistory searchHistory = new SearchHistory(50);
    private static final Set<String> vocabulary = new HashSet<>();

    public static void main(String[] args) throws IOException {
        System.out.println("================================================================================");
        System.out.println("               CAMPUS PLACEMENT HUB - WEB SERVER & API                         ");
        System.out.println("================================================================================");

        // Load 500 corpus records
        String corpusPath = "corpus/student-records";
        System.out.print("[+] Ingesting Student Records from: " + corpusPath + "... ");
        records = CorpusLoader.loadStudentRecords(corpusPath);
        System.out.println("Loaded " + records.size() + " profiles.");

        // Build vocabulary
        for (CorpusLoader.StudentRecord r : records) {
            vocabulary.addAll(r.getSkills());
            vocabulary.addAll(r.getProgrammingLanguages());
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Static Web Files Handler
        server.createContext("/", new StaticFileHandler());

        // REST API Handlers
        server.createContext("/api/status", new StatusHandler());
        server.createContext("/api/search", new SearchHandler());
        server.createContext("/api/fuzzy", new FuzzyHandler());
        server.createContext("/api/history", new HistoryHandler());

        server.setExecutor(null); // Default executor
        server.start();

        System.out.println("\n🚀 [SUCCESS] Placement Hub Web Dashboard is live!");
        System.out.println("👉 Open in your web browser: http://localhost:" + PORT + "/\n");
        System.out.println("Press Ctrl+C to terminate the server.");
        System.out.println("================================================================================");
    }

    // Static File Server
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            File file = new File("web" + path);
            if (!file.exists() || file.isDirectory()) {
                String response = "404 Not Found: " + path;
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            String contentType = "text/plain";
            if (path.endsWith(".html")) contentType = "text/html; charset=UTF-8";
            else if (path.endsWith(".css")) contentType = "text/css; charset=UTF-8";
            else if (path.endsWith(".js")) contentType = "application/javascript; charset=UTF-8";
            else if (path.endsWith(".json")) contentType = "application/json; charset=UTF-8";
            else if (path.endsWith(".txt")) contentType = "text/plain; charset=UTF-8";

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, file.length());

            OutputStream os = exchange.getResponseBody();
            Files.copy(file.toPath(), os);
            os.close();
        }
    }

    // API: System Status
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = String.format("{\"status\":\"ok\",\"corpusCount\":%d,\"port\":%d}", records.size(), PORT);
            sendJsonResponse(exchange, json);
        }
    }

    // API: String Search (KMP / ZAlgorithm)
    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String query = params.getOrDefault("q", "").trim();
            String algo = params.getOrDefault("algo", "kmp").toLowerCase();

            if (query.isEmpty()) {
                sendJsonResponse(exchange, "{\"matches\":[],\"totalHits\":0}");
                return;
            }

            searchHistory.recordSearch(query);

            long startTime = System.nanoTime();
            int hitCount = 0;
            StringBuilder matchesJson = new StringBuilder("[");

            boolean first = true;
            for (CorpusLoader.StudentRecord r : records) {
                List<Integer> positions;
                if (algo.equals("z")) {
                    positions = ZAlgorithm.search(r.getFullText(), query);
                } else {
                    positions = KMP.search(r.getFullText(), query);
                }

                if (!positions.isEmpty()) {
                    hitCount++;
                    if (!first) matchesJson.append(",");
                    matchesJson.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"dept\":\"%s\",\"count\":%d}",
                            r.getStudentId(), r.getName(), r.getDepartment(), positions.size()));
                    first = false;
                }
            }
            matchesJson.append("]");

            double elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0;
            String json = String.format("{\"query\":\"%s\",\"algorithm\":\"%s\",\"totalHits\":%d,\"timeMs\":%.2f,\"matches\":%s}",
                    query, algo, hitCount, elapsedMs, matchesJson.toString());

            sendJsonResponse(exchange, json);
        }
    }

    // API: Fuzzy Autocorrect (Edit Distance)
    static class FuzzyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String query = params.getOrDefault("q", "").trim();

            List<String> suggestions = EditDistance.findClosestMatches(query, vocabulary, 2);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < suggestions.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(suggestions.get(i)).append("\"");
            }
            sb.append("]");

            sendJsonResponse(exchange, String.format("{\"query\":\"%s\",\"suggestions\":%s}", query, sb.toString()));
        }
    }

    // API: Search History
    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<String> recent = searchHistory.getRecentSearches(10);
            Map<String, Integer> popular = searchHistory.getPopularQueries(5);

            StringBuilder recJson = new StringBuilder("[");
            for (int i = 0; i < recent.size(); i++) {
                if (i > 0) recJson.append(",");
                recJson.append("\"").append(recent.get(i)).append("\"");
            }
            recJson.append("]");

            StringBuilder popJson = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Integer> e : popular.entrySet()) {
                if (!first) popJson.append(",");
                popJson.append("\"").append(e.getKey()).append("\":").append(e.getValue());
                first = false;
            }
            popJson.append("}");

            sendJsonResponse(exchange, String.format("{\"recent\":%s,\"popular\":%s}", recJson.toString(), popJson.toString()));
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                           URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
            } else {
                result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8), "");
            }
        }
        return result;
    }
}
