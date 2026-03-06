package com.pl.service;

import com.pl.model.Fixture;
import com.pl.model.MatchResult;
import com.pl.model.Team;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Simple JSON file-based persistence for league data.
 * Saves teams, results, and fixtures to a "data/" directory
 * so they survive application restarts and rebuilds.
 */
public class DataStore {

    private static final String DATA_DIR;
    static {
        String env = System.getenv("DATA_DIR");
        DATA_DIR = (env != null && !env.isEmpty()) ? env : "data";
    }
    private static final String TEAMS_FILE = DATA_DIR + "/teams.json";
    private static final String RESULTS_FILE = DATA_DIR + "/results.json";
    private static final String FIXTURES_FILE = DATA_DIR + "/fixtures.json";

    /** Ensures the data directory exists */
    public static void init() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }
    }

    /** Returns true if saved data files exist */
    public static boolean hasSavedData() {
        return Files.exists(Paths.get(TEAMS_FILE));
    }

    // ─── SAVE ───────────────────────────────────────────────

    public static void saveTeams(Map<String, Team> teams) {
        StringBuilder sb = new StringBuilder("[\n");
        boolean first = true;
        for (Team t : teams.values()) {
            if (!first) sb.append(",\n");
            sb.append(String.format(
                "  {\"name\":\"%s\",\"abbr\":\"%s\",\"primaryColor\":\"%s\",\"secondaryColor\":\"%s\"," +
                "\"w\":%d,\"d\":%d,\"l\":%d,\"gf\":%d,\"ga\":%d}",
                escapeJson(t.getName()), t.getAbbr(), t.getPrimaryColor(), t.getSecondaryColor(),
                t.getWins(), t.getDraws(), t.getLosses(), t.getGoalsFor(), t.getGoalsAgainst()
            ));
            first = false;
        }
        sb.append("\n]");
        writeFile(TEAMS_FILE, sb.toString());
    }

    public static void saveResults(List<MatchResult> results) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < results.size(); i++) {
            MatchResult r = results.get(i);
            if (i > 0) sb.append(",\n");
            sb.append(String.format(
                "  {\"home\":\"%s\",\"away\":\"%s\",\"hs\":%d,\"as\":%d,\"date\":\"%s\"}",
                r.getHomeAbbr(), r.getAwayAbbr(), r.getHomeScore(), r.getAwayScore(),
                escapeJson(r.getDate())
            ));
        }
        sb.append("\n]");
        writeFile(RESULTS_FILE, sb.toString());
    }

    public static void saveFixtures(List<Fixture> fixtures) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < fixtures.size(); i++) {
            Fixture f = fixtures.get(i);
            if (i > 0) sb.append(",\n");
            sb.append(String.format(
                "  {\"home\":\"%s\",\"away\":\"%s\",\"date\":\"%s\",\"time\":\"%s\"," +
                "\"probH\":%.1f,\"probA\":%.1f,\"probD\":%.1f}",
                f.getHomeAbbr(), f.getAwayAbbr(), f.getDate(), f.getTime(),
                f.getProbH(), f.getProbA(), f.getProbD()
            ));
        }
        sb.append("\n]");
        writeFile(FIXTURES_FILE, sb.toString());
    }

    public static void saveAll(Map<String, Team> teams, List<MatchResult> results, List<Fixture> fixtures) {
        saveTeams(teams);
        saveResults(results);
        saveFixtures(fixtures);
    }

    // ─── LOAD ───────────────────────────────────────────────

    public static Map<String, Team> loadTeams() {
        Map<String, Team> teams = new LinkedHashMap<>();
        String json = readFile(TEAMS_FILE);
        if (json == null) return teams;

        for (Map<String, String> obj : parseJsonArray(json)) {
            String abbr = obj.getOrDefault("abbr", "");
            if (!abbr.isEmpty()) {
                teams.put(abbr, new Team(
                    obj.getOrDefault("name", ""),
                    abbr,
                    obj.getOrDefault("primaryColor", "#888888"),
                    obj.getOrDefault("secondaryColor", "#333333"),
                    parseInt(obj.get("w")),
                    parseInt(obj.get("d")),
                    parseInt(obj.get("l")),
                    parseInt(obj.get("gf")),
                    parseInt(obj.get("ga"))
                ));
            }
        }
        return teams;
    }

    public static List<MatchResult> loadResults() {
        List<MatchResult> results = new ArrayList<>();
        String json = readFile(RESULTS_FILE);
        if (json == null) return results;

        for (Map<String, String> obj : parseJsonArray(json)) {
            results.add(new MatchResult(
                obj.getOrDefault("home", ""),
                obj.getOrDefault("away", ""),
                parseInt(obj.get("hs")),
                parseInt(obj.get("as")),
                obj.getOrDefault("date", "")
            ));
        }
        return results;
    }

    public static List<Fixture> loadFixtures() {
        List<Fixture> fixtures = new ArrayList<>();
        String json = readFile(FIXTURES_FILE);
        if (json == null) return fixtures;

        for (Map<String, String> obj : parseJsonArray(json)) {
            fixtures.add(new Fixture(
                obj.getOrDefault("home", ""),
                obj.getOrDefault("away", ""),
                obj.getOrDefault("date", ""),
                obj.getOrDefault("time", ""),
                parseDouble(obj.get("probH")),
                parseDouble(obj.get("probA")),
                parseDouble(obj.get("probD"))
            ));
        }
        return fixtures;
    }

    // ─── HELPERS ────────────────────────────────────────────

    private static void writeFile(String path, String content) {
        try {
            Files.writeString(Paths.get(path), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error saving " + path + ": " + e.getMessage());
        }
    }

    private static String readFile(String path) {
        try {
            return Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    private static String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static int parseInt(String s) {
        try { return s == null ? 0 : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private static double parseDouble(String s) {
        try { return s == null ? 0.0 : Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    /**
     * Simple parser for a JSON array of flat objects.
     * Handles the specific format we write (no nested objects).
     */
    private static List<Map<String, String>> parseJsonArray(String json) {
        List<Map<String, String>> list = new ArrayList<>();
        json = json.trim();
        if (!json.startsWith("[")) return list;

        // Split into individual objects
        int depth = 0;
        int objStart = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) objStart = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && objStart >= 0) {
                    String objStr = json.substring(objStart + 1, i);
                    list.add(parseObj(objStr));
                    objStart = -1;
                }
            }
        }
        return list;
    }

    private static Map<String, String> parseObj(String obj) {
        Map<String, String> map = new LinkedHashMap<>();
        // Match key:value pairs - handles strings and numbers
        String[] entries = obj.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
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
}
