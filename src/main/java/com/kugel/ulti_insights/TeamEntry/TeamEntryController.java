package com.kugel.ulti_insights.TeamEntry;

import com.kugel.ulti_insights.League;
import com.kugel.ulti_insights.PlayerEntrys.PlayerEntry;
import com.kugel.ulti_insights.UltiData;
import com.kugel.ulti_insights.UltiDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/team")
public class TeamEntryController {

  @Autowired private UltiDataService service;

  @Operation(
      summary = "Get the available years",
      description = "gets the years for a given team with league name")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success got team years"),
        @ApiResponse(responseCode = "424", description = "Team years not found")
      })
  @GetMapping("/years/{name}/{league}")
  public ResponseEntity<List<String>> getTeamYears(
      @PathVariable String name, @PathVariable League league) {
    name = "\"" + name + "\"";
    List<String> toRet = new ArrayList();
    toRet.add("all");
    List<UltiData> playerEntry = service.getTeamByLeagueAndName(name, league);
    for (UltiData ud : playerEntry) {
      if (!toRet.contains(String.valueOf(ud.getYearValue()))) {
        toRet.add(String.valueOf(ud.getYearValue()));
      }
    }

    if (playerEntry.isEmpty()) {
      return ResponseEntity.status(424).body(null);
    } else {
      if (toRet.size() == 1) {
        // clear the array if the only element is all
        toRet.clear();
      }
      return ResponseEntity.ok(toRet);
    }
  }

  @Operation(
      summary = "Get team entry",
      description = "gets the necessary data for a given team page")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success got team entry"),
        @ApiResponse(responseCode = "424", description = "Team not found")
      })
  @GetMapping("/{name}")
  public ResponseEntity<TeamEntry> getTeamEntry(
      @PathVariable String name, @RequestParam(required = false) League league) {
    name = "\"" + name + "\"";
    List<UltiData> playerEntry;
    if (league != null) {
      playerEntry = service.getTeamByLeagueAndName(name, league);
    } else {
      playerEntry = service.getTeam(name);
    }
    if (!playerEntry.isEmpty()) {
      List<PlayerEntry> playerEntrys = new ArrayList<>(playerEntry.size());
      for (UltiData ud : playerEntry) {
        PlayerEntry toAdd =
            new PlayerEntry(
                ud.getPlayerName().substring(1, ud.getPlayerName().length() - 1),
                ud.getTeam().substring(1, ud.getTeam().length() - 1),
                ud.getYearValue(),
                ud.getDisplayValue(),
                ud.getLeague().toString());

        playerEntrys.add(toAdd);
      }
      TeamEntry teamEntry =
          new TeamEntry(name, playerEntry.get(0).getYearValue(), playerEntrys, league);
      return ResponseEntity.ok(teamEntry);
    } else {
      return ResponseEntity.status(424).body(null);
    }
  }

  @Operation(
      summary = "Get team entry for year",
      description = "gets the necessary data for a given team page in a given year")
  @GetMapping("/{name}/{year}")
  public ResponseEntity<TeamEntry> getTeamEntryYear(
      @PathVariable String name,
      @PathVariable short year,
      @RequestParam(required = false) League league) {
    name = "\"" + name + "\"";
    List<UltiData> playerEntry;
    if (league != null) {
      playerEntry = service.getTeamByLeagueAndNameAndYear(name, year, league);
    } else {
      playerEntry = service.getTeamYear(name, year);
    }
    if (!playerEntry.isEmpty()) {
      List<PlayerEntry> playerEntrys = new ArrayList<>(playerEntry.size());
      for (UltiData ud : playerEntry) {
        PlayerEntry toAdd =
            new PlayerEntry(
                ud.getPlayerName().substring(1, ud.getPlayerName().length() - 1),
                ud.getTeam().substring(1, ud.getTeam().length() - 1),
                ud.getYearValue(),
                ud.getDisplayValue(),
                ud.getLeague().toString());
        playerEntrys.add(toAdd);
      }
      TeamEntry teamEntry = new TeamEntry(name, year, playerEntrys, league);
      return ResponseEntity.ok(teamEntry);
    } else {
      return ResponseEntity.status(424).body(null);
    }
  }

  @Operation(
      summary = "Get leagues of team",
      description = "gets all the leagues that the given team has a team for")
  @GetMapping("/leagues/{name}")
  public ResponseEntity<List<League>> getTeamLeagues(@PathVariable String name) {
    name = "\"" + name + "\"";
    List<League> leagues =
        service.getTeam(name).stream().map(UltiData::getLeague).distinct().toList();
    if (leagues.isEmpty()) {
      return ResponseEntity.status(424).body(null);
    } else {
      return ResponseEntity.ok(leagues);
    }
  }
}
