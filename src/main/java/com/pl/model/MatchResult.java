package com.pl.model;

public class MatchResult {
    private String homeAbbr;
    private String awayAbbr;
    private int homeScore;
    private int awayScore;
    private String date;

    public MatchResult(String homeAbbr, String awayAbbr, int homeScore, int awayScore, String date) {
        this.homeAbbr = homeAbbr;
        this.awayAbbr = awayAbbr;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.date = date;
    }

    public String getHomeAbbr()  { return homeAbbr; }
    public String getAwayAbbr()  { return awayAbbr; }
    public int getHomeScore()    { return homeScore; }
    public int getAwayScore()    { return awayScore; }
    public String getDate()      { return date; }

    public String toJson() {
        return String.format(
            "{\"home\":\"%s\",\"away\":\"%s\",\"hs\":%d,\"as\":%d,\"date\":\"%s\"}",
            homeAbbr, awayAbbr, homeScore, awayScore, date
        );
    }
}
