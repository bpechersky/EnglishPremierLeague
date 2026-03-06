package com.pl.model;

public class Fixture {
    private String homeAbbr;
    private String awayAbbr;
    private String date;
    private String time;
    private double probH;
    private double probA;
    private double probD;

    public Fixture(String homeAbbr, String awayAbbr, String date, String time,
                   double probH, double probA, double probD) {
        this.homeAbbr = homeAbbr;
        this.awayAbbr = awayAbbr;
        this.date = date;
        this.time = time;
        this.probH = probH;
        this.probA = probA;
        this.probD = probD;
    }

    public String getHomeAbbr() { return homeAbbr; }
    public String getAwayAbbr() { return awayAbbr; }
    public String getDate()     { return date; }
    public String getTime()     { return time; }
    public double getProbH()    { return probH; }
    public double getProbA()    { return probA; }
    public double getProbD()    { return probD; }

    public String toJson() {
        return String.format(
            "{\"home\":\"%s\",\"away\":\"%s\",\"date\":\"%s\",\"time\":\"%s\"," +
            "\"probH\":%.1f,\"probA\":%.1f,\"probD\":%.1f}",
            homeAbbr, awayAbbr, date, time, probH, probA, probD
        );
    }
}
