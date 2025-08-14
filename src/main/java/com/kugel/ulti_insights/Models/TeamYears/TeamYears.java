package com.kugel.ulti_insights.Models.TeamYears;

import com.kugel.ulti_insights.Models.Player.Player;
import com.kugel.ulti_insights.Models.Teams.Teams;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specific year for a Team, containing the roster (players) for that year.
 *
 * <p>TeamYears links Players and Teams for a particular season, and stores the year and a more
 * granular yearTwo value.
 */
@Entity(name = "teamyears")
@Table(name = "teamyears")
public class TeamYears {

  @Id
  @Schema(description = "The unique identifier for the TeamYears entry")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToMany(
      cascade = {CascadeType.MERGE},
      fetch = FetchType.LAZY)
  @JoinTable(
      name = "teamyears_players",
      joinColumns = @JoinColumn(name = "teamyears_id"),
      inverseJoinColumns = @JoinColumn(name = "player_name", referencedColumnName = "player_name"),
      uniqueConstraints = @UniqueConstraint(columnNames = {"teamyears_id", "player_name"}))
  @Schema(
      description =
          "The players on the team for this year; each player can be on multiple TeamYears")
  private List<Player> players;

  @Schema(description = "The team associated with this TeamYears entry")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Teams team;

  @Schema(description = "The other year value with more granularity")
  private double yearTwo;

  @Schema(description = "The year for the corresponding team year")
  private short yearValue;

  /** The default constructor for the team years */
  public TeamYears() {
    this.players = new ArrayList<>();
  }

  /**
   * Constructs a TeamYears instance with the given year, team, and yearTwo value. Initializes the
   * players list as empty.
   */
  public TeamYears(short yearValue, Teams team, double yearTwo) {
    this.players = new ArrayList<>();
    this.team = team;
    this.yearValue = yearValue;
    this.yearTwo = yearTwo;
  }

  /** Constructs a TeamYears instance with all fields. */
  public TeamYears(Long id, List<Player> players, Teams team, short yearValue, double yearTwo) {
    this.id = id;
    this.players = players;
    this.team = team;
    this.yearValue = yearValue;
    this.yearTwo = yearTwo;
  }

  /** Gets the unique identifier for this TeamYears entry. */
  public Long getId() {
    return id;
  }

  /** Sets the unique identifier for this TeamYears entry. */
  public void setId(Long id) {
    this.id = id;
  }

  /** Sets the list of players for this TeamYears entry. */
  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  /** Gets the list of players for this TeamYears entry. */
  public List<Player> getPlayers() {
    return players;
  }

  /** Adds a player to this TeamYears roster and keeps both sides in sync. */
  public void addPlayer(Player player) {
    if (this.players == null) this.players = new ArrayList<>();
    if (!this.players.contains(player)) this.players.add(player);
    if (player.getTeamYears() == null) player.setTeamYears(new ArrayList<>());
    if (!player.getTeamYears().contains(this)) player.getTeamYears().add(this);
  }

  /** Gets the team associated with this TeamYears entry. */
  public Teams getTeam() {
    return team;
  }

  /** Sets the team for this TeamYears entry. */
  public void setTeam(Teams team) {
    this.team = team;
  }

  /** Gets the year for this TeamYears entry. */
  public short getYearValue() {
    return yearValue;
  }

  /** Sets the year for this TeamYears entry. */
  public void setYearValue(short yearValue) {
    this.yearValue = yearValue;
  }

  /** Gets the more granular yearTwo value for this TeamYears entry. */
  public double getYearTwo() {
    return yearTwo;
  }

  /** Sets the more granular yearTwo value for this TeamYears entry. */
  public void setYearTwo(double yearTwo) {
    this.yearTwo = yearTwo;
  }
}
