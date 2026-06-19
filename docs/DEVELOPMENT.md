# 🛠️ WarSim Development Guide

> Developer documentation, architecture overview, commands, workflows, and contribution guidelines.

---

# 📚 Table of Contents

* Overview
* Project Goals
* Current Game Flow
* Project Structure
* Commands
* Permissions
* Configuration
* Architecture
* Services
* Game States
* Development Workflow
* GitHub Project
* Planned Features
* Contributing

---

# 🎯 Overview

WarSim is a Paper 1.21.4 minigame plugin focused on team-based PvP gameplay.

The project is intentionally designed around a modular service-based architecture to make future expansion easier.

Current priorities:

1. Stability
2. Arena System
3. Kit System
4. GUI System

---

# ⚔️ Current Game Flow

```text
Join Server
    ↓
Join WarSim
    ↓
Pregame Lobby
    ↓
Choose Team
    ↓
Countdown
    ↓
Teleport To Arena
    ↓
Fight
    ↓
Elimination
    ↓
Winner Determined
    ↓
Endgame
    ↓
Reset
```

---

# 📂 Project Structure

```text
src/main/java/com/repance/warsim

commands/
game/
listeners/
managers/
service/
task/
utils/
```

### commands

Handles player and admin commands.

### game

Core game models and game state.

### listeners

Event listeners.

### managers

Global managers and singleton access.

### service

Business logic layer.

### task

Scheduled Bukkit tasks.

### utils

Shared utility classes.

---

# 🎮 Commands

## Player Commands

| Command     | Description   |
| ----------- | ------------- |
| `/ws join`  | Join WarSim   |
| `/ws leave` | Leave WarSim  |
| `/ws start` | Start a match |

---

## Admin Commands

| Command               | Description            |
| --------------------- | ---------------------- |
| `/ws setlobby`        | Set return lobby       |
| `/ws setpregamelobby` | Set pregame lobby      |
| `/ws setspawn team a` | Set Team A spawn       |
| `/ws setspawn team b` | Set Team B spawn       |
| `/ws stop`            | Force stop active game |

---

# 🔐 Permissions

| Permission     | Description       |
| -------------- | ----------------- |
| `warsim.join`  | Join WarSim       |
| `warsim.start` | Start matches     |
| `warsim.stop`  | Stop matches      |
| `warsim.setup` | Configure WarSim  |
| `warsim.admin` | Full admin access |

---

# ⚙️ Configuration

Current files:

```text
config.yml
messages.yml
```

Planned:

```text
arenas.yml
kits.yml
```

---

# 🧠 Architecture

The plugin follows a service-oriented design.

Core responsibilities are separated into dedicated services.

Examples:

* GameFlowService
* GamePlayerService
* GameTeamService
* GameScoreboardService
* TeamVisualService
* GameTeleportService

Business logic should remain inside services whenever possible.

Commands and listeners should remain lightweight.

---

# 🎮 Game States

Current states:

```text
WAITING
STARTING
ACTIVE
ENDING
```

State transitions should always be handled through the GameFlowService.

---

# 🔄 Development Workflow

When implementing new features:

1. Create GitHub issue.
2. Add issue to project board.
3. Implement feature.
4. Test manually.
5. Create pull request.
6. Move issue to Done.

---

# 📋 GitHub Project

The project board is used as the primary planning tool.

Statuses:

* Backlog
* Ready
* In Progress
* Testing
* Done

Issues should include:

* Goal
* Desired Result
* Tasks
* Acceptance Criteria

---

# 🚀 Planned Features

## High Priority

* Arena System
* Config Validation
* Gameplay Testing

## Future Features

* Kit System
* GUI Menus
* NPC Integration
* Arena Rotation
* Map Voting

---

# 🤝 Contributing

Before submitting a pull request:

* Follow existing code style.
* Keep classes focused on a single responsibility.
* Prefer services over large command classes.
* Update documentation where required.
* Link pull requests to issues whenever possible.

Thank you for helping improve WarSim.
