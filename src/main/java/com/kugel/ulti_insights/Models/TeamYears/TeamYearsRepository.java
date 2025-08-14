package com.kugel.ulti_insights.Models.TeamYears;

import com.kugel.ulti_insights.League;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kugel.ulti_insights.Models.TeamYears.LeagueYearProjection;

public interface TeamYearsRepository extends JpaRepository<TeamYears, Long> {
  @EntityGraph(attributePaths = {"players", "team"})
  List<TeamYears> findByTeam_NameIgnoreCaseAndTeam_League(String name, League league);

  @EntityGraph(attributePaths = {"players", "team"})
  List<TeamYears> findByTeam_NameIgnoreCase(String name);

  @EntityGraph(attributePaths = {"players", "team"})
  Optional<TeamYears> findByTeam_NameIgnoreCaseAndTeam_LeagueAndYearValue(
      String name, League league, short yearValue);

  // Year-only for a given team + league
  @Query(
      "select distinct ty.yearValue from teamyears ty join ty.team t " +
      "where lower(t.name) = lower(:name) and t.league = :league order by ty.yearValue")
  List<Short> findDistinctYearValuesByTeamNameAndLeague(
      @Param("name") String name, @Param("league") League league);

  // Distinct leagues that have this team name
  @Query("select distinct t.league from teams t where lower(t.name) = lower(:name)")
  List<League> findDistinctLeaguesByTeamName(@Param("name") String name);

  // League + year pairs for a team name (compact projection)
  @Query(
      "select t.league as league, ty.yearValue as yearValue from teamyears ty " +
      "join ty.team t where lower(t.name) = lower(:name) order by t.league, ty.yearValue")
  List<LeagueYearProjection> findLeagueYearsByTeamName(@Param("name") String name);
}
