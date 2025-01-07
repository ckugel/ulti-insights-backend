package com.kugel.ulti_insights;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UltiDataRepository extends JpaRepository<UltiData, Long> {


    List<UltiData> findAllByPlayerNameIgnoreCase(String playerName);

    List<UltiData> findAllByTeamIgnoreCase(String teamName);
}
