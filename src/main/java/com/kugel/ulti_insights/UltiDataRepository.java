package com.kugel.ulti_insights;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UltiDataRepository extends JpaRepository<UltiData, Long> {


    List<UltiData> findAllByPlayerNameIgnoreCase(String playerName);

    List<UltiData> findAllByTeamIgnoreCase(String teamName);

     List<UltiData> findAllByTeamIgnoreCaseAndYearValueAndLeague(String teamName, short year, League league);

    List<UltiData> findAllByTeamIgnoreCaseAndYearValue(String teamName, short year_value);

    List<UltiData> findByLeague(League league);

    List<UltiData> findByTeamIgnoreCaseAndLeague(String name, League league);
}
