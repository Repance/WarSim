package com.repance.warsim.service;

import com.repance.warsim.game.Game;
import com.repance.warsim.game.Team;
import com.repance.warsim.game.TeamPreference;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameTeamService {

    private final Game game;
    private final Random random = new Random();

    public GameTeamService(Game game) {
        this.game = game;
    }

    public TeamPreference getPreference(Player player) {
        return game.getTeamPreferences().getOrDefault(player.getUniqueId(), TeamPreference.AUTO);
    }

    public void setPreference(Player player, TeamPreference preference) {
        if (!game.getQueuedPlayers().contains(player.getUniqueId())) {
            player.sendMessage("§cJe zit niet in de pregame lobby.");
            return;
        }

        TeamPreference currentPreference = getPreference(player);

        if (preference == TeamPreference.AUTO) {
            game.getTeamPreferences().put(player.getUniqueId(), TeamPreference.AUTO);
            game.visuals().setAuto(player);
            player.sendMessage("§aJe bent AUTO gejoined.");
            game.scoreboard().update(player);
            return;
        }
        
        int teamACount = countPreference(TeamPreference.TEAM_A);
        int teamBCount = countPreference(TeamPreference.TEAM_B);

        if (currentPreference == TeamPreference.TEAM_A) {
            teamACount--;
        } else if (currentPreference == TeamPreference.TEAM_B) {
            teamBCount--;
        }

        int maxPerTeam = (int) Math.ceil(game.getQueuedPlayers().size() / 2.0);

        if (preference == TeamPreference.TEAM_A && teamACount >= maxPerTeam) {
            player.sendMessage("§cDit team zit momenteel vol.");
            return;
        }

        if (preference == TeamPreference.TEAM_B && teamBCount >= maxPerTeam) {
            player.sendMessage("§cDit team zit momenteel vol.");
            return;
        }

        game.getTeamPreferences().put(player.getUniqueId(), preference);

        switch (preference) {
            case TEAM_A -> {
                game.visuals().setTeamA(player);
                player.sendMessage("§aJe bent §cTeam A §agejoined.");
            }
            case TEAM_B -> {
                game.visuals().setTeamB(player);
                player.sendMessage("§aJe bent §9Team B §agejoined.");
            }
            case AUTO -> {
                game.visuals().setAuto(player);
                player.sendMessage("§aJe bent AUTO gejoined.");
            }
        }

        game.scoreboard().update(player);
    }

    public Team getTeamOf(Player player) {
        UUID uuid = player.getUniqueId();

        if (game.getTeamA().contains(uuid)) return Team.A;
        if (game.getTeamB().contains(uuid)) return Team.B;

        return null;
    }

    public boolean areTeammates(Player player1, Player player2) {
        Team team1 = getTeamOf(player1);
        Team team2 = getTeamOf(player2);

        return team1 != null && team1 == team2;
    }

    public void lockTeams() {
        game.getTeamA().clear();
        game.getTeamB().clear();

        List<UUID> preferA = new ArrayList<>();
        List<UUID> preferB = new ArrayList<>();
        List<UUID> auto = new ArrayList<>();

        for (UUID uuid : game.getQueuedPlayers()) {
            TeamPreference preference = game.getTeamPreferences().getOrDefault(uuid, TeamPreference.AUTO);

            switch (preference) {
                case TEAM_A -> preferA.add(uuid);
                case TEAM_B -> preferB.add(uuid);
                case AUTO -> auto.add(uuid);
            }
        }

        Collections.shuffle(auto, random);

        for (UUID uuid : preferA) {
            game.getTeamA().add(uuid);
        }

        for (UUID uuid : preferB) {
            game.getTeamB().add(uuid);
        }

        for (UUID uuid : auto) {
            if (game.getTeamA().size() < game.getTeamB().size()) {
                game.getTeamA().add(uuid);
            } else if (game.getTeamB().size() < game.getTeamA().size()) {
                game.getTeamB().add(uuid);
            } else {
                if (random.nextBoolean()) {
                    game.getTeamA().add(uuid);
                } else {
                    game.getTeamB().add(uuid);
                }
            }
        }

        for (UUID uuid : game.getTeamA()) {
            Player p = game.players().getOnlinePlayer(uuid);
            if (p != null) {
                game.visuals().setTeamA(p);
            }
        }

        for (UUID uuid : game.getTeamB()) {
            Player p = game.players().getOnlinePlayer(uuid);
            if (p != null) {
                game.visuals().setTeamB(p);
            }
        }

        game.messages().broadcastToQueued("§aTeams zijn gekozen!");
        game.messages().broadcastTeamComposition();
        sendFinalTeamMessages();
        game.scoreboard().updateAll();
    }

    public void eliminatePlayer(Player player) {
        if (!game.players().isEliminated(player)) {
            game.players().eliminate(player);
            game.flow().checkWinCondition();
        }
    }

    private int countPreference(TeamPreference preference) {
        int count = 0;

        for (TeamPreference value : game.getTeamPreferences().values()) {
            if (value == preference) {
                count++;
            }
        }

        return count;
    }

    private void sendFinalTeamMessages() {
        for (UUID uuid : game.getTeamA()) {
            Player player = game.players().getOnlinePlayer(uuid);
            if (player != null) {
                player.sendMessage("§aJe speelt in §cTeam A§a.");
            }
        }

        for (UUID uuid : game.getTeamB()) {
            Player player = game.players().getOnlinePlayer(uuid);
            if (player != null) {
                player.sendMessage("§aJe speelt in §9Team B§a.");
            }
        }
    }
}