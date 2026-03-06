package com.pl.service;

import com.pl.model.Fixture;
import com.pl.model.MatchResult;
import com.pl.model.Team;

import java.util.*;
import java.util.stream.Collectors;

public class LeagueService {

    private final Map<String, Team> teams = new LinkedHashMap<>();
    private final List<MatchResult> results = new ArrayList<>();
    private final List<Fixture> fixtures = new ArrayList<>();

    private static final LeagueService INSTANCE = new LeagueService();
    public static LeagueService getInstance() { return INSTANCE; }

    private LeagueService() {
        DataStore.init();
        if (DataStore.hasSavedData()) {
            System.out.println("Loading saved data from data/ directory...");
            teams.putAll(DataStore.loadTeams());
            results.addAll(DataStore.loadResults());
            fixtures.addAll(DataStore.loadFixtures());
            System.out.println("Loaded " + teams.size() + " teams, " +
                               results.size() + " results, " +
                               fixtures.size() + " fixtures from disk.");
        } else {
            System.out.println("No saved data found. Loading defaults...");
            initTeams();
            initResults();
            initFixtures();
            // Save the defaults so they exist on disk
            DataStore.saveAll(teams, results, fixtures);
            System.out.println("Default data saved to data/ directory.");
        }
    }

    private void initTeams() {
        Object[][] data = {
            {"Arsenal FC",              "ARS", "#EF0107", "#063672", 20, 7,  3,  62, 28},
            {"Manchester City",         "MCI", "#6CABDD", "#1C2C5B", 18, 6,  5,  58, 32},
            {"Manchester United",       "MUN", "#DA291C", "#FBE122", 14, 9,  6,  48, 38},
            {"Aston Villa",             "AVL", "#95BFE5", "#670E36", 15, 6,  8,  52, 40},
            {"Chelsea FC",              "CFC", "#034694", "#DBA111", 13, 9,  7,  50, 38},
            {"Liverpool FC",            "LFC", "#C8102E", "#00B2A9", 14, 6,  9,  55, 42},
            {"Brentford FC",            "BRE", "#E30613", "#FBB800", 13, 5, 11,  44, 45},
            {"Everton FC",              "EVE", "#003399", "#FFFFFF", 12, 7, 10,  40, 42},
            {"AFC Bournemouth",         "BOU", "#DA291C", "#000000",  9,13,  7,  36, 38},
            {"Fulham FC",               "FUL", "#000000", "#CC0000", 12, 4, 13,  42, 48},
            {"Sunderland AFC",          "SUN", "#EB172B", "#000000", 10,10,  9,  38, 40},
            {"Newcastle United",        "NEW", "#241F20", "#00A650", 11, 6, 12,  44, 46},
            {"Brighton & Hove Albion",  "BRI", "#0057B8", "#FFCD00",  9,10, 10,  40, 42},
            {"Crystal Palace",          "CRY", "#1B458F", "#C4122E",  9, 8, 11,  34, 42},
            {"Leeds United",            "LEE", "#FFCD00", "#1D428A",  7,10, 12,  34, 46},
            {"Tottenham Hotspur",       "TOT", "#132257", "#FFFFFF",  7, 8, 13,  36, 50},
            {"Nottingham Forest",       "NFO", "#DD0000", "#FFFFFF",  7, 7, 15,  30, 52},
            {"West Ham United",         "WHU", "#7A263A", "#1BB1E7",  7, 7, 15,  32, 54},
            {"Burnley FC",              "BUR", "#6C1D45", "#99D6EA",  4, 7, 18,  22, 62},
            {"Wolverhampton Wanderers", "WOL", "#FDB913", "#231F20",  3, 7, 20,  20, 68},
        };
        for (Object[] row : data) {
            String abbr = (String) row[1];
            teams.put(abbr, new Team(
                (String) row[0], abbr, (String) row[2], (String) row[3],
                (int) row[4], (int) row[5], (int) row[6], (int) row[7], (int) row[8]
            ));
        }
    }

