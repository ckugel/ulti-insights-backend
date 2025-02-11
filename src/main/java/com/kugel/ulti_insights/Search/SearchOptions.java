package com.kugel.ulti_insights.Search;

import java.util.List;
import com.kugel.ulti_insights.TeamEntry.TeamEntry;

public class SearchOptions {
    private List<String> players;
    private List<TeamEntry> teams; // need to make this a list of team objects which contain the team name and the league it is in

    public SearchOptions(List<String> players, List<TeamEntry> teams) {
        this.players = players;
        this.teams = teams;
    }

    public List<TeamEntry> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamEntry> teams) {
        this.teams = teams;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
