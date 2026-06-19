package com.repance.warsim.service;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamVisualService {

    public static final String TEAM_A_NAME = "warsim_a";
    public static final String TEAM_B_NAME = "warsim_b";
    public static final String TEAM_AUTO_NAME = "warsim_auto";
    public static final String TEAM_LOBBY_NAME = "warsim_lobby";

    private final Scoreboard mainScoreboard;

    public TeamVisualService() {
        this.mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        ensureMainTeams();
    }

    public void setTeamA(Player player) {
        ensureMainTeams();
        hardRemoveFromAllWarSimTeams(player);

        Team teamA = mainScoreboard.getTeam(TEAM_A_NAME);
        if (teamA != null) {
            teamA.addEntry(player.getName());
        }

        player.setPlayerListName("§c" + player.getName());
        player.setDisplayName("§c" + player.getName());
        player.setCustomName("§c" + player.getName());
    }

    public void setTeamB(Player player) {
        ensureMainTeams();
        hardRemoveFromAllWarSimTeams(player);

        Team teamB = mainScoreboard.getTeam(TEAM_B_NAME);
        if (teamB != null) {
            teamB.addEntry(player.getName());
        }

        player.setPlayerListName("§9" + player.getName());
        player.setDisplayName("§9" + player.getName());
        player.setCustomName("§9" + player.getName());
    }

    public void setAuto(Player player) {
        ensureMainTeams();
        hardRemoveFromAllWarSimTeams(player);

        Team auto = mainScoreboard.getTeam(TEAM_AUTO_NAME);
        if (auto != null) {
            auto.addEntry(player.getName());
        }

        player.setPlayerListName(player.getName());
        player.setDisplayName(player.getName());
        player.setCustomName(null);
    }

    public void reset(Player player) {
        setLobby(player);
    }

    public void resetAllOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            reset(player);
        }
    }

    public void reinitializeTeams() {
        resetWarSimTeams();
        ensureMainTeams();
    }

    public void resetWarSimTeams() {
        unregisterIfExists(TEAM_A_NAME);
        unregisterIfExists(TEAM_B_NAME);
        unregisterIfExists(TEAM_AUTO_NAME);
        unregisterIfExists(TEAM_LOBBY_NAME);
    }

    private void ensureMainTeams() {
        Team lobby = mainScoreboard.getTeam(TEAM_LOBBY_NAME);
        if (lobby == null) {
            lobby = mainScoreboard.registerNewTeam(TEAM_LOBBY_NAME);
        }
        configureTeam(lobby, ChatColor.WHITE);
        Team teamA = mainScoreboard.getTeam(TEAM_A_NAME);
        if (teamA == null) {
            teamA = mainScoreboard.registerNewTeam(TEAM_A_NAME);
        }
        configureTeam(teamA, ChatColor.RED);

        Team teamB = mainScoreboard.getTeam(TEAM_B_NAME);
        if (teamB == null) {
            teamB = mainScoreboard.registerNewTeam(TEAM_B_NAME);
        }
        configureTeam(teamB, ChatColor.BLUE);

        Team teamAuto = mainScoreboard.getTeam(TEAM_AUTO_NAME);
        if (teamAuto == null) {
            teamAuto = mainScoreboard.registerNewTeam(TEAM_AUTO_NAME);
        }
        configureTeam(teamAuto, ChatColor.GRAY);
    }

    private void configureTeam(Team team, ChatColor color) {
        team.setColor(color);
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(false);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }

    private void hardRemoveFromAllWarSimTeams(Player player) {
        // 1) Eerst van CURRENT scoreboard halen
        Scoreboard current = player.getScoreboard();
        if (current != null) {
            removeFromTeamsOnScoreboard(current, player);
        }

        // 2) Daarna terug naar main scoreboard
        if (Bukkit.getScoreboardManager() != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }

        // 3) Dan ook van MAIN scoreboard halen
        removeFromMainTeams(player);
    }

    private void removeFromMainTeams(Player player) {
        removeFromTeamsOnScoreboard(mainScoreboard, player);
    }

    private void removeFromTeamsOnScoreboard(Scoreboard scoreboard, Player player) {
        removeFromTeam(scoreboard, TEAM_A_NAME, player);
        removeFromTeam(scoreboard, TEAM_B_NAME, player);
        removeFromTeam(scoreboard, TEAM_AUTO_NAME, player);
        removeFromTeam(scoreboard, TEAM_LOBBY_NAME, player);
    }

    private void removeFromTeam(Scoreboard scoreboard, String teamName, Player player) {
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            team.removeEntry(player.getName());
        }
    }

    private void unregisterIfExists(String teamName) {
        Team team = mainScoreboard.getTeam(teamName);
        if (team != null) {
            for (String entry : team.getEntries()) {
                team.removeEntry(entry);
            }
            team.unregister();
        }
    }

    public void setLobby(Player player) {
        ensureMainTeams();
        hardRemoveFromAllWarSimTeams(player);

        Team lobby = mainScoreboard.getTeam(TEAM_LOBBY_NAME);
        if (lobby != null) {
            lobby.addEntry(player.getName());
        }

        player.setPlayerListName(player.getName());
        player.setDisplayName(player.getName());
        player.setCustomName(null);

        if (Bukkit.getScoreboardManager() != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
}