    private void initResults() {
        Object[][] data = {
            {"ARS","CFC", 2,1,"Mar 1"},
            {"BOU","BRE", 0,0,"Mar 3"},
            {"EVE","BUR", 2,0,"Mar 3"},
            {"LEE","SUN", 0,1,"Mar 3"},
            {"WOL","LFC", 2,1,"Mar 3"},
            {"BRI","ARS", 0,1,"Mar 4"},
            {"FUL","WHU", 0,1,"Mar 4"},
            {"AVL","CFC", 1,4,"Mar 4"},
            {"MCI","NFO", 2,2,"Mar 4"},
            {"NEW","MUN", 2,1,"Mar 4"},
        };
        for (Object[] row : data) {
            results.add(new MatchResult(
                (String) row[0], (String) row[1],
                (int) row[2], (int) row[3], (String) row[4]
            ));
        }
    }

    private void initFixtures() {
        Object[][] data = {
            {"TOT","CRY","Mar 5", "20:00", 40.0, 31.8, 28.2},
            {"BUR","BOU","Mar 14","15:00", 25.8, 48.5, 25.7},
            {"SUN","BRI","Mar 14","15:00", 28.5, 43.8, 27.7},
            {"ARS","EVE","Mar 14","17:30", 74.5,  8.9, 16.6},
            {"CFC","NEW","Mar 14","17:30", 52.4, 24.4, 23.2},
            {"WHU","MCI","Mar 14","20:00", 19.9, 58.5, 21.6},
            {"LFC","TOT","Mar 15","16:30", 69.8, 12.9, 17.3},
        };
        for (Object[] row : data) {
            fixtures.add(new Fixture(
                (String) row[0], (String) row[1],
                (String) row[2], (String) row[3],
                (double) row[4], (double) row[5], (double) row[6]
            ));
        }
    }

    /** Returns standings sorted by pts desc, then gd desc, then gf desc */
    public List<Team> getStandings() {
        return teams.values().stream()
            .sorted(Comparator
                .comparingInt(Team::getPoints).reversed()
                .thenComparingInt(Team::getGoalDiff).reversed()
                .thenComparingInt(Team::getGoalsFor).reversed())
            .collect(Collectors.toList());
    }

    public List<MatchResult> getResults() {
        List<MatchResult> reversed = new ArrayList<>(results);
        Collections.reverse(reversed);
        return reversed;
    }

    public List<Fixture> getFixtures() { return new ArrayList<>(fixtures); }

    public Map<String, Team> getTeams() { return Collections.unmodifiableMap(teams); }

    /**
     * Adds a new match result and updates both teams' stats.
     * Also removes from fixtures if this match was scheduled.
     * Returns error string or null on success.
     */
    public synchronized String addResult(String homeAbbr, String awayAbbr,
                                         int homeScore, int awayScore, String date) {
        if (!teams.containsKey(homeAbbr)) return "Unknown team: " + homeAbbr;
        if (!teams.containsKey(awayAbbr)) return "Unknown team: " + awayAbbr;
        if (homeAbbr.equals(awayAbbr))    return "A team cannot play itself";
        if (homeScore < 0 || awayScore < 0) return "Scores cannot be negative";

        teams.get(homeAbbr).addResult(homeScore, awayScore);
        teams.get(awayAbbr).addResult(awayScore, homeScore);
        results.add(new MatchResult(homeAbbr, awayAbbr, homeScore, awayScore, date));

        // Remove from fixtures if it was scheduled
        fixtures.removeIf(f ->
            f.getHomeAbbr().equals(homeAbbr) && f.getAwayAbbr().equals(awayAbbr));

        // Save to disk
        DataStore.saveAll(teams, results, fixtures);

        return null; // success
    }

