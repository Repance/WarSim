package com.repance.warsim.service;

import com.repance.warsim.WarSim;
import com.repance.warsim.game.Game;
import com.repance.warsim.game.GameState;
import com.repance.warsim.game.Team;
import com.repance.warsim.game.TeamPreference;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameFlowService {

    private final Game game;

    public GameFlowService(Game game) {
        this.game = game;
    }

    public void join(Player player) {
        if (game.players().isInGame(player)) {
            player.sendMessage("§cJe zit al in WarSim.");
            return;
        }

        game.visuals().reset(player);

        if (game.getQueuedPlayers().size() >= game.getSettings().getMaxPlayers()
                && game.getState() != GameState.ACTIVE
                && game.getState() != GameState.STARTING) {
            player.sendMessage("§cDe WarSim queue zit vol.");
            return;
        }

        if (!game.teleports().teleportPlayerToPregame(player)) {
            player.sendMessage("§cPregame lobby is nog niet ingesteld.");
            if (player.hasPermission("warsim.admin")) {
                player.sendMessage("§7Gebruik: §f/ws setpregamelobby");
            }
            return;
        }

        game.players().restoreDefaultState(player);
        game.getQueuedPlayers().add(player.getUniqueId());
        game.getTeamPreferences().put(player.getUniqueId(), TeamPreference.AUTO);
        game.getKills().put(player.getUniqueId(), 0);
        game.visuals().reset(player);
        game.pregameItems().givePregameItems(player);

        player.sendMessage("§aJe bent gejoined in de WarSim pregame lobby.");
        player.sendMessage("§7Teamvoorkeur: §fAUTO");

        if (game.getQueuedPlayers().size() >= game.getSettings().getMinPlayers()
                && !game.getSettings().isAutoStartEnabled()
                && game.getState() == GameState.WAITING) {
            player.sendMessage("§eEr zijn genoeg spelers. Gebruik §f/ws start §eom de countdown te starten.");
        }

        game.messages().broadcastToQueued(
                "§e" + player.getName() + " §7heeft WarSim gejoined. §f("
                        + game.getQueuedPlayers().size() + "/" + game.getSettings().getMaxPlayers() + ")"
        );

        game.scoreboard().updateAll();
        game.tasks().checkCountdownState();
    }

    public void leave(Player player) {
        boolean wasQueued = game.getQueuedPlayers().remove(player.getUniqueId());
        boolean wasInTeamA = game.getTeamA().remove(player.getUniqueId());
        boolean wasInTeamB = game.getTeamB().remove(player.getUniqueId());
        boolean hadPreference = game.getTeamPreferences().remove(player.getUniqueId()) != null;
        boolean wasEliminated = game.getEliminatedPlayers().remove(player.getUniqueId());
        boolean wasPendingReturn = game.getPendingReturnRespawns().remove(player.getUniqueId());

        if (!wasQueued && !wasInTeamA && !wasInTeamB && !hadPreference && !wasEliminated && !wasPendingReturn) {
            player.sendMessage("§cJe zit niet in WarSim.");
            return;
        }

        game.getFrozenPlayers().remove(player.getUniqueId());
        game.pregameItems().clearPregameItems(player);

        game.visuals().reset(player);
        game.scoreboard().reset(player);
        game.players().restoreDefaultState(player);
        game.teleports().teleportPlayerToReturn(player);

        player.sendMessage("§eJe hebt WarSim verlaten.");

        if (game.getState() == GameState.WAITING || game.getState() == GameState.COUNTDOWN) {
            game.messages().broadcastToQueued(
                    "§e" + player.getName() + " §7heeft de queue verlaten.\n§f("
                            + game.getQueuedPlayers().size() + "/" + game.getSettings().getMaxPlayers() + ")"
            );
            game.tasks().checkCountdownState();
            return;
        }

        if (game.getState() == GameState.STARTING || game.getState() == GameState.ACTIVE) {
            game.messages().broadcastToAllParticipants("§e" + player.getName() + " §7heeft de match verlaten.");
            checkWinCondition();
        }
    }

    public void requestStart(Player player) {
        if (!game.players().isInGame(player) || !game.getQueuedPlayers().contains(player.getUniqueId())) {
            player.sendMessage("§cJe moet in de WarSim queue zitten om de game te starten.");
            return;
        }

        if (game.getState() == GameState.ACTIVE || game.getState() == GameState.STARTING) {
            player.sendMessage("§cDe game is al gestart.");
            return;
        }

        if (game.getState() == GameState.COUNTDOWN) {
            player.sendMessage("§eDe countdown loopt al.");
            return;
        }

        if (game.getQueuedPlayers().size() < game.getSettings().getMinPlayers()) {
            player.sendMessage("§cEr zijn nog niet genoeg spelers om te starten.");
            player.sendMessage("§7Minimaal nodig: §f" + game.getSettings().getMinPlayers());
            return;
        }

        game.messages().broadcastToQueued("§aDe countdown is gestart door §f" + player.getName() + "§a.");
        game.tasks().startPregameCountdown();
    }

    public void forceStart(Player player) {
        if (game.getState() == GameState.ACTIVE || game.getState() == GameState.STARTING) {
            player.sendMessage("§cDe game is al gestart.");
            return;
        }

        if (game.getQueuedPlayers().isEmpty()) {
            player.sendMessage("§cEr zitten geen spelers in de WarSim queue.");
            return;
        }

        game.tasks().cancelCountdown();
        game.messages().broadcastToQueued("§aDe game is geforceerd gestart door §f" + player.getName() + "§a.");
        game.teams().lockTeams();

        if (!game.teleports().teleportTeamsToArena()) {
            game.messages().broadcastToQueued("§cArena spawns zijn niet goed ingesteld.");
            clearPregameItemsForParticipants();
            finishReturnAndReset(0L);
            return;
        }

        clearPregameItemsForParticipants();
        game.players().reviveAll();
        game.setCurrentArenaName("Default");
        game.tasks().startMatchCountdownWithDelay();
    }

    public void forceStop(Player player) {
        if (game.getQueuedPlayers().isEmpty() && game.getTeamA().isEmpty() && game.getTeamB().isEmpty()) {
            player.sendMessage("§cEr is geen actieve WarSim om te stoppen.");
            return;
        }

        game.tasks().cancelCountdown();
        game.tasks().cancelMatchTimer();
        game.setState(GameState.ENDING);

        game.messages().broadcastToAllParticipants("§cWarSim is gestopt door §f" + player.getName() + "§c.");
        game.messages().showStopTitle();

        startEndgameReturnCountdown();
    }

    public void startGamePreparation() {
        clearPregameItemsForParticipants();
    }

    public void startGame() {
        game.setState(GameState.ACTIVE);
        game.setRemainingMatchSeconds(game.getSettings().getMatchDurationSeconds());
        game.messages().broadcastToAllParticipants("§aDE GAME IS GESTART!");
        game.scoreboard().updateAll();
    }

    public void endGame(Team winner) {
        if (game.getState() == GameState.ENDING) {
            return;
        }

        game.tasks().cancelMatchTimer();
        game.setState(GameState.ENDING);

        Team loser = winner == Team.A ? Team.B : Team.A;

        game.messages().broadcastToAllParticipants("§6Team " + winner.name() + " heeft gewonnen!");

        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            Team playerTeam = game.teams().getTeamOf(player);

            if (playerTeam == winner) {
                game.messages().showWinTitle(player);
            } else if (playerTeam == loser) {
                game.messages().showLoseTitle(player);
            }
        }

        game.scoreboard().updateAll();
        startEndgameReturnCountdown();
    }

    public void finishTimedDraw() {
        game.tasks().cancelMatchTimer();
        game.scoreboard().updateAll();
        startEndgameReturnCountdown();
    }

    public void checkWinCondition() {
        if (game.getState() != GameState.ACTIVE && game.getState() != GameState.STARTING) {
            return;
        }

        int aliveA = game.players().getAliveCount(Team.A);
        int aliveB = game.players().getAliveCount(Team.B);

        if (aliveA <= 0 && aliveB > 0) {
            endGame(Team.B);
            return;
        }

        if (aliveB <= 0 && aliveA > 0) {
            endGame(Team.A);
            return;
        }

        if (aliveA <= 0 && aliveB <= 0) {
            game.tasks().cancelMatchTimer();
            game.setState(GameState.ENDING);
            game.messages().broadcastToAllParticipants("§7Niemand heeft gewonnen.");
            game.scoreboard().updateAll();
            startEndgameReturnCountdown();
        }
    }

    public void reset() {
        game.tasks().cancelCountdown();
        game.tasks().cancelMatchTimer();

        game.players().unfreezeAllParticipants();
        game.players().reviveAll();

        game.scoreboard().resetAll();
        game.visuals().resetAllOnlinePlayers();

        game.getQueuedPlayers().clear();
        game.getTeamPreferences().clear();
        game.getTeamA().clear();
        game.getTeamB().clear();
        game.getFrozenPlayers().clear();
        game.getEliminatedPlayers().clear();
    }

    private void clearPregameItemsForParticipants() {
        for (Player participant : game.players().getAllParticipantOnlinePlayers()) {
            game.pregameItems().clearPregameItems(participant);
        }
    }

    private void startEndgameReturnCountdown() {
        int seconds = game.getSettings().getEndgameDelaySeconds();

        game.messages().broadcastToAllParticipants(
                "§7Game afgelopen. Even geduld, je wordt teruggestuurd naar de lobby."
        );

        new BukkitRunnable() {
            int remaining = seconds;

            @Override
            public void run() {
                if (remaining <= 0) {
                    finishReturnAndReset(0L);
                    cancel();
                    return;
                }

                remaining--;
            }
        }.runTaskTimer(WarSim.getInstance(), 0L, 20L);
    }

    private void finishReturnAndReset(long delayTicks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                java.util.List<Player> participants =
                        new java.util.ArrayList<>(game.players().getAllParticipantOnlinePlayers());

                for (Player player : participants) {
                    if (player.isDead()) {
                        game.players().markPendingReturnRespawn(player);
                        player.spigot().respawn();
                    }
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : participants) {
                            game.teleports().teleportPlayerToReturn(player);
                        }

                        // Eerste cleanup direct na teleport
                        for (Player player : participants) {
                            game.scoreboard().reset(player);
                            game.players().restoreDefaultState(player);
                            game.visuals().reset(player); // 👈 ALS LAATSTE
                        }

                        // Tweede cleanup een paar ticks later:
                        // dit pakt plugins/hooks die NA teleport of spawn pas scoreboard/team state zetten
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Player player : participants) {
                                    game.scoreboard().reset(player);
                                    game.players().restoreDefaultState(player);
                                    game.visuals().reset(player); // 👈 ALS LAATSTE
                                }

                            reset();

                            // 🔥 BELANGRIJK: forceer lobby team NA reset
                            for (Player player : participants) {
                                game.visuals().setLobby(player);
                            }

                            game.setState(GameState.WAITING);
                            }
                        }.runTaskLater(WarSim.getInstance(), 5L);
                    }
                }.runTaskLater(WarSim.getInstance(), 10L);
            }
        }.runTaskLater(WarSim.getInstance(), delayTicks);
    }
}