package com.kugel.ulti_insights.Models.UltiData;

import com.kugel.ulti_insights.League;
import com.kugel.ulti_insights.Models.Player.*;
import com.kugel.ulti_insights.Views.TeamEntry.TeamEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Schema(
    name = "UltiData",
    description = "A single statistical entry for a player in a given team/league context. "
        + "Contains both raw values (e.g., stat, share) and derived values (rankingValue, displayValue). "
        + "'yearValue' is the calendar year and 'yearValueTwo' is a more granular year value (e.g., including quarter). "
        + "'displayValue' is an accumulated form of 'rankingValue' meant for display.")
@Entity(name = "ultidata")
@Table(name = "ultidata")
public class UltiData {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Database-generated unique identifier for the entry", example = "12345")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_name", nullable = false)
  @Schema(description = "The player this entry corresponds to. Kept LAZY to avoid unnecessary loading.")
  @JsonIgnore // prevent unintended lazy loading during JSON serialization (avoids N+1)
  private Player player;

  @Schema(description = "Calendar year of the entry", example = "2024")
  private short yearValue;

  @Schema(description = "Tournament name associated with the entry", example = "Nationals")
  private String tournament;

  @Schema(description = "Quarter of the year (1-4) when the entry applies", example = "2")
  private short quarter;

  @Schema(
      description = "More granular year value than 'yearValue'. For example, may encode fractional year or sub-year granularity.",
      example = "2024.5")
  private double yearValueTwo;

  @Schema(description = "Tier of the event/competition", example = "1")
  private short tier;

  @Schema(description = "Multiplier applied to the raw stat for ranking calculations", example = "1.25")
  private double multiplier;

  @Schema(description = "Final position the player/team finished in the event", example = "3")
  private Long finishPosition;

  @Schema(description = "Team name for the entry", example = "Seattle Sockeye")
  private String team;

  @Schema(
      description = "Denormalized player name captured with this entry (duplicated from Player for convenience).",
      example = "John Doe")
  private String name;

  @Schema(description = "Primary raw statistic captured for this entry", example = "42.0")
  private double stat;

  @Schema(description = "Share of the stat attributed to the player (e.g., usage share)", example = "0.37")
  private double share;

  @Schema(description = "Per-entry ranking value used for calculations and accumulation", example = "15.2")
  private double rankingValue;

  @Schema(
      description = "Accumulated form of 'rankingValue' intended for display. Note: getter returns a rounded value.",
      example = "128.0")
  private double displayValue;

  @Enumerated(EnumType.STRING)
  @Schema(description = "League in which the entry occurred")
  private League league;

  public UltiData() {}

  /**
   * Full constructor linking to a Player.
   */
  public UltiData(
      short yearValue,
      String Tournament,
      short quarter,
      double yearValueTwo,
      short tier,
      double multiplier,
      Long finishPosition,
      String team,
      String name,
      double stat,
      double share,
      double rankingValue,
      League league,
      double displayValue,
      Player player) {
    this.yearValue = yearValue;
    this.tournament = Tournament;
    this.quarter = quarter;
    this.yearValueTwo = yearValueTwo;
    this.tier = tier;
    this.multiplier = multiplier;
    this.finishPosition = finishPosition;
    this.team = team;
    this.name = name;
    this.stat = stat;
    this.share = share;
    this.rankingValue = rankingValue;
    this.league = league;
    this.displayValue = displayValue;
    this.player = player;
  }

  /**
   * Constructor without linking to a Player (player can be set later).
   */
  public UltiData(
      short yearValue,
      String Tournament,
      short quarter,
      double yearValueTwo,
      short tier,
      double multiplier,
      Long finishPosition,
      String team,
      String name,
      double stat,
      double share,
      double rankingValue,
      League league,
      double displayValue) {
    this.yearValue = yearValue;
    this.tournament = Tournament;
    this.quarter = quarter;
    this.yearValueTwo = yearValueTwo;
    this.tier = tier;
    this.multiplier = multiplier;
    this.finishPosition = finishPosition;
    this.team = team;
    this.name = name;
    this.stat = stat;
    this.share = share;
    this.rankingValue = rankingValue;
    this.league = league;
    this.displayValue = displayValue;
  }

  public League getLeague() {
    return league;
  }

  public void setLeague(League league) {
    this.league = league;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public short getYearValue() {
    return yearValue;
  }

  public void setYearValue(short year_value) {
    this.yearValue = year_value;
  }

  public double getYearValueTwo() {
    return yearValueTwo;
  }

  public void setYearValueTwo(double year_value_two) {
    this.yearValueTwo = year_value_two;
  }

  public String getTournament() {
    return tournament;
  }

  public void setTournament(String tournament) {
    this.tournament = tournament;
  }

  public short getQuarter() {
    return quarter;
  }

  public void setQuarter(short quarter) {
    this.quarter = quarter;
  }

  public short getTier() {
    return tier;
  }

  public void setTier(short tier) {
    this.tier = tier;
  }

  public double getMultiplier() {
    return multiplier;
  }

  public void setMultiplier(double multiplier) {
    this.multiplier = multiplier;
  }

  public Long getFinishPosition() {
    return finishPosition;
  }

  public void setFinishPosition(Long finishPosition) {
    this.finishPosition = finishPosition;
  }

  public String getTeam() {
    return team;
  }

  public void setTeam(String team) {
    this.team = team;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getStat() {
    return stat;
  }

  public void setStat(double stat) {
    this.stat = stat;
  }

  public double getShare() {
    return share;
  }

  public void setShare(double share) {
    this.share = share;
  }

  public double getRankingValue() {
    return rankingValue;
  }

  public void setRankingValue(double rankingValue) {
    this.rankingValue = rankingValue;
  }

  /**
   * Convenience converter for views that expect a TeamEntry.
   */
  public TeamEntry getAsTeamEntry() {
    return new TeamEntry(team, yearValue, null, league);
  }

  public void setDisplayValue(double displayValue) {
    this.displayValue = displayValue;
  }

  /**
   * Returns the accumulated display value rounded to the nearest whole number.
   */
  public double getDisplayValue() {
    return Math.round(this.displayValue);
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }
}
