package com.kugel.ulti_insights.Search;

import java.util.List;

public class SearchOptions {
    private List<String> players;
    private List<String> teams;

    public SearchOptions(List<String> players, List<String> teams) {
        this.players = players;
        this.teams = teams;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
