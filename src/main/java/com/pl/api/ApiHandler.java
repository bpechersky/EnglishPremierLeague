package com.pl.api;

import com.pl.service.LeagueService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ApiHandler implements HttpHandler {

    private final LeagueService svc = LeagueService.getInstance();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        // CORS headers
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            sendResponse(ex, 200, "{}");
            return;
        }

        String path = ex.getRequestURI().getPath();

        try {
            if (path.startsWith("/api/team/")) {
                String abbr = path.substring("/api/team/".length()).trim().toUpperCase();
                String json = svc.getTeamDetailJson(abbr);
                if (json == null) sendResponse(ex, 404, "{\"error\":\"Team not found\"}");
                else sendResponse(ex, 200, json);
            } else switch (path) {
                case "/api/standings" -> sendResponse(ex, 200, svc.getStandingsJson());
                case "/api/results"   -> sendResponse(ex, 200, svc.getResultsJson());
                case "/api/fixtures"  -> sendResponse(ex, 200, svc.getFixturesJson());
                case "/api/teams"     -> sendResponse(ex, 200, svc.getTeamsJson());
                case "/api/result"    -> handleAddResult(ex);
                case "/api/team"      -> handleAddTeam(ex);
                default               -> sendResponse(ex, 404, "{\"error\":\"Not found\"}");
            }
        } catch (Exception e) {
            sendResponse(ex, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleAddResult(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            sendResponse(ex, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseJson(body);

        String home  = params.getOrDefault("home", "").trim().toUpperCase();
        String away  = params.getOrDefault("away", "").trim().toUpperCase();
        String hsStr = params.getOrDefault("homeScore", "");
        String asStr = params.getOrDefault("awayScore", "");
        String date  = params.getOrDefault("date", "").trim();

        if (home.isEmpty() || away.isEmpty() || hsStr.isEmpty() || asStr.isEmpty() || date.isEmpty()) {
            sendResponse(ex, 400, "{\"error\":\"Missing required fields\"}");
            return;
        }

        int homeScore, awayScore;
        try {
            homeScore = Integer.parseInt(hsStr.trim());
            awayScore = Integer.parseInt(asStr.trim());
        } catch (NumberFormatException e) {
            sendResponse(ex, 400, "{\"error\":\"Invalid score values\"}");
            return;
        }

        String err = svc.addResult(home, away, homeScore, awayScore, date);
        if (err != null) {
            sendResponse(ex, 400, "{\"error\":\"" + err + "\"}");
            return;
        }

        // Return updated standings + results in one response
        sendResponse(ex, 200, String.format(
            "{\"success\":true,\"standings\":%s,\"results\":%s,\"fixtures\":%s}",
            svc.getStandingsJson(), svc.getResultsJson(), svc.getFixturesJson()
        ));
    }

    private void handleAddTeam(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            sendResponse(ex, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseJson(body);

        String name     = params.getOrDefault("name", "").trim();
        String abbr     = params.getOrDefault("abbr", "").trim().toUpperCase();
        String primary  = params.getOrDefault("primaryColor", "").trim();
        String secondary= params.getOrDefault("secondaryColor", "").trim();

        if (name.isEmpty() || abbr.isEmpty()) {
            sendResponse(ex, 400, "{\"error\":\"Name and abbreviation are required\"}");
            return;
        }

        String err = svc.addTeam(name, abbr, primary, secondary);
        if (err != null) {
            sendResponse(ex, 400, "{\"error\":\"" + err + "\"}");
            return;
        }

        sendResponse(ex, 200, String.format(
            "{\"success\":true,\"standings\":%s,\"teams\":%s}",
            svc.getStandingsJson(), svc.getTeamsJson()
        ));
    }

    /** Very simple JSON key-value parser for flat objects */
    private Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}"))   json = json.substring(0, json.length() - 1);

        // Split on commas that are not inside quotes
        String[] entries = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String entry : entries) {
            String[] kv = entry.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String k = kv[0].trim().replaceAll("^\"|\"$", "");
                String v = kv[1].trim().replaceAll("^\"|\"$", "");
                map.put(k, v);
            }
        }
        return map;
    }

    private void sendResponse(HttpExchange ex, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }
}
