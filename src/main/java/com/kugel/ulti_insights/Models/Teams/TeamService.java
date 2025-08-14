package com.kugel.ulti_insights.Models.Teams;

import com.kugel.ulti_insights.League;
import com.kugel.ulti_insights.Models.TeamYears.TeamYears;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

  @Autowired private TeamsRepository teamsRepository;

  private static final Logger log = LoggerFactory.getLogger(TeamService.class);

  /** Save a teams instance to the database */
  public Teams saveTeams(Teams teams) {
    log.debug("Saving team: {} ({}) with {} teamYears", teams.getName(), teams.getLeague(),
        teams.getTeamYears() == null ? 0 : teams.getTeamYears().size());
    return teamsRepository.save(teams);
  }

  /** Gets the teams which match the name provided */
  public Optional<List<Teams>> getTeams(String name) {
    log.trace("Finding teams by name (ignore-case): {}", name);
    return teamsRepository.findAllByNameIgnoreCase(name);
  }

  /** Batch saves to DB */
  public List<Teams> saveAll(@NotNull List<Teams> teams) {
    log.debug("Saving {} teams", teams == null ? 0 : teams.size());
    return teamsRepository.saveAll(teams);
  }

  /**
   * Get the teams in a league
   *
   * @param league the league to match on
   */
  public List<Teams> getTeamsInLeague(League league) {
    log.trace("Fetching teams in league: {}", league);
    return teamsRepository.findByLeague(league);
  }

  /**
   * Get the teams the match a specific name and league
   *
   * @param league the league to match on
   * @param name the name to match on
   */
  public Optional<Teams> getTeamsByNameAndLeague(String name, League league) {
    log.trace("Finding team by name+league: {} / {}", name, league);
    return teamsRepository.findByNameIgnoreCaseAndLeague(name, league);
  }

  public Optional<List<Short>> getTeamYears(String name, League league) {
    Optional<Teams> teamOpt = getTeamsByNameAndLeague(name, league);
    if (teamOpt.isEmpty()) {
      return Optional.empty();
    }
    List<Short> years =
        teamOpt.get().getTeamYears().stream()
            .map(TeamYears::getYearValue)
            .collect(Collectors.toList());
    return Optional.of(years);
  }

  public List<Teams> getAll() {
    log.trace("Fetching all teams");
    return teamsRepository.findAll();
  }

  public Optional<List<Teams>> getTeamsByName(String name) {
    log.trace("Finding teams by name (ignore-case): {}", name);
    return teamsRepository.findAllByNameIgnoreCase(name);
  }
}
