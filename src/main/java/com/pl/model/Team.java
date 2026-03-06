package com.pl.model;

public class Team {
    private String name;
    private String abbr;
    private String primaryColor;
    private String secondaryColor;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;

    public Team(String name, String abbr, String primaryColor, String secondaryColor,
                int wins, int draws, int losses, int goalsFor, int goalsAgainst) {
        this.name = name;
        this.abbr = abbr;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
    }

    public int getPoints()       { return wins * 3 + draws; }
    public int getGamesPlayed()  { return wins + draws + losses; }
    public int getGoalDiff()     { return goalsFor - goalsAgainst; }

    public String getName()         { return name; }
    public String getAbbr()         { return abbr; }
    public String getPrimaryColor() { return primaryColor; }
    public String getSecondaryColor(){ return secondaryColor; }
    public int getWins()            { return wins; }
    public int getDraws()           { return draws; }
    public int getLosses()          { return losses; }
    public int getGoalsFor()        { return goalsFor; }
    public int getGoalsAgainst()    { return goalsAgainst; }

    public void addResult(int gf, int ga) {
        this.goalsFor += gf;
        this.goalsAgainst += ga;
        if (gf > ga)      wins++;
        else if (gf == ga) draws++;
        else               losses++;
    }

    public String toJson(int rank) {
        return String.format(
            "{\"rank\":%d,\"name\":\"%s\",\"abbr\":\"%s\",\"primaryColor\":\"%s\"," +
            "\"secondaryColor\":\"%s\",\"w\":%d,\"d\":%d,\"l\":%d,\"gp\":%d," +
            "\"gf\":%d,\"ga\":%d,\"gd\":%d,\"pts\":%d}",
            rank, name, abbr, primaryColor, secondaryColor,
            wins, draws, losses, getGamesPlayed(),
            goalsFor, goalsAgainst, getGoalDiff(), getPoints()
        );
    }
}
