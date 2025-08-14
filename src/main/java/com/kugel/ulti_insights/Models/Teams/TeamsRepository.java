package com.kugel.ulti_insights.Models.Teams;

import com.kugel.ulti_insights.League;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamsRepository extends JpaRepository<Teams, Long> {
  Optional<List<Teams>> findAllByNameIgnoreCase(String name);

  List<Teams> findByLeague(League league);

  Optional<Teams> findByNameIgnoreCaseAndLeague(String name, League league);

  List<Teams> findAll();
}
