package com.kugel.ulti_insights.PlayerEntrys;

// Does not get saved in the database but is used for easy formatting back to the client
public class PlayerEntry {
  private String name;
  private String team;
  private short year;
  private String league;
  private Double rankingValue;

  public PlayerEntry() {}

  public PlayerEntry(String name, String team, short year, Double rankingValue, String league) {
    this.name = name;
    this.team = team;
    this.year = year;
    this.league = league;
    this.rankingValue = rankingValue;
  }

  public String getLeague() {
    return league;
  }

  public void setLeague(String league) {
    this.league = league;
  }

  public String getName() {
    return name;
  }

  public String getTeam() {
    return team;
  }

  public Double getRankingValue() {
    return rankingValue;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTeam(String team) {
    this.team = team;
  }

  public void setRankingValue(Double rankingValue) {
    this.rankingValue = rankingValue;
  }

  public short getYear() {
    return year;
  }

  public void setYear(short year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return "PlayerEntry{"
        + "name='"
        + name
        + '\''
        + ", team='"
        + team
        + '\''
        + ", year="
        + year
        + ", league="
        + league
        + ", rankingValue="
        + rankingValue
        + '}';
  }
}
