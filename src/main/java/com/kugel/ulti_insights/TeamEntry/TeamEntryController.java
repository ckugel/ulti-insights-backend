package com.kugel.ulti_insights.TeamEntry;

import com.kugel.ulti_insights.PlayerEntrys.PlayerEntry;
import com.kugel.ulti_insights.UltiData;
import com.kugel.ulti_insights.UltiDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamEntryController {

    @Autowired
    private UltiDataService service;

    @Operation(summary = "Get team entry", description = "gets the neccessary data for a given team page")
    @ApiResponses(
             value = {
                     @ApiResponse(responseCode = "200", description = "Success got team entry"),
                     @ApiResponse(responseCode = "424", description = "Team not found")
             }
    )
    @GetMapping("/{name}")
    public ResponseEntity<TeamEntry> getTeamEntry(@PathVariable String name) {
        name = "\"" + name + "\"";
        if (service.teamExists(name)) {
            List<UltiData> playerEntry = service.getTeam(name);
            List<PlayerEntry> playerEntrys = new ArrayList<PlayerEntry>(playerEntry.size());
            for (UltiData ud : playerEntry) {
                PlayerEntry toAdd = new PlayerEntry(ud.getPlayerName().substring(1, ud.getPlayerName().length() - 1), ud.getTeam().substring(1, ud.getTeam().length() - 1), ud.getYearValue(), ud.getRankingValue());
                playerEntrys.add(toAdd);
            }
            TeamEntry teamEntry = new TeamEntry(name, playerEntry.get(0).getYearValue(), playerEntrys);
            return ResponseEntity.ok(teamEntry);
        }
        else {
            return ResponseEntity.status(424).body(null);
        }
    }

    @Operation(summary = "Get team entry for year", description = "gets the neccessary data for a given team page in a given year")
    @GetMapping("/{name}/{year}")
    public ResponseEntity<TeamEntry> getTeamEntryYear(@PathVariable String name, @PathVariable short year) {
        name = "\"" + name + "\"";
        if (service.teamExists(name)) { //FIXME: check if exists for year as well
            List<UltiData> playerEntry = service.getTeamYear(name, year);
            List<PlayerEntry> playerEntrys = new ArrayList<PlayerEntry>(playerEntry.size());
            for (UltiData ud : playerEntry) {
                PlayerEntry toAdd = new PlayerEntry(ud.getPlayerName().substring(1, ud.getPlayerName().length() - 1), ud.getTeam().substring(1, ud.getTeam().length() - 1), ud.getYearValue(), ud.getRankingValue());
                playerEntrys.add(toAdd);
            }
            TeamEntry teamEntry = new TeamEntry(name, year, playerEntrys);
            return ResponseEntity.ok(teamEntry);
        }
        else {
            return ResponseEntity.status(424).body(null);
        }
    }
}