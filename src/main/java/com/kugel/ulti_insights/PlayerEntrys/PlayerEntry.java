package com.kugel.ulti_insights.PlayerEntrys;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Does not get saved in the database but is used for easy formatting back to the client
public class PlayerEntry {
    private String name;
    private String team;
    private short year;
    private Double rankingValue;

    public PlayerEntry() {
    }

    public PlayerEntry(String name, String team, short year, Double rankingValue) {
        this.name = name;
        this.team = team;
        this.year = year;
        this.rankingValue = rankingValue;
    }

    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    public Double getRankingValue() {
        return rankingValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setRankingValue(Double rankingValue) {
        this.rankingValue = rankingValue;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "PlayerEntry{" +
                "name='" + name + '\'' +
                ", team='" + team + '\'' +
                ", year=" + year +
                ", rankingValue=" + rankingValue +
                '}';
    }
}
