package com.kugel.ulti_insights.Models.UltiData;

import com.kugel.ulti_insights.League;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UltiDataService {

  @Autowired private UltiDataRepository repository;

  public UltiData saveUltiData(UltiData ultidata) {
    return repository.save(ultidata);
  }

  public List<UltiData> saveAll(List<UltiData> ultidataList) {
    return repository.saveAll(ultidataList);
  }

  public boolean playerExists(String playerName) {
    return !repository.findAllByNameIgnoreCase(playerName).isEmpty();
  }

  public List<UltiData> getPlayer(String playerName) {
    return repository.findAllByNameIgnoreCase(playerName);
  }

  public List<UltiData> getTeam(String teamName) {
    return repository.findAllByTeamIgnoreCase(teamName);
  }

  public boolean teamExists(String name) {
    return !repository.findAllByTeamIgnoreCase(name).isEmpty();
  }

  public List<UltiData> getTeamYear(String teamName, short year) {
    return repository.findAllByTeamIgnoreCaseAndYearValue(teamName, year);
  }

  public List<UltiData> getTeamByLeagueAndNameAndYear(String name, short year, League league) {
    return repository.findAllByTeamIgnoreCaseAndYearValueAndLeague(name, year, league);
  }

  public List<UltiData> getAll() {
    return repository.findAll();
  }

  public List<UltiData> getTeamByLeagueAndName(String name, League league) {
    return repository.findByTeamIgnoreCaseAndLeague(name, league);
  }

  // New fast-path helpers using @Query projections

  /** Distinct calendar years for a team name (case-insensitive). */
  public List<Short> getDistinctYearsForTeam(String team) {
    return repository.findDistinctYearsForTeam(team);
  }

  /** Distinct calendar years for a team name within a league. */
  public List<Short> getDistinctYearsForTeamAndLeague(String team, League league) {
    return repository.findDistinctYearsForTeamAndLeague(team, league);
  }

  /** Distinct leagues that have entries for a team (case-insensitive). */
  public List<League> getDistinctLeaguesForTeam(String team) {
    return repository.findDistinctLeaguesForTeam(team);
  }

  /** Distinct (league, year) pairs for a team name. */
  public List<UltiDataRepository.LeagueYear> getDistinctLeagueYearsForTeam(String team) {
    return repository.findDistinctLeagueYearsForTeam(team);
  }
}
