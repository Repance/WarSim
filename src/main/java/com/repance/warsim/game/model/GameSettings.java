package com.repance.warsim.game.model;

import com.repance.warsim.WarSim;

public class GameSettings {

    private final int minPlayers;
    private final int maxPlayers;
    private final boolean autoStartEnabled;
    private final int pregameCountdownSeconds;
    private final int startCountdownSeconds;
    private final int endgameDelaySeconds;
    private final int matchDurationSeconds;

    public GameSettings(
            int minPlayers,
            int maxPlayers,
            boolean autoStartEnabled,
            int pregameCountdownSeconds,
            int startCountdownSeconds,
            int endgameDelaySeconds,
            int matchDurationSeconds
    ) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.autoStartEnabled = autoStartEnabled;
        this.pregameCountdownSeconds = pregameCountdownSeconds;
        this.startCountdownSeconds = startCountdownSeconds;
        this.endgameDelaySeconds = endgameDelaySeconds;
        this.matchDurationSeconds = matchDurationSeconds;
    }

    public static GameSettings load() {
        return new GameSettings(
                WarSim.getInstance().getConfig().getInt("settings.min-players", 2),
                WarSim.getInstance().getConfig().getInt("settings.max-players", 10),
                WarSim.getInstance().getConfig().getBoolean("settings.auto-start", false),
                WarSim.getInstance().getConfig().getInt("settings.pregame-countdown", 10),
                WarSim.getInstance().getConfig().getInt("settings.game-start-countdown", 5),
                WarSim.getInstance().getConfig().getInt("settings.endgame-delay", 7),
                WarSim.getInstance().getConfig().getInt("settings.match-duration-seconds", 30)
        );
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isAutoStartEnabled() {
        return autoStartEnabled;
    }

    public int getPregameCountdownSeconds() {
        return pregameCountdownSeconds;
    }

    public int getStartCountdownSeconds() {
        return startCountdownSeconds;
    }

    public int getEndgameDelaySeconds() {
        return endgameDelaySeconds;
    }

    public int getMatchDurationSeconds() {
        return matchDurationSeconds;
    }
}