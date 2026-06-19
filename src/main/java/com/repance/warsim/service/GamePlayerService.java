package com.repance.warsim.service;

import com.repance.warsim.WarSim;
import com.repance.warsim.game.Game;
import com.repance.warsim.game.Team;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GamePlayerService {

    private final Game game;

    public GamePlayerService(Game game) {
        this.game = game;
    }

    public boolean isInGame(Player player) {
        UUID uuid = player.getUniqueId();
        return game.getQueuedPlayers().contains(uuid)
                || game.getTeamA().contains(uuid)
                || game.getTeamB().contains(uuid);
    }

    public Player getOnlinePlayer(UUID uuid) {
        Player player = WarSim.getInstance().getServer().getPlayer(uuid);
        return (player != null && player.isOnline()) ? player : null;
    }

    public List<Player> getQueuedOnlinePlayers() {
        List<Player> players = new ArrayList<>();

        for (UUID uuid : game.getQueuedPlayers()) {
            Player player = getOnlinePlayer(uuid);
            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Player> getAllParticipantOnlinePlayers() {
        Set<UUID> everyone = new HashSet<>();
        everyone.addAll(game.getQueuedPlayers());
        everyone.addAll(game.getTeamA());
        everyone.addAll(game.getTeamB());

        List<Player> players = new ArrayList<>();
        for (UUID uuid : everyone) {
            Player player = getOnlinePlayer(uuid);
            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public boolean isFrozen(Player player) {
        return game.getFrozenPlayers().contains(player.getUniqueId());
    }

    public void freezeAllParticipants() {
        for (Player player : getAllParticipantOnlinePlayers()) {
            game.getFrozenPlayers().add(player.getUniqueId());
        }
    }

    public void unfreezeAllParticipants() {
        game.getFrozenPlayers().clear();
    }

    public boolean isEliminated(Player player) {
        return game.getEliminatedPlayers().contains(player.getUniqueId());
    }

    public void eliminate(Player player) {
        game.getEliminatedPlayers().add(player.getUniqueId());
    }

    public void reviveAll() {
        game.getEliminatedPlayers().clear();
    }

    public boolean isPendingReturnRespawn(Player player) {
        return game.getPendingReturnRespawns().contains(player.getUniqueId());
    }

    public void markPendingReturnRespawn(Player player) {
        game.getPendingReturnRespawns().add(player.getUniqueId());
    }

    public void clearPendingReturnRespawn(Player player) {
        game.getPendingReturnRespawns().remove(player.getUniqueId());
    }

    public boolean isAlive(Player player) {
        return isInGame(player) && !isEliminated(player);
    }

    public int getAliveCount(Team team) {
        Set<UUID> source = team == Team.A ? game.getTeamA() : game.getTeamB();
        int alive = 0;

        for (UUID uuid : source) {
            if (!game.getEliminatedPlayers().contains(uuid)) {
                alive++;
            }
        }

        return alive;
    }

    public void prepareForSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(0);
    }

    public void restoreDefaultState(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setFallDistance(0f);
    }
}