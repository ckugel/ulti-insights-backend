package com.kugel.ulti_insights.Views.TeamEntry;

import com.kugel.ulti_insights.League;
import com.kugel.ulti_insights.Models.Player.Player;
import com.kugel.ulti_insights.Models.TeamYears.TeamYears;
import com.kugel.ulti_insights.Models.TeamYears.TeamYearsService;
import com.kugel.ulti_insights.Models.Teams.TeamService;
import com.kugel.ulti_insights.Views.PlayerEntrys.PlayerEntry;
import com.kugel.ulti_insights.Models.UltiData.UltiDataRepository;
import com.kugel.ulti_insights.Models.UltiData.UltiDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/team")
public class TeamEntryController {

  @Autowired private TeamService service;
  @Autowired private TeamYearsService teamYearsService;
  @Autowired private UltiDataService ultiDataService;

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
    List<String> toRet = new ArrayList<>();
    toRet.add("all");
    // Union years from UltiData stats and TeamYears roster
    Set<Short> years = new TreeSet<>();
    years.addAll(ultiDataService.getDistinctYearsForTeamAndLeague(name, league));
    years.addAll(teamYearsService.getYearValuesByTeamNameAndLeague(name, league));
    if (years.isEmpty()) {
      return ResponseEntity.status(424).body(null);
    }
    for (Short yr : years) {
      toRet.add(String.valueOf(yr));
    }
    if (toRet.size() == 1) {
      toRet.clear();
    }
    return ResponseEntity.ok(toRet);
  }

  public List<TeamEntry> makeTeamEntrysFromTeamYears(
      List<TeamYears> teamYears, String teamName) {
    List<TeamEntry> toRet = new ArrayList<>();
    for (TeamYears ty : teamYears) {
      League resolvedLeague = ty.getTeam().getLeague();
      TeamEntry te = new TeamEntry(teamName, ty.getYearValue(), new ArrayList<>(), resolvedLeague);
      List<Player> players = ty.getPlayers();
      for (Player p : players) {
        Double dv = p.getDisplayValueForYear(ty.getYearValue());
        double safeDisplay = dv != null ? dv : 0.0;
        PlayerEntry toAdd =
            new PlayerEntry(
                p.getPlayerName(),
                teamName,
                ty.getYearValue(),
                resolvedLeague,
                p.getRankingValueForYear(ty.getYearValue()),
                ty.getYearTwo(),
                safeDisplay);
        te.addPlayerEntry(toAdd);
      }
      toRet.add(te);
    }
    return toRet;
  }

  @Operation(
      summary = "Get a team that matches the name and, if provided, league",
      description =
          "gets the necessary data for a given team page. Will return multiple teams if league is"
              + " ambiguous")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success got team entry"),
        @ApiResponse(responseCode = "424", description = "Team not found")
      })
  @GetMapping("/{name}")
  @Transactional(readOnly = true)
  public ResponseEntity<List<TeamEntry>> getTeamEntry(
      @PathVariable String name, @RequestParam League league) {
    if (league != null) {
      List<TeamYears> tys = teamYearsService.getTeamYearsByTeamNameAndLeague(name, league);
      if (tys.isEmpty()) {
        return ResponseEntity.status(424).body(null);
      }
      return ResponseEntity.ok(makeTeamEntrysFromTeamYears(tys, name));
    } else {
      List<TeamYears> tys = teamYearsService.getTeamYearsByTeamName(name);
      if (tys.isEmpty()) {
        return ResponseEntity.status(424).body(null);
      }
      return ResponseEntity.ok(makeTeamEntrysFromTeamYears(tys, name));
    }
  }

  @Operation(
      summary = "Get team entry for year",
      description = "gets the necessary data for a given team page in a given year")
  @GetMapping("/{name}/{year}")
  @Transactional(readOnly = true)
  public ResponseEntity<TeamEntry> getTeamEntryYear(
      @PathVariable String name,
      @PathVariable short year,
      @RequestParam League league) {
    Optional<TeamYears> teamYearOpt =
        teamYearsService.getTeamByNameAndLeagueAndYear(name, year, league);
    if (teamYearOpt.isEmpty()) {
      return ResponseEntity.status(424).body(null);
    }
    TeamYears teamYear = teamYearOpt.get();
    List<TeamEntry> teamEntry = makeTeamEntrysFromTeamYears(List.of(teamYear), name);
    return ResponseEntity.ok(teamEntry.getFirst());
  }

  @Operation(
      summary = "Get leagues of team",
      description = "gets all the leagues that the given team has a team for")
  @GetMapping("/leagues/{name}")
  public ResponseEntity<List<League>> getTeamLeagues(@PathVariable String name) {
    // Union leagues from UltiData and TeamYears
    Set<League> leagues = new LinkedHashSet<>();
    leagues.addAll(ultiDataService.getDistinctLeaguesForTeam(name));
    leagues.addAll(teamYearsService.getLeaguesByTeamName(name));
    if (leagues.isEmpty()) {
      return ResponseEntity.status(424).body(null);
    } else {
      return ResponseEntity.ok(new ArrayList<>(leagues));
    }
  }

  @Operation(
      summary = "Get (league, year) pairs for team",
      description = "fast path using a projection to get available leagues and years for a team")
  @GetMapping("/league-years/{name}")
  public ResponseEntity<List<LeagueYearDTO>> getLeagueYears(@PathVariable String name) {
    List<UltiDataRepository.LeagueYear> pairs = ultiDataService.getDistinctLeagueYearsForTeam(name);
    List<LeagueYearDTO> dto =
        pairs.stream()
            .map(p -> new LeagueYearDTO(p.getLeague(), p.getYear()))
            .toList();
    // Add pairs from TeamYears as well
    List<LeagueYearDTO> teamYearDTOs =
        teamYearsService.getLeagueYearsByTeamName(name).stream()
            .map(p -> new LeagueYearDTO(p.getLeague(), p.getYearValue()))
            .toList();
    // Deduplicate while preserving order
    LinkedHashSet<String> seen = new LinkedHashSet<>();
    List<LeagueYearDTO> combined =
        Stream.concat(dto.stream(), teamYearDTOs.stream())
            .filter(p -> seen.add(p.getLeague().name() + ":" + p.getYear()))
            .collect(Collectors.toList());
    if (combined.isEmpty()) {
      return ResponseEntity.status(424).body(null);
    }
    return ResponseEntity.ok(combined);
  }
}
