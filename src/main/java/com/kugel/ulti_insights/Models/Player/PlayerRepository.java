package com.kugel.ulti_insights.Models.Player;

import java.util.Optional;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends JpaRepository<Player, String> {
  Optional<Player> findByPlayerNameIgnoreCase(String playerName);

  @EntityGraph(attributePaths = {"teamYears", "teamYears.team"})
  Optional<Player> findWithTeamYearsAndTeamByPlayerNameIgnoreCase(String playerName);

  // Bulk case-insensitive fetch of players by name
  @Query("select p from player p where lower(p.playerName) in :lowerNames")
  List<Player> findAllByPlayerNameLowerIn(@Param("lowerNames") Collection<String> lowerNames);
}
