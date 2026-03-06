# ⚽ Premier League App — Java Edition

A full-stack Premier League standings tracker built with:
- **Backend**: Java 21 (JDK built-in `com.sun.net.httpserver` — no frameworks needed)
- **Frontend**: Vanilla HTML/CSS/JavaScript (dark theme, matches original design)

---

## Features

| Feature | Description |
|---------|-------------|
| 📊 Standings Table | Full table with W/D/L/GP/GF/GD/Pts, ranked by points → GD → GF |
| ✅ Results | All match results with score display |
| 📅 Fixtures | Upcoming matches with win probability bars |
| ➕ **Add Result** | Add any match score → standings update instantly |
| 🏆 UCL/UEL/REL zones | Color-coded zones in standings |

---

## Requirements

- **Java 21 JDK** (must include `javac`)  
  Download: https://adoptium.net or https://jdk.java.net/21/

---

## Quick Start

### macOS / Linux
```bash
chmod +x build-and-run.sh
./build-and-run.sh
```

### Windows
```bat
build-and-run.bat
```

Then open **http://localhost:8080** in your browser.

---

## Project Structure

```
premier-league/
├── src/
│   └── main/
│       ├── java/com/pl/
│       │   ├── Main.java                  ← Entry point, starts HTTP server
│       │   ├── model/
│       │   │   ├── Team.java              ← Team entity + stat tracking
│       │   │   ├── MatchResult.java       ← Match result entity
│       │   │   └── Fixture.java           ← Upcoming fixture entity
│       │   ├── service/
│       │   │   └── LeagueService.java     ← All data + business logic (singleton)
│       │   └── api/
│       │       ├── ApiHandler.java        ← REST API handler
│       │       └── StaticHandler.java     ← Serves HTML/CSS/JS files
│       └── resources/
│           └── static/
│               └── index.html             ← Full frontend (single file)
├── build-and-run.sh    ← Build + run (Unix/macOS)
├── build-and-run.bat   ← Build + run (Windows)
└── README.md
```

---

## REST API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/standings` | Current league table (sorted) |
| GET | `/api/results` | All match results (newest first) |
| GET | `/api/fixtures` | Upcoming fixtures |
| GET | `/api/teams` | List of all teams |
| POST | `/api/result` | Add a new match result |

### POST /api/result
```json
{
  "home": "ARS",
  "away": "MCI",
  "homeScore": 2,
  "awayScore": 1,
  "date": "Mar 10"
}
```
Returns updated standings, results, and fixtures in one response.

---

## How Standings Are Calculated

Points: **Win = 3pts, Draw = 1pt, Loss = 0pts**

Tiebreaker order: Points → Goal Difference → Goals For

When you add a result via the **＋ Add Result** button:
1. Both teams' stats are updated in memory
2. If the match was in Fixtures, it's removed
3. The frontend receives updated standings + results in a single response
4. The table re-renders with the new rankings
