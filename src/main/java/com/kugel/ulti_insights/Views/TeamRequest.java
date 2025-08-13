package com.kugel.ulti_insights.Views;

import com.kugel.ulti_insights.League;
import com.kugel.ulti_insights.Views.TeamEntry.TeamEntry;
import java.util.List;

public record TeamRequest(String name, League league, List<TeamEntry> teamEntries) {}
