package com.kugel.ulti_insights.Models.Teams;

import com.kugel.ulti_insights.League;
import com.kugel.ulti_insights.Models.TeamYears.TeamYears;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "teams")
@Table(name = "teams")
public class Teams {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Schema(description = "The name of the team")
  private String name;

  @Schema(description = "The league that the team is in")
  private League league;

  @OneToMany(
      mappedBy = "team",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<TeamYears> teamYears;

  /** Default constructor for a teams object */
  public Teams() {
    this.teamYears = new ArrayList<>();
  }

  public Teams(Long id, String name, List<TeamYears> teamYears, League league) {
    this.id = id;
    this.name = name;
    this.teamYears = teamYears;
    this.league = league;
  }

  public Teams(String name, List<TeamYears> teamYears, League league) {
    this.name = name;
    this.teamYears = teamYears;
    this.league = league;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void addTeamYear(short year) {
    this.teamYears.add(new TeamYears(year, this, year));
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setTeamYears(List<TeamYears> teamYears) {
    this.teamYears = teamYears;
  }

  public List<TeamYears> getTeamYears() {
    return teamYears;
  }

  public League getLeague() {
    return league;
  }

  public void setLeague(League league) {
    this.league = league;
  }
}