    /**
     * Adds a new team to the league.
     * Returns error string or null on success.
     */
    public synchronized String addTeam(String name, String abbr,
                                       String primaryColor, String secondaryColor) {
        if (name == null || name.trim().isEmpty()) return "Team name is required";
        if (abbr == null || abbr.trim().isEmpty()) return "Abbreviation is required";
        abbr = abbr.trim().toUpperCase();
        if (abbr.length() < 2 || abbr.length() > 4) return "Abbreviation must be 2-4 characters";
        if (teams.containsKey(abbr)) return "A team with abbreviation " + abbr + " already exists";
        if (primaryColor == null || primaryColor.trim().isEmpty()) primaryColor = "#888888";
        if (secondaryColor == null || secondaryColor.trim().isEmpty()) secondaryColor = "#333333";

        teams.put(abbr, new Team(name.trim(), abbr, primaryColor.trim(), secondaryColor.trim(),
                                 0, 0, 0, 0, 0));

        // Save to disk
        DataStore.saveTeams(teams);

        return null;
    }

    public String getStandingsJson() {
        List<Team> sorted = getStandings();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(sorted.get(i).toJson(i + 1));
        }
        sb.append("]");
        return sb.toString();
    }

    public String getResultsJson() {
        List<MatchResult> list = getResults();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(list.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
    }

    public String getFixturesJson() {
        List<Fixture> list = getFixtures();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(list.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
    }

    /** Returns JSON for a single team: stats + their results + their fixtures */
    public String getTeamDetailJson(String abbr) {
        Team team = teams.get(abbr);
        if (team == null) return null;

        // Find rank
        List<Team> sorted = getStandings();
        int rank = 1;
        for (Team t : sorted) {
            if (t.getAbbr().equals(abbr)) break;
            rank++;
        }

        // Team results (newest first)
        List<MatchResult> teamResults = new ArrayList<>();
        for (int i = results.size() - 1; i >= 0; i--) {
            MatchResult r = results.get(i);
            if (r.getHomeAbbr().equals(abbr) || r.getAwayAbbr().equals(abbr)) {
                teamResults.add(r);
            }
        }

        // Team fixtures
        List<Fixture> teamFixtures = new ArrayList<>();
        for (Fixture f : fixtures) {
            if (f.getHomeAbbr().equals(abbr) || f.getAwayAbbr().equals(abbr)) {
                teamFixtures.add(f);
            }
        }

        // Build form string (last 5 results: W/D/L)
        StringBuilder form = new StringBuilder("[");
        int count = 0;
        for (MatchResult r : teamResults) {
            if (count >= 5) break;
            boolean isHome = r.getHomeAbbr().equals(abbr);
            int gf = isHome ? r.getHomeScore() : r.getAwayScore();
            int ga = isHome ? r.getAwayScore() : r.getHomeScore();
            if (count > 0) form.append(",");
            form.append("\"").append(gf > ga ? "W" : gf == ga ? "D" : "L").append("\"");
            count++;
        }
        form.append("]");

        // Build results JSON
        StringBuilder rJson = new StringBuilder("[");
        for (int i = 0; i < teamResults.size(); i++) {
            if (i > 0) rJson.append(",");
            rJson.append(teamResults.get(i).toJson());
        }
        rJson.append("]");

        // Build fixtures JSON
        StringBuilder fJson = new StringBuilder("[");
        for (int i = 0; i < teamFixtures.size(); i++) {
            if (i > 0) fJson.append(",");
            fJson.append(teamFixtures.get(i).toJson());
        }
        fJson.append("]");

        return String.format(
            "{\"team\":%s,\"form\":%s,\"results\":%s,\"fixtures\":%s}",
            team.toJson(rank), form, rJson, fJson
        );
    }

    public String getTeamsJson() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Team t : teams.values()) {
            if (!first) sb.append(",");
            sb.append(String.format("{\"abbr\":\"%s\",\"name\":\"%s\"}", t.getAbbr(), t.getName()));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
