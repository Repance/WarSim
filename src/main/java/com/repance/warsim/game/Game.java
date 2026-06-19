package com.repance.warsim.game;

import com.repance.warsim.game.model.GameSettings;
import com.repance.warsim.service.GameFlowService;
import com.repance.warsim.service.GameMessageService;
import com.repance.warsim.service.GamePlayerService;
import com.repance.warsim.service.GameScoreboardService;
import com.repance.warsim.service.GameTeamService;
import com.repance.warsim.service.GameTeleportService;
import com.repance.warsim.service.PregameItemService;
import com.repance.warsim.service.TeamVisualService;
import com.repance.warsim.task.GameTaskService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Game {

    private GameState state = GameState.WAITING;

    private final Set<UUID> queuedPlayers = new HashSet<>();
    private final Map<UUID, TeamPreference> teamPreferences = new HashMap<>();

    private final Set<UUID> teamA = new HashSet<>();
    private final Set<UUID> teamB = new HashSet<>();

    private final Set<UUID> frozenPlayers = new HashSet<>();
    private final Set<UUID> eliminatedPlayers = new HashSet<>();
    private final Set<UUID> pendingReturnRespawns = new HashSet<>();

    private final Map<UUID, Integer> kills = new HashMap<>();

    private int remainingMatchSeconds;
    private String currentArenaName = "Default";

    private final GameSettings settings;
    private final GamePlayerService playerService;
    private final GameMessageService messageService;
    private final GameTeleportService teleportService;
    private final GameTeamService teamService;
    private final GameTaskService taskService;
    private final GameFlowService flowService;
    private final PregameItemService pregameItemService;
    private final TeamVisualService teamVisualService;
    private final GameScoreboardService scoreboardService;

    public Game() {
        this.settings = GameSettings.load();
        this.playerService = new GamePlayerService(this);
        this.messageService = new GameMessageService(this);
        this.teleportService = new GameTeleportService(this);
        this.teamService = new GameTeamService(this);
        this.taskService = new GameTaskService(this);
        this.flowService = new GameFlowService(this);
        this.pregameItemService = new PregameItemService();
        this.teamVisualService = new TeamVisualService();
        this.scoreboardService = new GameScoreboardService(this);
        this.remainingMatchSeconds = settings.getMatchDurationSeconds();
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Set<UUID> getQueuedPlayers() {
        return queuedPlayers;
    }

    public Map<UUID, TeamPreference> getTeamPreferences() {
        return teamPreferences;
    }

    public Set<UUID> getTeamA() {
        return teamA;
    }

    public Set<UUID> getTeamB() {
        return teamB;
    }

    public Set<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    public Set<UUID> getEliminatedPlayers() {
        return eliminatedPlayers;
    }

    public Set<UUID> getPendingReturnRespawns() {
        return pendingReturnRespawns;
    }

    public Map<UUID, Integer> getKills() {
        return kills;
    }

    public int getRemainingMatchSeconds() {
        return remainingMatchSeconds;
    }

    public void setRemainingMatchSeconds(int remainingMatchSeconds) {
        this.remainingMatchSeconds = remainingMatchSeconds;
    }

    public String getCurrentArenaName() {
        return currentArenaName;
    }

    public void setCurrentArenaName(String currentArenaName) {
        this.currentArenaName = currentArenaName;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public GamePlayerService players() {
        return playerService;
    }

    public GameMessageService messages() {
        return messageService;
    }

    public GameTeleportService teleports() {
        return teleportService;
    }

    public GameTeamService teams() {
        return teamService;
    }

    public GameTaskService tasks() {
        return taskService;
    }

    public GameFlowService flow() {
        return flowService;
    }

    public PregameItemService pregameItems() {
        return pregameItemService;
    }

    public TeamVisualService visuals() {
        return teamVisualService;
    }

    public GameScoreboardService scoreboard() {
        return scoreboardService;
    }
}