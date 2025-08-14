package com.kugel.ulti_insights.Models.UltiData;

import com.kugel.ulti_insights.League;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UltiDataRepository extends JpaRepository<UltiData, Long> {

  List<UltiData> findAllByNameIgnoreCase(String name);

  List<UltiData> findAllByTeamIgnoreCase(String teamName);

  List<UltiData> findAllByTeamIgnoreCaseAndYearValueAndLeague(
      String teamName, short year, League league);

  List<UltiData> findAllByTeamIgnoreCaseAndYearValue(String teamName, short year_value);

  List<UltiData> findByLeague(League league);

  List<UltiData> findByTeamIgnoreCaseAndLeague(String name, League league);

  /**
   * Distinct calendar years in which the given team has entries, case-insensitive team match.
   */
  @Query("select distinct u.yearValue from ultidata u where lower(u.team) = lower(:team)")
  List<Short> findDistinctYearsForTeam(@Param("team") String team);

  /**
   * Distinct calendar years for a team within a league.
   */
  @Query(
      "select distinct u.yearValue from ultidata u where lower(u.team) = lower(:team) and u.league = :league")
  List<Short> findDistinctYearsForTeamAndLeague(
      @Param("team") String team, @Param("league") League league);

  /**
   * Distinct leagues in which the given team has entries, case-insensitive team match.
   */
  @Query("select distinct u.league from ultidata u where lower(u.team) = lower(:team)")
  List<League> findDistinctLeaguesForTeam(@Param("team") String team);

  /**
   * Distinct (league, year) pairs for a given team. Uses interface-based projection for efficiency.
   */
  @Query(
      "select distinct u.league as league, u.yearValue as year "
          + "from ultidata u where lower(u.team) = lower(:team)")
  List<UltiDataRepository.LeagueYear> findDistinctLeagueYearsForTeam(@Param("team") String team);

  interface LeagueYear {
    League getLeague();
    short getYear();
  }
}
