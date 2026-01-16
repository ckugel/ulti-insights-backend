package com.kugel.ulti_insights.Models.Player;

import com.kugel.ulti_insights.Models.TeamYears.TeamYears;
import com.kugel.ulti_insights.Models.UltiData.UltiData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * The Player entry in the database. Players have a name, list of entries, list of teams they have
 * been on with the year they were, A map of ranking and display values
 */
@Entity(name = "player")
@Table(name = "player")
public class Player {
  @Schema(description = "The unique name of the player")
  @Id
  @Column(name = "player_name", nullable = false, updatable = false)
  private String playerName;

  @Schema(description = "The list of UltiData entries (statistics) associated with the player")
  @OneToMany(
      mappedBy = "player",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<UltiData> entries;

  @Schema(description = "The list of TeamYears the player participated in")
  @ManyToMany(mappedBy = "players", fetch = FetchType.LAZY)
  private List<TeamYears> teamYears;

  public Player() {
    this.playerName = "shouldn't exist";
    this.entries = new ArrayList<>();
    this.teamYears = new ArrayList<>();
  }

  /**
   * A constructor for the player class
   *
   * @param playerName the name of the player - unique indicator
   * @param entries A list of the entries that the
   * @param teamYears A list of the teams that players are on
   */
  public Player(String playerName, List<UltiData> entries, List<TeamYears> teamYears) {
    this.playerName = playerName;
    this.entries = entries;
    this.teamYears = teamYears;
  }

  /**
   * Gets the player's unique name.
   *
   * @return playerName
   */
  public String getPlayerName() {
    return playerName;
  }

  /**
   * Sets the player's unique name.
   *
   * @param playerName the name to set
   */
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  /**
   * Gets the list of UltiData entries for the player.
   *
   * @return list of UltiData
   */
  public List<UltiData> getEntries() {
    return entries;
  }

  /**
   * Sets the list of UltiData entries for the player.
   *
   * @param entries list of UltiData
   */
  public void setEntries(List<UltiData> entries) {
    this.entries = entries;
  }

  /**
   * Adds an ulti data entry for a corresponding player and keeps both sides in sync.
   *
   * @param ud the ulti data entry to add
   */
  public void addUltiDataEntry(UltiData ud) {
    if (this.entries == null) {
      this.entries = new ArrayList<>();
    }
    // ensure bidirectional association is set
    ud.setPlayer(this);
    this.entries.add(ud);
  }

  /**
   * Gets the list of TeamYears the player participated in.
   *
   * @return list of TeamYears
   */
  public List<TeamYears> getTeamYears() {
    return teamYears;
  }

  /**
   * Sets the list of TeamYears for the player.
   *
   * @param teamYears list of TeamYears
   */
  public void setTeamYears(List<TeamYears> teamYears) {
    this.teamYears = teamYears;
  }

  /** Utility to link a TeamYears to this Player and keep both sides in sync. */
  public void addTeamYear(TeamYears teamYear) {
    if (this.teamYears == null) this.teamYears = new ArrayList<>();
    if (!this.teamYears.contains(teamYear)) this.teamYears.add(teamYear);
    if (teamYear.getPlayers() == null) {
      teamYear.setPlayers(new ArrayList<>());
    }
    if (!teamYear.getPlayers().contains(this)) {
      teamYear.getPlayers().add(this);
    }
  }

  /**
   * Gets the display value for a given year, or null if not present.
   *
   * @param year the year to look up
   * @return the display value for the year, or null if not found
   */
  public Double getDisplayValueForYear(short year) {
    if (entries == null || entries.isEmpty()) {
      return null;
    }

    UltiData minYearEntry = Collections.min(entries, Comparator.comparing(UltiData::getYearValue));
    UltiData maxYearEntry = Collections.max(entries, Comparator.comparing(UltiData::getYearValue));

    // System.out.printf("year indexed on: {%d}. max year: {%d}. min year: {%d}\n",year,
    // maxYearEntry.getYearValue(), minYearEntry.getYearValue());

    if (year < minYearEntry.getYearValue()) {
      return 0d;
    }
    if (year > maxYearEntry.getYearValue()) {
      return maxYearEntry.getDisplayValue();
    }
    return entries.stream()
        .filter(e -> e.getYearValue() == year)
        .map(UltiData::getDisplayValue)
        .findFirst()
        .orElse(0d);
  }

  /**
   * Gets the ranking value for a given year, or null if not present.
   *
   * @param year the year to look up
   * @return the ranking value for the year, or null if not found
   */
  public Double getRankingValueForYear(Short year) {
    if (entries == null || entries.isEmpty()) {
      return null;
    }
    return entries.stream()
        .filter(e -> e.getYearValue() == year)
        .map(UltiData::getRankingValue)
        .findFirst()
        .orElse(null);
  }

  /** Custom to String for easier reading */
  public String toString() {
    return "{\nname: "
        + this.playerName
        + ",\nteam years: "
        + (this.teamYears == null ? "[]" : this.teamYears.toString())
        + ", \nentries"
        + (this.entries == null ? "[]" : this.entries.toString())
        + "\n}";
  }

  /** Equality is based on the immutable primary key (playerName). */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Player player = (Player) o;
    return Objects.equals(playerName, player.playerName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerName);
  }
}
