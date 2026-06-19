package com.repance.warsim.service;

import com.repance.warsim.game.Game;
import com.repance.warsim.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GameTeleportService {

    private final Game game;

    public GameTeleportService(Game game) {
        this.game = game;
    }

    public Location getPregameLocation() {
        return LocationUtil.loadLocation("locations.pregame");
    }

    public Location getReturnLocation() {
        Location configured = LocationUtil.loadLocation("locations.return");
        if (configured != null) {
            return configured;
        }

        World defaultWorld = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        if (defaultWorld == null) {
            return null;
        }

        return defaultWorld.getSpawnLocation();
    }

    public boolean hasConfiguredReturnLocation() {
        return LocationUtil.loadLocation("locations.return") != null;
    }

    public Location getTeamASpawn() {
        return LocationUtil.loadLocation("arena.spawn-a");
    }

    public Location getTeamBSpawn() {
        return LocationUtil.loadLocation("arena.spawn-b");
    }

    public boolean teleportPlayerToPregame(Player player) {
        Location location = getPregameLocation();
        if (location == null) {
            return false;
        }

        player.teleport(location);
        return true;
    }

    public boolean teleportPlayerToReturn(Player player) {
        Location location = getReturnLocation();
        if (location == null) {
            return false;
        }

        player.teleport(location);
        return true;
    }

    public boolean teleportTeamsToArena() {
        Location spawnA = getTeamASpawn();
        Location spawnB = getTeamBSpawn();

        if (spawnA == null || spawnB == null) {
            return false;
        }

        for (UUID uuid : game.getTeamA()) {
            Player player = game.players().getOnlinePlayer(uuid);
            if (player != null) {
                player.teleport(spawnA);
            }
        }

        for (UUID uuid : game.getTeamB()) {
            Player player = game.players().getOnlinePlayer(uuid);
            if (player != null) {
                player.teleport(spawnB);
            }
        }

        return true;
    }

    public void teleportEveryoneToReturn() {
        Location returnLocation = getReturnLocation();
        if (returnLocation == null) {
            return;
        }

        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            player.teleport(returnLocation);
        }
    }
}