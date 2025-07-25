package com.kugel.ulti_insights.TeamEntry;

import com.kugel.ulti_insights.League;
import com.kugel.ulti_insights.PlayerEntrys.PlayerEntry;
import java.util.ArrayList;
import java.util.List;

public class TeamEntry {
  private String teamName;
  private short year;
  private League league;

  /*
  private static final HashMap<League, String> leagueName = new HashMap();

  static {
    leagueName.put(League.MENS_CLUB, "Men's Club");
    leagueName.put(League.MENS_COLLEGE_D1, "Men's College - D1");
    leagueName.put(League.MENS_COLLEGE_D3, "Men's College - D3");
    leagueName.put(League.WOMENS_COLLEGE_D1, "Women's College - D1");
    leagueName.put(League.WOMENS_COLLEGE_D3, "Women's College - D3");
    leagueName.put(League.WOMENS_CLUB, "Women's Club");
    leagueName.put(League.WOMENS_YCC_U17, "Women's ycc - U17");
    leagueName.put(League.MENS_YCC_U17, "Men's ycc - U17");
    leagueName.put(League.OTHER, "Other");
    leagueName.put(League.WOMENS_YCC_U20, "Women's ycc - U20");
    leagueName.put(League.MENS_YCC_U20, "Men's ycc - U20");
    leagueName.put(League.MIXED_YCC_U20, "Mixed ycc - U20");
    leagueName.put(League.MIXED_YCC_U17, "Mixed ycc - U17");
    leagueName.put(League.MIXED_CLUB, "Mixed club");
  }
    */

  private List<PlayerEntry> players;

  public TeamEntry() {
    players = new ArrayList<>();
  }

  public TeamEntry(String teamName, short year, List<PlayerEntry> players, League league) {
    this.teamName = teamName;
    this.year = year;
    this.players = players;
    this.league = league;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public short getYear() {
    return year;
  }

  public void setYear(short year) {
    this.year = year;
  }

  public List<PlayerEntry> getPlayers() {
    return players;
  }

  public void setPlayers(List<PlayerEntry> players) {
    this.players = players;
  }

  public League getLeague() {
    return league;
  }

  public void setLeague(League league) {
    this.league = league;
  }

  @Override
  public int hashCode() {
    return teamName.hashCode() ^ league.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TeamEntry)) {
      return false;
    }
    TeamEntry te = (TeamEntry) o;
    return te.getTeamName().equals(teamName) && te.getLeague().equals(league);
  }
}
