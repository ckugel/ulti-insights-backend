# Ulti Insights Backend

Ulti Insights is a Spring Boot REST API that aggregates ultimate frisbee player and team statistics. It ingests CSV data at startup, normalizes team and league info, and exposes endpoints to query players, teams, seasons, and leagues.

## Key features
- CSV ingestion on startup (dev/test profiles) from src/main/resources/data.csv and rosterFile.csv
- Normalized model: Player, Teams, TeamYears, UltiData, and League enum
- Team and player views with precomputed display/ranking values
- Search endpoints for teams and players
- H2 in-memory database with schema auto-creation

## Tech stack
- Java + Spring Boot (Web, Data JPA, Transactions)
- H2 in-memory DB (dev/test)
- Maven wrapper (mvnw)

## Run locally
Requirements: Java 17+ and Maven (or use the Maven wrapper provided).

- Start the API with the dev profile to seed sample data:
  - Unix: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
  - Windows: `mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev`
- Server listens on http://localhost:8080 (CORS is configured for a frontend on 3000).
- H2 console (optional): http://localhost:8080/h2-console

Notes
- The DB is in-memory and resets on each run (spring.jpa.hibernate.ddl-auto=create-drop).
- StartupPopulator only runs with the dev or test profile active.

## API overview (examples)
- GET /team/years/{name}/{league}
  - Distinct years a team competed in a league.
- GET /team/{name}
  - Team entries (optionally filter by ?league=LEAGUE_NAME).
- GET /team/{name}/{year}?league=LEAGUE_NAME
  - Team entries for a single year within a league.
- GET /player/{username}
  - Player entries across seasons/teams.
- GET /search/options
  - All players and teams for search menus.
- GET /search/teams?query=...
  - Teams matching a query.
- GET /search/players?query=...
  - Players matching a query.

## Data normalization
- Team names are normalized to remove gender/division suffixes (e.g., "Men's", "Women's", "Womxn", "Open").
- League parsing is robust to common variants in tournament/roster data.

## Project layout
- src/main/java/com/kugel/ulti_insights
  - Models: Player, Teams, TeamYears, UltiData
  - Views/Controllers: TeamEntry, PlayerEntrys, Search
  - StartupPopulator: CSV ingestion and association wiring
- src/main/resources: application properties and CSVs

## Build
- Package: `./mvnw clean package`
- Artifact: target/ulti-insights-0.0.1-SNAPSHOT.jar

## Contributing
- Open issues/PRs. Please include tests where possible and keep endpoints documented here.

