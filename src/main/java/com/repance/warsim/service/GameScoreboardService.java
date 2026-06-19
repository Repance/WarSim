package com.repance.warsim.service;

import com.repance.warsim.game.Game;
import com.repance.warsim.game.GameState;
import com.repance.warsim.game.Team;
import com.repance.warsim.game.TeamPreference;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameScoreboardService {

    private final Game game;

    public GameScoreboardService(Game game) {
        this.game = game;
    }

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }

    public void update(Player player) {
        if (!game.players().isInGame(player)) {
            reset(player);
            return;
        }

        if (Bukkit.getScoreboardManager() == null) {
            return;
        }

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        applyWarSimTeams(scoreboard);

        Objective objective = scoreboard.registerNewObjective("warsim", "dummy", "§6⚔ §c§lWarSim");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> lines = buildLines(player);
        int score = lines.size();

        for (String line : lines) {
            objective.getScore(makeUnique(line, score)).setScore(score);
            score--;
        }

        player.setScoreboard(scoreboard);
    }

    public void reset(Player player) {
        if (Bukkit.getScoreboardManager() == null) {
            return;
        }

        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void resetAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            reset(player);
        }
    }

    private void applyWarSimTeams(Scoreboard scoreboard) {
        org.bukkit.scoreboard.Team teamA = getOrCreate(scoreboard, TeamVisualService.TEAM_A_NAME, ChatColor.RED);
        org.bukkit.scoreboard.Team teamB = getOrCreate(scoreboard, TeamVisualService.TEAM_B_NAME, ChatColor.BLUE);
        org.bukkit.scoreboard.Team teamAuto = getOrCreate(scoreboard, TeamVisualService.TEAM_AUTO_NAME, ChatColor.GRAY);

        for (UUID uuid : game.getQueuedPlayers()) {
            Player target = game.players().getOnlinePlayer(uuid);
            if (target == null) {
                continue;
            }

            if (game.getTeamA().contains(uuid)) {
                teamA.addEntry(target.getName());
            } else if (game.getTeamB().contains(uuid)) {
                teamB.addEntry(target.getName());
            } else {
                teamAuto.addEntry(target.getName());
            }
        }
    }

    private org.bukkit.scoreboard.Team getOrCreate(Scoreboard scoreboard, String name, ChatColor color) {
        org.bukkit.scoreboard.Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }

        team.setColor(color);
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(false);
        team.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE,
                org.bukkit.scoreboard.Team.OptionStatus.NEVER);

        return team;
    }

    private List<String> buildLines(Player player) {
        List<String> lines = new ArrayList<>();

        if (game.getState() == GameState.WAITING || game.getState() == GameState.COUNTDOWN) {
            lines.add("§7Status: §f" + getPregameStatusText());
            lines.add("§8");
            lines.add("§7Spelers: §f" + game.getQueuedPlayers().size() + "/" + game.getSettings().getMaxPlayers());
            lines.add("§0");
            lines.add("§7Team: §f" + getPregameTeamText(player));
            return lines;
        }

        lines.add("§7Tijd: §f" + formatTime(game.getRemainingMatchSeconds()));
        lines.add("§8");
        lines.add("§7Map: §f" + game.getCurrentArenaName());
        lines.add("§0");
        lines.add("§7Team: §f" + getMatchTeamText(player));
        lines.add("§1");
        lines.add("§cTeam A: §f" + game.players().getAliveCount(Team.A) + "/" + game.getTeamA().size());
        lines.add("§9Team B: §f" + game.players().getAliveCount(Team.B) + "/" + game.getTeamB().size());
        lines.add("§2");
        lines.add("§7Totaal: §f" + getParticipantCount() + "/" + game.getSettings().getMaxPlayers());
        lines.add("§3");
        lines.add("§7Kills: §f" + getKills(player));

        return lines;
    }

    private String getPregameTeamText(Player player) {
        TeamPreference preference = game.teams().getPreference(player);
        return switch (preference) {
            case TEAM_A -> "§cTeam A";
            case TEAM_B -> "§9Team B";
            case AUTO -> "AUTO";
        };
    }

    private String getPregameStatusText() {
        return switch (game.getState()) {
            case COUNTDOWN -> "§eStart over " + game.tasks().getPregameCountdownRemaining() + "s";
            case WAITING -> "Wachten";
            default -> game.getState().name();
        };
    }

    private String getMatchTeamText(Player player) {
        Team team = game.teams().getTeamOf(player);

        if (team == Team.A) {
            return "§cTeam A";
        }

        if (team == Team.B) {
            return "§9Team B";
        }

        return "AUTO";
    }

    private int getParticipantCount() {
        return game.getTeamA().size() + game.getTeamB().size();
    }

    private int getKills(Player player) {
        UUID uuid = player.getUniqueId();
        return game.getKills().getOrDefault(uuid, 0);
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String makeUnique(String line, int index) {
        if (line.length() > 32) {
            line = line.substring(0, 32);
        }
        return line + "§" + Integer.toHexString(index % 10);
    }
}