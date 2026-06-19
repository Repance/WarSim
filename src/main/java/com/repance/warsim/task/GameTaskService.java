package com.repance.warsim.task;

import com.repance.warsim.WarSim;
import com.repance.warsim.game.Game;
import com.repance.warsim.game.GameState;
import com.repance.warsim.game.Team;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTaskService {

    private final Game game;
    private BukkitRunnable countdownTask;
    private BukkitRunnable matchTimerTask;
    private int pregameCountdownRemaining = -1;

    public GameTaskService(Game game) {
        this.game = game;
    }

    public void checkCountdownState() {
        if (game.getState() == GameState.ACTIVE || game.getState() == GameState.STARTING || game.getState() == GameState.ENDING) {
            return;
        }

        if (game.getState() == GameState.COUNTDOWN && game.getQueuedPlayers().size() < game.getSettings().getMinPlayers()) {
            cancelCountdown();
            game.setState(GameState.WAITING);
            game.messages().broadcastToQueued("§cGame gecanceld: te weinig spelers.");
            game.messages().showCancelledTitleToQueued();
            game.scoreboard().updateAll();
            return;
        }

        if (game.getSettings().isAutoStartEnabled()
                && game.getState() == GameState.WAITING
                && game.getQueuedPlayers().size() >= game.getSettings().getMinPlayers()) {
            startPregameCountdown();
        } else {
            game.scoreboard().updateAll();
        }
    }

    public void startPregameCountdown() {
        if (countdownTask != null || game.getState() == GameState.COUNTDOWN) {
            return;
        }

        game.setState(GameState.COUNTDOWN);
        game.messages().broadcastToQueued("§aPregame countdown gestart.");
        game.scoreboard().updateAll();

        countdownTask = new BukkitRunnable() {
            int seconds = game.getSettings().getPregameCountdownSeconds();

            @Override
            public void run() {
                pregameCountdownRemaining = seconds;

                if (game.getQueuedPlayers().size() < game.getSettings().getMinPlayers()) {
                    game.setState(GameState.WAITING);
                    game.messages().broadcastToQueued("§cGame gecanceld: te weinig spelers.");
                    game.messages().showCancelledTitleToQueued();
                    game.scoreboard().updateAll();
                    cancel();
                    countdownTask = null;
                    pregameCountdownRemaining = -1;
                    return;
                }

                if (seconds <= 0) {
                    game.teams().lockTeams();

                    if (!game.teleports().teleportTeamsToArena()) {
                        game.messages().broadcastToQueued("§cArena spawns zijn niet goed ingesteld.");
                        game.flow().startGamePreparation();
                        game.flow().reset();
                        game.setState(GameState.WAITING);
                        game.scoreboard().updateAll();
                        cancel();
                        countdownTask = null;
                        pregameCountdownRemaining = -1;
                        return;
                    }

                    game.flow().startGamePreparation();
                    game.players().reviveAll();
                    startMatchCountdownWithDelay();

                    cancel();
                    countdownTask = null;
                    pregameCountdownRemaining = -1;
                    return;
                }

                game.messages().showCountdownTitleToQueued(seconds, "§7Match start zo...");

                if (seconds <= 5 || seconds % 10 == 0) {
                    game.messages().broadcastToQueued("§eMatch start in §f" + seconds + " §eseconden!");
                }

                game.scoreboard().updateAll();
                seconds--;
            }
        };

        countdownTask.runTaskTimer(WarSim.getInstance(), 0L, 20L);
    }

    public void startMatchCountdownWithDelay() {
        game.setState(GameState.STARTING);
        game.players().freezeAllParticipants();
        game.messages().broadcastToAllParticipants("§7Maak je klaar...");
        game.scoreboard().updateAll();

        new BukkitRunnable() {
            @Override
            public void run() {
                startMatchCountdown();
            }
        }.runTaskLater(WarSim.getInstance(), 40L);
    }

    public void startMatchCountdown() {
        new BukkitRunnable() {
            int seconds = game.getSettings().getStartCountdownSeconds();

            @Override
            public void run() {
                if (seconds <= 0) {
                    game.players().unfreezeAllParticipants();
                    game.messages().showGoTitle();
                    game.flow().startGame();
                    startMatchTimer();
                    cancel();
                    return;
                }

                game.messages().showCountdownTitleToParticipants(seconds, "§7Maak je klaar...");
                game.scoreboard().updateAll();
                seconds--;
            }
        }.runTaskTimer(WarSim.getInstance(), 0L, 20L);
    }

    public void startMatchTimer() {
        cancelMatchTimer();
        game.setRemainingMatchSeconds(game.getSettings().getMatchDurationSeconds());
        game.scoreboard().updateAll();

        matchTimerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getState() != GameState.ACTIVE) {
                    cancel();
                    matchTimerTask = null;
                    return;
                }

                tickMatchTimer();
            }
        };

        matchTimerTask.runTaskTimer(WarSim.getInstance(), 20L, 20L);
    }

    public void tickMatchTimer() {
        int remaining = game.getRemainingMatchSeconds();

        if (remaining <= 0) {
            handleTimeLimitReached();
            return;
        }

        game.setRemainingMatchSeconds(remaining - 1);
        int secondsLeft = game.getRemainingMatchSeconds();

        game.messages().sendMatchTimerActionBarToParticipants(secondsLeft);

        if (secondsLeft == 60 || secondsLeft == 30 || secondsLeft == 10) {
            game.messages().broadcastToAllParticipants("§eNog " + secondsLeft + " seconden!");
        }

        if (secondsLeft <= 5 && secondsLeft > 0) {
            game.messages().broadcastSoundToParticipants(Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
        }

        game.scoreboard().updateAll();
    }

    private void handleTimeLimitReached() {
        int aliveA = game.players().getAliveCount(Team.A);
        int aliveB = game.players().getAliveCount(Team.B);

        if (aliveA > aliveB) {
            game.messages().broadcastToAllParticipants("§eTijd is om! §cTeam A §ewint op basis van meeste levende spelers.");
            game.flow().endGame(Team.A);
        } else if (aliveB > aliveA) {
            game.messages().broadcastToAllParticipants("§eTijd is om! §9Team B §ewint op basis van meeste levende spelers.");
            game.flow().endGame(Team.B);
        } else {
            game.setState(GameState.ENDING);
            game.messages().showDrawTitleToParticipants();
            game.messages().broadcastToAllParticipants("§eTijd is om! §6Het is een gelijkspel.");
            game.messages().broadcastToAllParticipants("§7Beide teams hebben evenveel levende spelers.");
            game.flow().finishTimedDraw();
        }

        cancelMatchTimer();
    }

    public void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        pregameCountdownRemaining = -1;
    }

    public void cancelMatchTimer() {
        if (matchTimerTask != null) {
            matchTimerTask.cancel();
            matchTimerTask = null;
        }
    }

    public int getPregameCountdownRemaining() {
        return pregameCountdownRemaining;
    }
}