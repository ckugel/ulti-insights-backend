package com.kugel.ulti_insights.Views.PlayerEntrys;

import com.kugel.ulti_insights.Models.Player.Player;
import com.kugel.ulti_insights.Models.Player.PlayerService;
import com.kugel.ulti_insights.Models.TeamYears.TeamYears;
import com.kugel.ulti_insights.Models.UltiData.UltiData;
import com.kugel.ulti_insights.Models.UltiData.UltiDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player")
public class PlayerEntryController {

  // setup logger
  private static final Logger logger = LoggerFactory.getLogger(PlayerEntryController.class);

  @Autowired private UltiDataService service;
  @Autowired private PlayerService playerService;

  @Operation(
      summary = "Get player entry",
      description = "Gets the entrys of the player in the database, has multiple players ")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success got player entry"),
        @ApiResponse(responseCode = "424", description = "Player not found")
      })
  @GetMapping("/old/{username}")
  public ResponseEntity<List<PlayerEntry>> getPlayerEntryOld(@PathVariable String username) {
    // No quoting, names are stored normalized
    if (service.playerExists(username)) {
      List<UltiData> playerEntry = service.getPlayer(username);
      List<PlayerEntry> playerEntrys = new ArrayList<PlayerEntry>(playerEntry.size());
      for (UltiData ud : playerEntry) {
        PlayerEntry toAdd =
            new PlayerEntry(
                ud.getName(),
                ud.getTeam(),
                ud.getYearValue(),
                ud.getLeague(),
                ud.getRankingValue(),
                ud.getYearValueTwo(),
                ud.getDisplayValue());
        playerEntrys.add(toAdd);
      }
      return ResponseEntity.ok(playerEntrys);
    } else {
      return ResponseEntity.status(424).body(null);
    }
  }

  @Operation(
      summary = "Get player entry",
      description = "Gets the entrys of the player in the database, has multiple players ")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success got player entry"),
        @ApiResponse(responseCode = "424", description = "Player not found")
      })
  @GetMapping("/{username}")
  @Transactional(readOnly = true)
  public ResponseEntity<List<PlayerEntry>> getPlayerEntry(@PathVariable String username) {
    Optional<Player> playerRes = playerService.getPlayerWithTeamYearsAndTeam(username);
    if (playerRes.isPresent()) {
      Player player = playerRes.get();
      logger.debug("Building PlayerEntry list for {}", player.getPlayerName());
      List<TeamYears> teamYears = player.getTeamYears();
      if (teamYears == null || teamYears.isEmpty()) {
        // Fallback: build from UltiData entries when no roster mapping exists
        List<UltiData> entries = service.getPlayer(username);
        if (entries == null || entries.isEmpty()) {
          return ResponseEntity.ok(List.of());
        }
        List<PlayerEntry> fromData = new ArrayList<>(entries.size());
        entries.stream()
            .sorted(Comparator.comparing(UltiData::getYearValue))
            .forEach(
                ud -> fromData.add(
                    new PlayerEntry(
                        ud.getName(),
                        ud.getTeam(),
                        ud.getYearValue(),
                        ud.getLeague(),
                        ud.getRankingValue(),
                        ud.getYearValueTwo(),
                        ud.getDisplayValue())));
        return ResponseEntity.ok(fromData);
      }
      List<PlayerEntry> playerEntrys = new ArrayList<>(teamYears.size());
      for (TeamYears ty : teamYears) {
        short year = ty.getYearValue();
        Double dv = player.getDisplayValueForYear(year);
        double safeDisplay = dv != null ? dv : 0.0;
        PlayerEntry toAdd =
            new PlayerEntry(
                player.getPlayerName(),
                ty.getTeam().getName(),
                year,
                ty.getTeam().getLeague(),
                player.getRankingValueForYear(year),
                ty.getYearTwo(),
                safeDisplay);
        playerEntrys.add(toAdd);
      }
      return ResponseEntity.ok(playerEntrys);
    } else {
      return ResponseEntity.status(424).body(null);
    }
  }
}
