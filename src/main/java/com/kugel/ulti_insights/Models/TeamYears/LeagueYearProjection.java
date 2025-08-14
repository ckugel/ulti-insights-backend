package com.kugel.ulti_insights.Models.TeamYears;

import com.kugel.ulti_insights.League;

/**
 * Closed projection for (league, yearValue) pairs for a given team name.
 */
public interface LeagueYearProjection {
  League getLeague();
  short getYearValue();
}

