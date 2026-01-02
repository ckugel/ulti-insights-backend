package com.kugel.ulti_insights.Search;

import com.kugel.ulti_insights.Models.Player.Player;
import com.kugel.ulti_insights.Models.Player.PlayerService;
import com.kugel.ulti_insights.Models.Teams.TeamService;
import com.kugel.ulti_insights.Models.Teams.Teams;
import com.kugel.ulti_insights.Views.TeamSearchOption.TeamSearchOption;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

  @Autowired private TeamService teamService;
  @Autowired private PlayerService playerService;

  @GetMapping("/options")
  @Operation(summary = "Gets all the players and teams in the database")
  public SearchOptions getSearchOptions() {
    // could maybe be cached later lol, I do not care about space complexity

    List<String> playerList =
        playerService.getAll().stream().map(Player::getPlayerName).collect(Collectors.toList());
    List<Teams> teamList = teamService.getAll();

    ArrayList<TeamSearchOption> tso = new ArrayList<>();

    for (Teams t : teamList) {
      tso.add(new TeamSearchOption(t.getName(), t.getLeague()));
    }

    return new SearchOptions(playerList, tso);
  }

  @Operation(summary = "Get a list of teams that match a search query")
  @GetMapping("/teams")
  public List<TeamSearchOption> searchTeams(@RequestParam(required = true) String query) {
    query = query.toLowerCase();

    List<TeamSearchOption> teams = new ArrayList<>();

    List<Teams> allTeams = teamService.getAll();

    for (Teams t : allTeams) {
      if (t.getName().toLowerCase().contains(query)) {
        teams.add(new TeamSearchOption(t.getName(), t.getLeague()));
      }
    }
    return teams;
  }

  @Operation(summary = "Get a list of players that match a search query")
  @GetMapping("/players")
  public List<String> searchPlayers(@RequestParam(required = true) String query) {
    query = query.toLowerCase();

    List<String> players = new ArrayList<>();
    List<Player> allPlayers = playerService.getAll();

    for (Player p : allPlayers) {
      if (p.getPlayerName().toLowerCase().contains(query)) {
        players.add(p.getPlayerName());
      }
    }

    return players;
  }

  @Operation(summary = "gets all the players and teams")
  @GetMapping()
  public SearchOptions search(@RequestParam(required = false) String query) {
    if (query == null || query.isEmpty()) {
      return getSearchOptions();
    }

    List<String> players = searchPlayers(query);
    List<TeamSearchOption> teams = searchTeams(query);

    return new SearchOptions(players, teams);
  }
}
