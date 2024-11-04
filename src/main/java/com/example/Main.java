package com.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        try {
            // Create an HTTP server that listens on port 8081
            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
            server.createContext("/healthz", new HealthCheckHandler()); // Health check endpoint
            server.createContext("/ready", new ReadinessCheckHandler()); // Readiness endpoint
            server.setExecutor(null); // Creates a default executor
            server.start();
            System.out.println("Server is running on port 8081...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handler for health check
    static class HealthCheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Healthy";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Handler for readiness check
    static class ReadinessCheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Ready";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
