package com.kugel.ulti_insights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UltiDataService {

    @Autowired
    private UltiDataRepository repository;

    public UltiData saveUltiData(UltiData ultidata) {
        return repository.save(ultidata);
    }

    public boolean playerExists(String playerName) {
        return !repository.findAllByPlayerNameIgnoreCase(playerName).isEmpty();
    }

    public List<UltiData> getPlayer(String playerName) {
        return repository.findAllByPlayerNameIgnoreCase(playerName);
    }

    public List<UltiData> getTeam(String teamName) {
        return repository.findAllByTeamIgnoreCase(teamName);
    }

    public boolean teamExists(String name) {
        return !repository.findAllByTeamIgnoreCase(name).isEmpty();
    }

    public List<UltiData> getTeamYear(String teamName, short year) {
        return repository.findAllByTeamIgnoreCaseAndYearValue(teamName, year);
    }

    public List<UltiData> getTeamByLeagueAndNameAndYear(String name, short year, League league) {
        return repository.findAllByTeamIgnoreCaseAndYearValueAndLeague(name, year, league);
    }

    public List<UltiData> getAll() {
        return repository.findAll();
    }

    public List<UltiData> getTeamByLeagueAndName(String name, League league) {
        return repository.findByTeamIgnoreCaseAndLeague(name, league);
    }
}
