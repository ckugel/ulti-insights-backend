package com.kugel.ulti_insights.Views.TeamEntry;

import com.kugel.ulti_insights.League;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LeagueYear", description = "Pair of league and calendar year for which a team has data")
public class LeagueYearDTO {
  @Schema(description = "League identifier")
  private League league;

  @Schema(description = "Calendar year")
  private short year;

  public LeagueYearDTO() {}

  public LeagueYearDTO(League league, short year) {
    this.league = league;
    this.year = year;
  }

  public League getLeague() {
    return league;
  }

  public void setLeague(League league) {
    this.league = league;
  }

  public short getYear() {
    return year;
  }

  public void setYear(short year) {
    this.year = year;
  }
}

