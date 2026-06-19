package com.repance.warsim.listeners;

import com.repance.warsim.WarSim;
import com.repance.warsim.game.Game;
import com.repance.warsim.game.Team;
import com.repance.warsim.managers.GameManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerEliminationListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Game game = GameManager.getInstance().getCurrentGame();

        if (game == null) {
            return;
        }

        if (!game.players().isInGame(player)) {
            return;
        }

        if (game.players().isEliminated(player)) {
            return;
        }

        if (game.getState() != com.repance.warsim.game.GameState.ACTIVE) {
            return;
        }

        event.deathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setKeepInventory(true);
        event.setKeepLevel(true);

        game.players().eliminate(player);

        Player killer = player.getKiller();
        if (killer != null && game.players().isInGame(killer) && !game.teams().areTeammates(player, killer)) {
            game.getKills().put(killer.getUniqueId(), game.getKills().getOrDefault(killer.getUniqueId(), 0) + 1);
        }

        Team team = game.teams().getTeamOf(player);
        if (team != null) {
            game.messages().broadcastToAllParticipants(
                    "§7" + player.getName() + " §cis geëlimineerd. §7(Team " + team.name() + ")"
            );
        }

        game.scoreboard().updateAll();
        game.flow().checkWinCondition();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Game game = GameManager.getInstance().getCurrentGame();

        if (game == null) {
            return;
        }

        if (game.players().isPendingReturnRespawn(player)) {
            Location returnLocation = game.teleports().getReturnLocation();
            if (returnLocation != null) {
                event.setRespawnLocation(returnLocation);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        return;
                    }

                    game.players().restoreDefaultState(player);
                    game.players().clearPendingReturnRespawn(player);
                    game.visuals().reset(player);
                    game.scoreboard().reset(player);
                }
            }.runTaskLater(WarSim.getInstance(), 1L);

            return;
        }

        if (!game.players().isInGame(player)) {
            return;
        }

        if (!game.players().isEliminated(player)) {
            return;
        }

        Location respawnLocation = null;
        Team team = game.teams().getTeamOf(player);

        if (team == Team.A) {
            respawnLocation = game.teleports().getTeamASpawn();
        } else if (team == Team.B) {
            respawnLocation = game.teleports().getTeamBSpawn();
        }

        if (respawnLocation != null) {
            event.setRespawnLocation(respawnLocation);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }

                player.setGameMode(GameMode.SPECTATOR);
                player.setAllowFlight(true);
                player.setFlying(true);
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.setFireTicks(0);
                game.scoreboard().update(player);
            }
        }.runTaskLater(WarSim.getInstance(), 1L);
    }
}