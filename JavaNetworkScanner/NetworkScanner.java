import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.file.Files;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class NetworkScanner {

    static List<String[]> lastScanResults = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Serve index.html
        server.createContext("/", exchange -> {
            File file = new File("public/index.html");
            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().add("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        // /scan endpoint
        server.createContext("/scan", new ScanHandler());

        // /download endpoint
        server.createContext("/download", exchange -> {
            StringBuilder csv = new StringBuilder("IP,Status,Hostname,Latency(ms)\n");
            synchronized (lastScanResults) {
                for (String[] row : lastScanResults) {
                    csv.append(String.join(",", row)).append("\n");
                }
            }
            byte[] bytes = csv.toString().getBytes();
            exchange.getResponseHeaders().add("Content-Type", "text/csv");
            exchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"scan_results.csv\"");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("✅ Server started at http://localhost:8080");
    }

    static class ScanHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQuery(query);

            String subnet = params.get("subnet");
            int start = Integer.parseInt(params.get("start"));
            int end = Integer.parseInt(params.get("end"));

            ExecutorService pool = Executors.newFixedThreadPool(40);
            List<Future<String>> futures = new ArrayList<>();
            lastScanResults.clear();
            List<String> jsonItems = Collections.synchronizedList(new ArrayList<>());

            for (int i = start; i <= end; i++) {
                String ip = subnet + "." + i;
                futures.add(pool.submit(() -> {
                    try {
                        InetAddress address = InetAddress.getByName(ip);
                        long startTime = System.currentTimeMillis();
                        boolean reachable = address.isReachable(500);
                        long latency = System.currentTimeMillis() - startTime;
                        String hostname = address.getCanonicalHostName();
                        String status = reachable ? "Online" : "Offline";
                        String[] row = new String[]{ip, status, hostname, String.valueOf(latency)};
                        lastScanResults.add(row);
                        return String.format("{\"ip\":\"%s\",\"status\":\"%s\",\"hostname\":\"%s\",\"latency\":%d}",
                                ip, status, hostname, latency);
                    } catch (IOException e) {
                        return String.format("{\"ip\":\"%s\",\"status\":\"Error\",\"hostname\":\"N/A\",\"latency\":-1}", ip);
                    }
                }));
            }

            pool.shutdown();

            int online = 0, offline = 0, totalLatency = 0;
            for (Future<String> f : futures) {
                try {
                    String json = f.get();
                    jsonItems.add(json);
                    if (json.contains("\"status\":\"Online\"")) online++;
                    else if (json.contains("\"status\":\"Offline\"")) offline++;

                    String latencyStr = json.split("\"latency\":")[1].replace("}", "").trim();
                    int latency = Integer.parseInt(latencyStr);
                    if (latency > 0) totalLatency += latency;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int total = online + offline;
            int avgLatency = total > 0 ? totalLatency / total : 0;

            String jsonResponse = "{ \"online\": " + online +
                    ", \"offline\": " + offline +
                    ", \"avgLatency\": " + avgLatency +
                    ", \"results\": [" + String.join(",", jsonItems) + "] }";

            byte[] responseBytes = jsonResponse.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }

        private Map<String, String> parseQuery(String query) {
            Map<String, String> map = new HashMap<>();
            if (query == null) return map;
            for (String pair : query.split("&")) {
                String[] parts = pair.split("=");
                if (parts.length == 2) map.put(parts[0], parts[1]);
            }
            return map;
        }
    }
}
