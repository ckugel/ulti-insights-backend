package com.kugel.ulti_insights.Views.PlayerEntrys;

import com.kugel.ulti_insights.League;

// Does not get saved in the database but is used for easy formatting back to the client
public class PlayerEntry {
  private String name;
  private String team;
  private short year;
  private League league;
  private Double rankingValue;
  private double yearValueTwo;
  private double displayValue;

  public PlayerEntry() {}

  public PlayerEntry(
      String name,
      String team,
      short year,
      League league,
      Double rankingValue,
      double yearValueTwo,
      double displayValue) {
    this.name = name;
    this.team = team;
    this.year = year;
    this.league = league;
    this.rankingValue = rankingValue;
    this.yearValueTwo = yearValueTwo;
    this.displayValue = displayValue;
  }

  public League getLeague() {
    return league;
  }

  public void setLeague(League league) {
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

  public double getDisplayValue() {
    return this.displayValue;
  }

  public void setDisplayValue(double displayValue) {
    this.displayValue = displayValue;
  }

  public double getYearValueTwo() {
    return this.yearValueTwo;
  }

  public void setYearValueTwo(double yearValueTwo) {
    this.yearValueTwo = yearValueTwo;
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
        + ", yearValueTwo="
        + yearValueTwo
        + ", displayValue"
        + displayValue
        + '}';
  }
}
