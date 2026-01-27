# Hytale CTF Battlefield - "Build & Destroy Edition"

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A complete competitive, team-based Capture the Flag minigame mod for Hytale featuring dynamic "Build & Destroy" mechanics where players can modify the battlefield in real-time.

## 🎯 Features

### Core Gameplay
- **Team-Based Combat**: Red vs Blue teams compete to capture enemy flags
- **Build & Destroy**: Modify the battlefield with strategic building and destruction
- **Dynamic Arenas**: Create and manage multiple CTF maps with full regeneration support
- **Flag Structures**: Choose from 5+ pre-built flag structure types or use custom designs
- **Auto-Balancing**: Intelligent team sorting for fair matches

### Flag Mechanics
- Break enemy flag blocks to steal flags
- Carry flags back to your base to score
- Flags return instantly on carrier death
- Visual indicators for flag carriers
- Protected flag zones

### Map Management
- 3D region selection with wand tool
- Arena snapshots for instant regeneration
- Multiple arena support
- Configurable spawn points and lobbies
- Portal-based team assignment

### Game Features
- Configurable capture limits and time limits
- Build zones and no-build zones
- PvP toggle options
- Comprehensive statistics tracking
- Real-time HUD and scoreboards
- Alert system for game events

## 🚀 Quick Start

### Requirements
- **Java 25 or higher** (REQUIRED)
- Maven 3.8+
- Hytale Server (when available)

### Building
```bash
mvn clean package
```

### Installation
1. Download the latest release from the releases page
2. Place the JAR file in your Hytale server's plugins folder
3. Start the server
4. Configure the plugin in `plugins/CTF-Battlefield/config.yml`

## 📋 Command Reference

### Map Management
```
/ctf wand                    - Get region selection wand
/ctf pos1                    - Set position 1 at current location
/ctf pos2                    - Set position 2 at current location
/ctf define <mapName>        - Define a new arena
/ctf snapshot <mapName>      - Save arena snapshot for regeneration
/ctf reset <mapName>         - Reset arena to snapshot
/ctf delete <mapName>        - Delete an arena
/ctf list                    - List all arenas
/ctf info <mapName>          - Show arena details
```

### Flag Setup
```
/ctf setflag <red|blue> [structure]  - Place team flag
/ctf removeflag <red|blue>           - Remove team flag
/ctf moveflag <red|blue>             - Move flag to current location
/ctf flagreturn <red|blue>           - Force return flag to base
```

**Available Structures**: `pedestal` (default), `tower`, `fortress`, `shrine`, `pillar`, `custom:<name>`

### Team & Spawn Setup
```
/ctf setspawn <red|blue>    - Set team spawn point
/ctf setlobby               - Set lobby location
/ctf setportal              - Set auto-sort portal
/ctf sethub                 - Set hub location
```

### Game Control
```
/ctf start [mapName]        - Start a CTF match
/ctf stop                   - End current match
/ctf pause                  - Pause the match
/ctf resume                 - Resume paused match
/ctf restart                - Restart current match
```

### Game Settings
```
/ctf setlimit <captures>    - Set capture limit (default: 3)
/ctf settime <minutes>      - Set time limit (0 = unlimited)
/ctf setbuildzone <radius>  - Set build restriction radius
/ctf setnobuild <radius>    - Set no-build zone radius
/ctf togglepvp <on|off>     - Toggle lobby PvP
/ctf togglebuild <on|off>   - Toggle building
/ctf togglebreak <on|off>   - Toggle block breaking
```

### Player Commands
```
/ctf join [mapName]         - Join CTF queue
/ctf leave                  - Leave game/queue
/ctf team <red|blue>        - Request specific team
/ctf stats [player]         - View statistics
/ctf top                    - View leaderboard
```

## 🏗️ Architecture

### Project Structure
```
src/main/java/com/hytale/ctf/
├── CTFPlugin.java          - Main plugin entry point
├── commands/               - Command implementations
├── game/                   - Core game logic
├── flag/                   - Flag system
├── arena/                  - Arena management
├── player/                 - Player management
├── team/                   - Team system
├── events/                 - Event handlers
├── ui/                     - HUD and alerts
├── structure/              - Flag structures
├── storage/                - Data persistence
└── util/                   - Utilities
```

### Java 25 Features Used
- **Records**: Immutable data classes (GameSettings, PlayerStats, etc.)
- **Pattern Matching**: Enhanced switch expressions
- **Sealed Classes**: Type-safe state machines
- **Virtual Threads**: Async operations for I/O and events
- **Text Blocks**: Multi-line string literals for messages

## 🎮 Game Flow

1. **Setup Phase**
   - Admin defines arena with `/ctf wand` and `/ctf define`
   - Set team spawns and flag locations
   - Save snapshot with `/ctf snapshot`

2. **Pre-Match**
   - Players join with `/ctf join`
   - Auto-sorting assigns teams
   - Players teleport to lobby

3. **Match Start**
   - Countdown begins
   - Players teleport to team spawns
   - Flags spawn on structures

4. **Gameplay**
   - Build defenses and pathways
   - Attack enemy base
   - Steal flag by breaking flag block
   - Return flag to base to score

5. **Match End**
   - Team reaches capture limit or time expires
   - Winner announced
   - Arena regenerates from snapshot
   - Statistics saved

## 📊 Statistics Tracked

- Total captures
- Flag returns (killed carriers)
- Kills and deaths
- Wins and losses
- Total playtime
- Blocks placed/broken

## ⚙️ Configuration

See `config.yml` for all configurable options including:
- Default capture limits
- Time limits
- Zone radii
- Material restrictions
- Messages and alerts
- Structure templates

## 🔧 Development

### Building from Source
```bash
git clone https://github.com/2020009-CPE/hytale-ctf-battlefield.git
cd hytale-ctf-battlefield
mvn clean package
```

### Running Tests
```bash
mvn test
```

### Code Style
- Follow Java conventions
- Use Java 25 features where appropriate
- Comprehensive JavaDoc for public APIs
- SOLID principles

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 🐛 Bug Reports

Please report bugs via GitHub Issues with:
- Detailed description
- Steps to reproduce
- Expected vs actual behavior
- Server version and Java version

## 📞 Support

- GitHub Issues: [Report bugs or request features](https://github.com/2020009-CPE/hytale-ctf-battlefield/issues)
- Documentation: See `/docs` folder for detailed guides

## 🎯 Roadmap

- [ ] Ranked matchmaking system
- [ ] Custom kit system
- [ ] Advanced statistics dashboard
- [ ] Tournament mode
- [ ] Spectator mode
- [ ] Replay system

---

**Built with Java 25** | Made for Hytale
