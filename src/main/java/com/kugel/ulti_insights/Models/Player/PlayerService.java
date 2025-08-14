package com.kugel.ulti_insights.Models.Player;

import com.kugel.ulti_insights.Models.TeamYears.TeamYears;
import com.kugel.ulti_insights.Models.Teams.Teams;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
  @Autowired private PlayerRepository playerRepository;

  private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

  public Player savePlayer(Player player) {
    log.debug("Saving player: {}", player.getPlayerName());
    return playerRepository.save(player);
  }

  public List<Player> saveAll(List<Player> players) {
    log.debug("Saving {} players", players == null ? 0 : players.size());
    return playerRepository.saveAll(players);
  }

  /** Determines whether a player with a given name exists */
  public boolean playerExists(String username) {
    boolean exists = playerRepository.findByPlayerNameIgnoreCase(username).isPresent();
    log.trace("playerExists('{}') -> {}", username, exists);
    return exists;
  }

  /**
   * Fetch a player with their team years and each team eager-loaded to avoid N+1 when traversing.
   */
  public Optional<Player> getPlayerWithTeamYearsAndTeam(String name) {
    log.trace("Fetching player with team years and team: {}", name);
    return playerRepository.findWithTeamYearsAndTeamByPlayerNameIgnoreCase(name);
  }

  /**
   * Gets the teams a player has been on.
   *
   * @param playerName the playerName of the player to search through.
   * @return Optional.Empty if the player doesn't exist and Optional.of(List.empty) if the player
   *     exists but wasn't on any team
   */
  public Optional<List<Teams>> getTeamsPlayerOn(String playerName) {
    log.trace("Getting teams for player: {}", playerName);
    Optional<Player> player = this.getPlayerFromName(playerName);
    if (player.isPresent()) {
      return Optional.of(getTeamsPlayerOn(player.get()));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Gets all the players in the database
   *
   * @return all the players
   */
  public List<Player> getAll() {
    log.trace("Fetching all players");
    return playerRepository.findAll();
  }

  /**
   * Gets the teams a player has been on.
   *
   * @param player the player to search through.
   * @return List.empty if the player exists but wasn't on any team
   */
  public List<Teams> getTeamsPlayerOn(Player player) {
    return player.getTeamYears().stream().map(TeamYears::getTeam).collect(Collectors.toList());
  }

  /**
   * Gets a player from a name
   *
   * @param name the name of the player to search for
   * @return the player if it exits.
   */
  public Optional<Player> getPlayerFromName(String name) {
    log.trace("Finding player by name (ignore-case): {}", name);
    return playerRepository.findByPlayerNameIgnoreCase(name);
  }

  /**
   * Bulk fetch players by a set of names (case-insensitive) using a single query.
   */
  public List<Player> findAllByNamesIgnoreCase(Set<String> names) {
    if (names == null || names.isEmpty()) {
      log.trace("findAllByNamesIgnoreCase called with empty set");
      return List.of();
    }
    Set<String> lower = names.stream().map(String::toLowerCase).collect(Collectors.toSet());
    log.trace("Bulk fetching {} players by name (ignore-case)", lower.size());
    return playerRepository.findAllByPlayerNameLowerIn(lower);
  }
}
