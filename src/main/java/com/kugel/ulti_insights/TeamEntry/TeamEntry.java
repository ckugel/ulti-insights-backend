package com.kugel.ulti_insights.TeamEntry;

import com.kugel.ulti_insights.PlayerEntrys.PlayerEntry;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.List;

public class TeamEntry {
    private String teamName;
    private short year;

    private List<PlayerEntry> players;

    public TeamEntry() {
        players = new ArrayList<>();
    }

    public TeamEntry(String teamName, short year, List<PlayerEntry> players) {
        this.teamName = teamName;
        this.year = year;
        this.players = players;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public List<PlayerEntry> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerEntry> players) {
        this.players = players;
    }
}
