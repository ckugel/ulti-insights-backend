package com.kugel.ulti_insights.PlayerEntrys;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerEntryController {

    //setup logger
    private static final Logger logger = LoggerFactory.getLogger(PlayerEntryController.class);

    @Autowired
    private UltiDataService service;

    @Operation(summary = "Get player entry", description = "Gets the entrys of the player in the database, has multiple players ")
    @ApiResponses(
             value = {
                     @ApiResponse(responseCode = "200", description = "Success got player entry"),
                     @ApiResponse(responseCode = "424", description = "Player not found")
             }
    )
    @GetMapping("/{username}")
    public ResponseEntity<List<PlayerEntry>> getPlayerEntry(@PathVariable String username) {
        username = "\"" + username + "\"";
        if (service.playerExists(username)) {
            List<UltiData> playerEntry = service.getPlayer(username);
            List<PlayerEntry> playerEntrys = new ArrayList<PlayerEntry>(playerEntry.size());
            for (UltiData ud : playerEntry) {
                PlayerEntry toAdd = new PlayerEntry(ud.getPlayerName(), ud.getTeam(), ud.getYearValue(), ud.getRankingValue());
                playerEntrys.add(toAdd);
            }
            return ResponseEntity.ok(playerEntrys);
        }
        else {
            return ResponseEntity.status(424).body(null);
        }
    }
}