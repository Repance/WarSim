package com.repance.warsim.managers;

import com.repance.warsim.game.Game;
import org.bukkit.entity.Player;

public class GameManager {

    private static GameManager instance;
    private Game currentGame;

    public GameManager() {
        instance = this;
        currentGame = new Game();
    }

    public static GameManager getInstance() {
        return instance;
    }

    public void joinGame(Player player) {
        ensureGame();
        currentGame.flow().join(player);
    }

    public void leaveGame(Player player) {
        ensureGame();
        currentGame.flow().leave(player);
    }

    public void startCountdown(Player player) {
        ensureGame();
        currentGame.flow().requestStart(player);
    }

    public void forceStart(Player player) {
        ensureGame();
        currentGame.flow().forceStart(player);
    }

    public void forceStop(Player player) {
        ensureGame();
        currentGame.flow().forceStop(player);
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void resetGame() {
        currentGame = new Game();
    }

    private void ensureGame() {
        if (currentGame == null) {
            currentGame = new Game();
        }
    }
}