# Stadisticts (backend)

Stadisticts is a Spring Boot REST API that aggregates ultimate frisbee player and team statistics. It ingests CSV data at startup, normalizes team and league info, and exposes endpoints to query players, teams, seasons, and leagues.

## Key features
- CSV ingestion on startup (dev/test profiles) from src/main/resources/data.csv and rosterFile.csv
- Normalized model: Player, Teams, TeamYears, UltiData, and League enum
- Team and player views with precomputed display/ranking values
- Search endpoints for teams and players
- Supports both H2 (development) and MySQL (production) databases

## Tech stack
- Java 21 + Spring Boot (Web, Data JPA, Transactions, Actuator)
- H2 in-memory DB (dev/test) or MySQL 8.0+ (production)
- Maven wrapper (mvnw)

## Run locally

### H2 Database (Development)
Requirements: Java 21 (Temurin) and Maven (or use the Maven wrapper provided).

- Start the API with the dev profile to seed sample data:
  - Unix: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
  - Windows: `mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev`
- Server listens on http://localhost:8080
- H2 console (optional): http://localhost:8080/h2-console

### MySQL Database (Development)
Requirements: MySQL 8.0+ running locally or via Docker

1. **Using Docker Compose (Recommended)**:
   ```bash
   docker-compose up -d mysql
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev-mysql
   ```

2. **Using existing MySQL instance**:
   - Create database: `CREATE DATABASE stadisticts_dev;`
   - Set environment variables or update `application-dev-mysql.yml`:
     - `DATABASE_URL=jdbc:mysql://localhost:3306/stadisticts_dev`
     - `DATABASE_USERNAME=your_username`
     - `DATABASE_PASSWORD=your_password`
   - Run: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev-mysql`

## Database Configuration

### Environment Variables
- `DATABASE_URL`: JDBC URL (default: varies by profile)
- `DATABASE_USERNAME`: Database username
- `DATABASE_PASSWORD`: Database password

### Profiles
- `dev`: H2 in-memory database with sample data
- `dev-mysql`: MySQL database with sample data and debug logging
- `prod`: MySQL database for production use
- `test`: H2 for testing

## Build
- Package: `./mvnw clean package`
- Artifact: target/stadisticts-1.0.jar

## Docker

### Development with Docker Compose
```bash
# Start MySQL and application
docker-compose up

# Start only MySQL (for local development)
docker-compose up -d mysql
```

### Manual Docker Build
```bash
# Build image
docker build -t stadisticts:1.0 .

# Run with H2 (dev profile)
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev stadisticts:1.0

# Run with MySQL (requires MySQL container/instance)
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/stadisticts_dev \
  -e DATABASE_USERNAME=stadisticts_user \
  -e DATABASE_PASSWORD=userpassword \
  stadisticts:1.0
```

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
- GET /actuator/health
  - Application health status.

## Contributing
Open issues/PRs. Please include tests where possible and keep endpoints documented here.
