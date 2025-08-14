package com.kugel.ulti_insights.Models.TeamYears;

import com.kugel.ulti_insights.League;
import com.kugel.ulti_insights.Models.Teams.TeamService;
import com.kugel.ulti_insights.Models.Teams.Teams;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamYearsService {
  @Autowired private TeamYearsRepository repository;
  @Autowired private TeamService teamService;

  public TeamYears saveTeamYears(TeamYears teamYears) {
    return repository.save(teamYears);
  }

  public Optional<TeamYears> getTeamYearsById(Long id) {
    return repository.findById(id);
  }

  /** Batch saves to DB */
  public List<TeamYears> saveAll(List<TeamYears> teams) {
    return repository.saveAll(teams);
  }

  /**
   * Returns the TeamYears by team name, league, and year. This method is transactional because it
   * traverses a LAZY collection (Teams.teamYears).
   */
  @Transactional(readOnly = true)
  public Optional<TeamYears> getTeamByNameAndLeagueAndYear(String name, short year, League league) {
    return repository.findByTeam_NameIgnoreCaseAndTeam_LeagueAndYearValue(name, league, year);
  }

  /** Fetch TeamYears with players and team for a given team name and league (avoids N+1). */
  @Transactional(readOnly = true)
  public List<TeamYears> getTeamYearsByTeamNameAndLeague(String name, League league) {
    return repository.findByTeam_NameIgnoreCaseAndTeam_League(name, league);
  }

  /** Fetch all TeamYears with players and team for a team name (across leagues). */
  @Transactional(readOnly = true)
  public List<TeamYears> getTeamYearsByTeamName(String name) {
    return repository.findByTeam_NameIgnoreCase(name);
  }

  /** Year-only list for a team name and league (single query). */
  @Transactional(readOnly = true)
  public List<Short> getYearValuesByTeamNameAndLeague(String name, League league) {
    return repository.findDistinctYearValuesByTeamNameAndLeague(name, league);
  }

  /** Distinct leagues that have this team name. */
  @Transactional(readOnly = true)
  public List<League> getLeaguesByTeamName(String name) {
    return repository.findDistinctLeaguesByTeamName(name);
  }

  /** Compact list of (league, year) pairs for this team name. */
  @Transactional(readOnly = true)
  public List<LeagueYearProjection> getLeagueYearsByTeamName(String name) {
    return repository.findLeagueYearsByTeamName(name);
  }
}
