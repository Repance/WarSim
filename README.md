# ⚔️ WarSim

> A team-vs-team PvP minigame for Paper Minecraft servers.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-green)
![Paper](https://img.shields.io/badge/Paper-1.21.4-blue)
![Status](https://img.shields.io/badge/Status-In%20Development-orange)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## 📖 About

WarSim is a team-based PvP minigame plugin for Paper servers.

Players join a match, choose a team, fight until one side is eliminated, and return to the lobby once a winner has been determined.

The project focuses on:

* Team-based gameplay
* Simple setup
* Expandable architecture
* Arena support
* Future kit system
* Future GUI support

---

## ✨ Features

### Current Features

* Team selection
* Automatic team balancing
* Pregame lobby
* Match countdown
* Team spawns
* Spectator system
* Team elimination
* Win detection
* Match reset
* Sidebar scoreboard

### Planned Features

* Multiple arenas
* Kit system
* GUI menus
* NPC interaction
* Map voting
* Improved statistics

---

## 🎮 Gameplay Flow

```text
Join Server
     ↓
Join WarSim
     ↓
Choose Team
     ↓
Match Countdown
     ↓
Spawn Into Arena
     ↓
Fight
     ↓
Elimination
     ↓
Winner Determined
     ↓
Endgame
     ↓
Reset To Lobby
```

---

## 📦 Installation

1. Download the latest release.
2. Place the jar inside your server's `plugins` folder.
3. Start or restart the server.
4. Configure WarSim.
5. Begin playing.

---

## 🎮 Player Commands

| Command     | Description   |
| ----------- | ------------- |
| `/ws join`  | Join WarSim   |
| `/ws leave` | Leave WarSim  |
| `/ws start` | Start a match |

---

## 🔧 Admin Commands

| Command               | Description       |
| --------------------- | ----------------- |
| `/ws setlobby`        | Set return lobby  |
| `/ws setpregamelobby` | Set pregame lobby |
| `/ws setspawn team a` | Set Team A spawn  |
| `/ws setspawn team b` | Set Team B spawn  |

---

## 🚧 Development Status

WarSim is currently under active development.

Current priorities:

1. Gameplay stability
2. Arena system
3. Kit system
4. GUI implementation

View the public project board for progress tracking and planned features.

---

## 🤝 Contributing

Contributions are welcome.

Please read:

`docs/development.md`

before creating issues or pull requests.

---

## 📜 License

MIT License
