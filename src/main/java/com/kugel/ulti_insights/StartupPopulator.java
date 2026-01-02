package com.kugel.ulti_insights;

import com.kugel.ulti_insights.Models.Player.Player;
import com.kugel.ulti_insights.Models.Player.PlayerService;
import com.kugel.ulti_insights.Models.TeamYears.TeamYears;
import com.kugel.ulti_insights.Models.Teams.TeamService;
import com.kugel.ulti_insights.Models.Teams.Teams;
import com.kugel.ulti_insights.Models.UltiData.UltiData;
import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class StartupPopulator implements CommandLineRunner {

  @Autowired private PlayerService playerService;
  @Autowired private TeamService teamService;

  private static final Logger log = LoggerFactory.getLogger(StartupPopulator.class);

  private static final HashMap<String, League> nameToLeague = new HashMap<>();

  static {
    // Preserve existing keys for backward compatibility
    nameToLeague.put("CollegeWomxns", League.WOMXNS_COLLEGE);
    nameToLeague.put("CollegeMens", League.OPEN_COLLEGE);
    nameToLeague.put("ClubMens", League.OPEN_CLUB);
    nameToLeague.put("ClubWomxns", League.WOMXNS_CLUB);
    nameToLeague.put("ClubMixed", League.MIXED_CLUB);
    nameToLeague.put("collegewomxns", League.WOMXNS_COLLEGE);
    nameToLeague.put("collegemens", League.OPEN_COLLEGE);
    nameToLeague.put("clubmens", League.OPEN_CLUB);
    nameToLeague.put("clubwomxns", League.WOMXNS_CLUB);
    nameToLeague.put("clubmixed", League.MIXED_CLUB);
    // Common variants
    nameToLeague.put("CollegeWomens", League.WOMXNS_COLLEGE);
    nameToLeague.put("ClubWomens", League.WOMXNS_CLUB);
    nameToLeague.put("collegewomens", League.WOMXNS_COLLEGE);
    nameToLeague.put("clubwomens", League.WOMXNS_CLUB);
  }

  private static String unquote(String s) {
    if (s == null) return null;
    String t = s.trim();
    if (t.length() >= 2
        && ((t.startsWith("\"") && t.endsWith("\"")) || (t.startsWith("'") && t.endsWith("'")))) {
      return t.substring(1, t.length() - 1).trim();
    }
    return t;
  }

  // Normalize token by stripping non-letters and lowercasing
  private static String normalizeToken(String s) {
    if (s == null) return "";
    return s.replaceAll("[^A-Za-z]", "").toLowerCase();
  }

  /**
   * Normalizes team names by removing gender/division suffixes
   *
   * @param teamName the original team name
   * @return normalized team name without gender suffixes
   */
  public static String normalizeTeamName(String teamName) {
    if (teamName == null || teamName.trim().isEmpty()) {
      return teamName;
    }

    String normalized = teamName.trim();

    // Remove common gender/division suffixes (case insensitive)
    String[] suffixesToRemove = {
      "\\s+Men's?\\s*$",
      "\\s+Women's?\\s*$",
      "\\s+Womxn'?s?\\s*$",
      "\\s+Mixed\\s*$",
      "\\s+Open\\s*$",
      "\\s+College\\s*$",
      "\\s+Mens||s*$",
      "\\s+Womens||s*$",
      "\\s+Club\\s*$",
      "\\s+Ultimate\\s*$",
      "\\s+Frisbee\\s*$",
      "\\s+\\(Men\\)\\s*$",
      "\\s+\\(Women\\)\\s*$",
      "\\s+\\(Mixed\\)\\s*$",
      "\\s+\\(Open\\)\\s*$",
      "\\s+-\\s*Men's?\\s*$",
      "\\s+-\\s*Women's?\\s*$"
    };

    for (String suffix : suffixesToRemove) {
      normalized = normalized.replaceAll("(?i)" + suffix, "");
    }

    return normalized.trim();
  }

  // Parse league from two tokens like ("College","Men's") or ("Club","Open")
  private static League parseLeagueTokens(String t1, String t2) {
    String a = normalizeToken(unquote(t1));
    String b = normalizeToken(unquote(t2));

    boolean isCollege = a.contains("college") || b.contains("college");

    // Determine division gender/category from the second token mostly
    String cat = (b.isEmpty() ? a : b);
    if (cat.contains("mixed")) {
      return isCollege ? League.OTHER : League.MIXED_CLUB; // no College Mixed in this dataset
    }
    if (cat.contains("womxn")
        || cat.contains("women")
        || cat.contains("womens")
        || cat.contains("womxns")) {
      return isCollege ? League.WOMXNS_COLLEGE : League.WOMXNS_CLUB;
    }
    if (cat.contains("men") || cat.contains("mens") || cat.contains("open")) {
      return isCollege ? League.OPEN_COLLEGE : League.OPEN_CLUB;
    }

    // Fallback to legacy mapping if present
    League legacy = nameToLeague.get(unquote(t1) + unquote(t2));
    if (legacy == null) legacy = nameToLeague.get((unquote(t1) + unquote(t2)).toLowerCase());

    if (legacy != null) return legacy;

    return League.OTHER;
  }

  // Parse league from tournament string with robust keyword checks
  private static League parseLeagueFromTournament(String tournament) {
    String t = tournament == null ? "" : tournament.toLowerCase();
    boolean isCollege = t.contains("college");
    boolean isClub = t.contains("club");

    if (t.contains("mixed")) {
      return isClub ? League.MIXED_CLUB : League.OTHER;
    }
    if (t.contains("womxn")
        || t.contains("women")
        || t.contains("women's")
        || t.contains("womens")
        || t.contains("womxns")) {
      return isCollege ? League.WOMXNS_COLLEGE : (isClub ? League.WOMXNS_CLUB : League.OTHER);
    }
    if (t.contains("men") || t.contains("men's") || t.contains("mens") || t.contains("open")) {
      return isCollege ? League.OPEN_COLLEGE : (isClub ? League.OPEN_CLUB : League.OTHER);
    }
    // If college/club present but no gender keyword, default to OTHER to avoid misclassification
    return League.OTHER;
  }

  @Override
  @Transactional
  public void run(String... args) {
    String filename = "src/main/resources/data.csv";
    String line;
    String delimiter = ",";

    HashMap<String, List<UltiData>> nameToData = new HashMap<>();
    ArrayList<UltiData> toAdd = new ArrayList<>();
    HashMap<String, Player> playersToSave = new HashMap<>();

    log.info("Starting csv reading");

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] data = line.split(delimiter);

        short year = Short.parseShort(data[1]);
        String tournament = data[2];
        League league = parseLeagueFromTournament(tournament);
        short quarter = Short.parseShort(data[3]);
        double year2 = Double.parseDouble(data[4]);
        short tier = Short.parseShort(data[5]);
        double multiplier = Double.parseDouble(data[6]);
        Long finishPosition = Long.parseLong(data[7]);
        String team = normalizeTeamName(data[8]);
        String playerName = unquote(data[9]);
        int current = 9;
        if (data.length > 13) {
          current += data.length - 13;
        }
        current++;
        double stat = Double.parseDouble(data[current]);
        current++;
        double share = Double.parseDouble(data[current]);
        current++;
        double rankingValue = Double.parseDouble(data[current]);

        UltiData ultidata =
            new UltiData(
                year,
                tournament,
                quarter,
                year2,
                tier,
                multiplier,
                finishPosition,
                team,
                playerName,
                stat,
                share,
                rankingValue,
                league,
                0);

        // Reuse or create a Player instance for this name and attach the UltiData
        Player player =
            playersToSave.getOrDefault(
                playerName, new Player(playerName, new ArrayList<>(), new ArrayList<>()));
        player.addUltiDataEntry(ultidata);

        toAdd.add(ultidata);
        playersToSave.put(player.getPlayerName(), player);

        if (nameToData.containsKey(ultidata.getName())) {
          nameToData.get(ultidata.getName()).add(ultidata);
        } else {
          ArrayList<UltiData> arr = new ArrayList<>();
          arr.add(ultidata);
          nameToData.put(ultidata.getName(), arr);
        }
      }

      log.info("finished reading the csv");

      for (UltiData ud : toAdd) {
        double displayValue = ud.getRankingValue();
        for (UltiData ud2 : nameToData.get(ud.getName())) {
          if (ud2.getYearValueTwo() > 0) {
            if (ud2.getYearValueTwo() < ud.getYearValueTwo()) {
              displayValue += ud2.getRankingValue();
            }
          }
        }
        ud.setDisplayValue(displayValue);
      }

      // Save all players and their UltiData at once via cascade
      playerService.saveAll(new ArrayList<>(playersToSave.values()));

      log.info("finished all saves");
    } catch (IOException e) {
      log.error("Could not open file: {} (wd: {})", filename, new File(".").getAbsolutePath(), e);
    }

    log.info("Now reading roster");

    // Roster database parsing and building the Teams and TeamYears
    String rosterFile = "src/main/resources/rosterFile.csv";
    ArrayList<Player> rosterPlayersToCreate = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(rosterFile))) {
      br.readLine();
      List<String[]> rosterLines = new ArrayList<>();
      while ((line = br.readLine()) != null) {
        rosterLines.add(line.split(delimiter));
      }
      log.info("done reading roster");

      // Preload caches
      HashMap<String, Teams> teamCache = new HashMap<>();
      HashMap<String, Player> playerCache = new HashMap<>();
      HashMap<String, TeamYears> teamYearCache = new HashMap<>();

      // Bulk fetch all existing players referenced in the roster to avoid per-row lookups
      Set<String> rosterPlayerNames =
          rosterLines.stream().map(arr -> unquote(arr[0])).collect(Collectors.toSet());
      List<Player> existingPlayers = playerService.findAllByNamesIgnoreCase(rosterPlayerNames);
      for (Player p : existingPlayers) {
        playerCache.put(p.getPlayerName(), p);
      }
      // Create missing players in-memory and queue them for a single batch save
      for (String pname : rosterPlayerNames) {
        if (!playerCache.containsKey(pname)) {
          Player np = new Player(pname, new ArrayList<>(), new ArrayList<>());
          rosterPlayersToCreate.add(np);
          playerCache.put(pname, np);
        }
      }

      // Persist any newly created players BEFORE associating them to TeamYears
      if (!rosterPlayersToCreate.isEmpty()) {
        List<Player> managedNewPlayers = playerService.saveAll(rosterPlayersToCreate);
        // Refresh cache with managed instances returned by saveAll
        for (Player mp : managedNewPlayers) {
          playerCache.put(mp.getPlayerName(), mp);
        }
      }

      // Now build Teams/TeamYears and associate managed Player instances
      for (String[] data : rosterLines) {
        String playerName = unquote(data[0]);
        String teamName = normalizeTeamName(data[1]);
        short year = (short) Double.parseDouble(data[2]);
        League league = parseLeagueTokens(data[3], data[4]);
        if (league == null || league == League.OTHER) {
          log.warn(
              "Unknown league mapping for roster row: team='{}', player='{}', rawTokens='{}','{}'",
              teamName,
              playerName,
              data[3],
              data[4]);
          continue; // skip invalid/unknown league rows to prevent cross-division lumping
        }

        String teamKey = teamName + league;
        Teams team =
            teamCache.computeIfAbsent(teamKey, k -> new Teams(teamName, new ArrayList<>(), league));

        String teamYearKey = teamKey + year;
        TeamYears teamYear = teamYearCache.get(teamYearKey);
        if (teamYear == null) {
          if (team.getTeamYears() != null) {
            for (TeamYears ty : team.getTeamYears()) {
              if (ty.getYearValue() == year) {
                teamYear = ty;
                break;
              }
            }
          }
          if (teamYear == null) {
            teamYear = new TeamYears(year, team, year);
            if (team.getTeamYears() == null) {
              team.setTeamYears(new ArrayList<>());
            }
            team.getTeamYears().add(teamYear);
          }
          teamYearCache.put(teamYearKey, teamYear);
        }

        // Get Player from cache (managed instance)
        Player player = playerCache.get(playerName);
        if (player == null) {
          // Fallback (should not happen due to preloading)
          player = new Player(playerName, new ArrayList<>(), new ArrayList<>());
          Player managed = playerService.savePlayer(player);
          playerCache.put(playerName, managed);
          player = managed;
        }

        // Link both sides using helpers
        teamYear.addPlayer(player);
      }

      // Serialize Teams (cascades to TeamYears). TeamYears -> players does not PERSIST, only MERGE.
      ArrayList<Teams> teamsToSave = new ArrayList<>(teamCache.values());

      log.info("now saving all");
      teamService.saveAll(teamsToSave);

    } catch (IOException e) {
      log.error("Could not open file: {} (wd: {})", rosterFile, new File(".").getAbsolutePath(), e);
    }

    log.info("Done saving data");
  }
}
