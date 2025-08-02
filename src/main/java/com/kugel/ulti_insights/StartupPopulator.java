package com.kugel.ulti_insights;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupPopulator implements CommandLineRunner {

  @Autowired private UltiDataService service;
  private static final HashMap<String, League> nameToLeague = new HashMap();

  static {
    nameToLeague.put("", MENS_COLLEGE_D1);
    nameToLeague.put("", MENS_COLLEGE_D3);
    nameToLeague.put("ClubMens", MENS_CLUB);
    nameToLeague.put("ClubWomxns", WOMENS_CLUB);
    nameToLeague.put("ClubMixed", MIXED_CLUB);


  }

    public League leagueFromStrings(String Division, String Gender) {
	leagN
    }

  @Override
  public void run(String... args) throws Exception {
    String filename = "src/main/resources/data.csv";
    String line;
    String delimiter = ",";

    HashMap<String, List<UltiData>> nameToData = new HashMap();

    ArrayList<UltiData> toAdd = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      // skip first line as it is the header
      br.readLine();
      // read the csv into a ultidata object and save it to the database.
      // the csv header is given by:
      // "","Year","Tournament","Quarter","Year2","Tier","Multiplier","Finish.Position","Team","Player","Stat","Share","Rank.Value"
      while ((line = br.readLine()) != null) {
        String[] data = line.split(delimiter);

        short year = Short.parseShort(data[1]);
        String tournament = data[2];
        League league;
        if (tournament.contains("D1")) {
          // can be Mens or womens
          if (tournament.contains("Men's")) {
            league = League.MENS_COLLEGE_D1;
          } else {
            league = League.WOMENS_COLLEGE_D1;
          }
        } else if (tournament.contains("D3")) {
          // can be Mens or womens
          if (tournament.contains("Men's")) {
            league = League.MENS_COLLEGE_D3;
          } else {
            league = League.WOMENS_COLLEGE_D3;
          }
        } else if (tournament.contains("Club")) {
          // Can be Mens, Womens, Mixed
          if (tournament.contains("Men's")) {
            league = League.MENS_CLUB;
          } else if (tournament.contains("Women's")) {
            league = League.WOMENS_CLUB;
          } else {
            league = League.MIXED_CLUB;
          }
        } else if (tournament.contains("YCC")) {
          // can be U20, U17
          if (tournament.contains("U-20")) {
            // can be Mens, Womens, Mixed
            if (tournament.contains("Boys")) {
              league = League.MENS_YCC_U20;
            } else if (tournament.contains("Girls")) {
              league = League.WOMENS_YCC_U20;
            } else {
              league = League.MIXED_YCC_U20;
            }
          } else if (tournament.contains("U-17")) {
            // can be Mens, Womens, Mixed
            if (tournament.contains("Boys")) {
              league = League.MENS_YCC_U17;
            } else if (tournament.contains("Girls")) {
              league = League.WOMENS_YCC_U17;
            } else {
              league = League.MIXED_YCC_U17;
            }
          } else {
            league = League.OTHER;
          }
        } else {
          league = League.OTHER;
        }
        short quarter = Short.parseShort(data[3]);
        double year2 = Double.parseDouble(data[4]);
        short tier = Short.parseShort(data[5]);
        double multiplier = Double.parseDouble(data[6]);
        Long finishPosition = Long.parseLong(data[7]);
        String team = data[8];
        String playerName = data[9];
        // this is because of names with delimiters in the name, not a good fix tbh
        int current = 9;
        if (data.length > 13) {
          current += data.length - 13;
          // either fool proof or foolish
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
        toAdd.add(ultidata);
        if (nameToData.containsKey(ultidata.getPlayerName())) {
          nameToData.get(ultidata.getPlayerName()).add(ultidata);
        } else {
          ArrayList<UltiData> arr = new ArrayList<>();
          arr.add(ultidata);
          nameToData.put(ultidata.getPlayerName(), arr);
        }
      }

      for (UltiData ud : toAdd) {
        /**
         * Gets a ranking value from a year, team, and league. The ranking value is calculated by
         * summing the ranking values of that player leading up to that point. TODO: RESEARCH: Does
         * player name and yearValueTwo form a unique key?
         */
        double displayValue = ud.getRankingValue();
        for (UltiData ud2 : nameToData.get(ud.getPlayerName())) {
          if (ud2.getYearValueTwo() < ud.getYearValueTwo()) {
            displayValue += ud2.getRankingValue();
          }
        }

        ud.setDisplayValue(displayValue);
        service.saveUltiData(ud);
      }

    } catch (IOException e) {
      System.out.println("could not open file: " + filename);
      System.out.println("Working dir: " + new File(".").getAbsolutePath());
      e.printStackTrace();
    }

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] data = line.split(delimiter);
        String player = data[0];
        String team = data[1];
        String Year = Short.valueOf(data[2]);
        String Division = data[3];
        String Gender = data[4];
      }
    } catch (IOException e) {
    }
  }
}
