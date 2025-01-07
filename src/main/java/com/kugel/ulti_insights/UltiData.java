package com.kugel.ulti_insights;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UltiData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private short year_value;
    private String tournament;
    private short quarter;
    private double year_value_two;
    private short tier;
    private double multiplier;
    private Long finishPosition;
    private String team;
    private String playerName;
    private double stat;
    private double share;
    private double rankingValue;

    public UltiData() {
    }

    public UltiData(short year_value, String Tournament, short quarter, double year_value_two, short tier, double multiplier, Long finishPosition, String team, String PlayerName, double stat, double share, double rankingValue) {
        this.year_value = year_value;
        this.tournament = Tournament;
        this.quarter = quarter;
        this.year_value_two = year_value_two;
        this.tier = tier;
        this.multiplier = multiplier;
        this.finishPosition = finishPosition;
        this.team = team;
        this.playerName = PlayerName;
        this.stat = stat;
        this.share = share;
        this.rankingValue = rankingValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public short getYear_value() {
        return year_value;
    }

    public void setYear_value(short year_value) {
        this.year_value = year_value;
    }

    public double getYear_value_two() {
        return year_value_two;
    }

    public void setYear_value_two(double year_value_two) {
        this.year_value_two = year_value_two;
    }

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

    public short getQuarter() {
        return quarter;
    }

    public void setQuarter(short quarter) {
        this.quarter = quarter;
    }

    public short getTier() {
        return tier;
    }

    public void setTier(short tier) {
        this.tier = tier;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public Long getFinishPosition() {
        return finishPosition;
    }

    public void setFinishPosition(Long finishPosition) {
        this.finishPosition = finishPosition;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public double getStat() {
        return stat;
    }

    public void setStat(double stat) {
        this.stat = stat;
    }

    public double getShare() {
        return share;
    }

    public void setShare(double share) {
        this.share = share;
    }

    public double getRankingValue() {
        return rankingValue;
    }

    public void setRankingValue(double rankingValue) {
        this.rankingValue = rankingValue;
    }
}
