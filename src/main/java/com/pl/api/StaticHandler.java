package com.pl.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticHandler implements HttpHandler {

    private final String staticDir;

    public StaticHandler(String staticDir) {
        this.staticDir = staticDir;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String uriPath = ex.getRequestURI().getPath();

        // Default to index.html
        if (uriPath.equals("/") || uriPath.isEmpty()) {
            uriPath = "/index.html";
        }

        // Prevent path traversal
        if (uriPath.contains("..")) {
            send(ex, 403, "text/plain", "Forbidden".getBytes());
            return;
        }

        Path file = Paths.get(staticDir, uriPath.substring(1));

        if (!Files.exists(file) || Files.isDirectory(file)) {
            // Fall back to index.html for SPA routing
            file = Paths.get(staticDir, "index.html");
        }

        String mime = getMime(file.toString());
        byte[] bytes = Files.readAllBytes(file);
        ex.getResponseHeaders().add("Content-Type", mime);
        send(ex, 200, mime, bytes);
    }

    private void send(HttpExchange ex, int code, String mime, byte[] bytes) throws IOException {
        ex.getResponseHeaders().set("Content-Type", mime);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    private String getMime(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".js"))   return "application/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".svg"))  return "image/svg+xml";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".ico"))  return "image/x-icon";
        return "application/octet-stream";
    }
}
