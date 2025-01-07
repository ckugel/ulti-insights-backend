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
}
