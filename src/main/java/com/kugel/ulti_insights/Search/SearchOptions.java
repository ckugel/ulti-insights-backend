package com.kugel.ulti_insights.Search;

import com.kugel.ulti_insights.Views.TeamSearchOption.TeamSearchOption;
import java.util.List;

/** The search options to return Has a list of players name players. A view */
public class SearchOptions {
  private List<String> players;

  /** A list of team search options which may be returned List of items with name and league */
  private List<TeamSearchOption> teams;

  /**
   * A constructor for the search options object. Contains a list of players and teams search
   * options
   */
  public SearchOptions(List<String> players, List<TeamSearchOption> teams) {
    this.players = players;
    this.teams = teams;
  }

  /** The teams options */
  public List<TeamSearchOption> getTeams() {
    return teams;
  }

  /** Set the team search options */
  public void setTeams(List<TeamSearchOption> teams) {
    this.teams = teams;
  }

  /** Get the players */
  public List<String> getPlayers() {
    return players;
  }

  /** Sets the players */
  public void setPlayers(List<String> players) {
    this.players = players;
  }
}
