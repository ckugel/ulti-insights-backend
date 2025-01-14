package com.kugel.ulti_insights.Search;

import com.kugel.ulti_insights.UltiData;
import com.kugel.ulti_insights.UltiDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private UltiDataService service;

    @GetMapping("/options")
    public SearchOptions getSearchOptions() {
        // get all unique teams and players
        HashSet<String> players = new HashSet<>();
        HashSet<String> teams = new HashSet<>();

        // could maybe be cached later lol, I do not care about space complexity

        for (UltiData ud : service.getAll()) {
            players.add(ud.getPlayerName());
            teams.add(ud.getTeam());
        }

        ArrayList<String> playerList = new ArrayList<>(players);
        ArrayList<String> teamList = new ArrayList<>(teams);

        playerList.addAll(players);

        teamList.addAll(teams);

        return new SearchOptions(playerList, teamList);
    }

    @GetMapping()
    public SearchOptions search(@RequestParam(required = false) String query) {
        if (query == null || query.isEmpty()) {
            return getSearchOptions();
        }
        query = query.toLowerCase();
        HashSet<String> players = new HashSet<>();
        HashSet<String> teams = new HashSet<>();

        // could maybe be cached later lol, I do not care about space complexity

        for (UltiData ud : service.getAll()) {
            players.add(ud.getPlayerName());
            teams.add(ud.getTeam());
        }

        ArrayList<String> playerList = new ArrayList<>();
        ArrayList<String> teamList = new ArrayList<>();

        for (String player : players) {
            if (player.toLowerCase().contains(query)) {
                playerList.add(player);
            }
        }

        for (String team : teams) {
            if (team.toLowerCase().contains(query)) {
                teamList.add(team);
            }
        }

        return new SearchOptions(playerList, teamList);
    }
}
