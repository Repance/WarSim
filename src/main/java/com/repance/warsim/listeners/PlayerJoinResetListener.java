package com.repance.warsim.listeners;

import com.repance.warsim.managers.GameManager;
import com.repance.warsim.game.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinResetListener implements Listener {

    private final GameManager gameManager;

    public PlayerJoinResetListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Game game = gameManager.getCurrentGame(); // 🔥 DIT ONTBRAK

        game.visuals().setLobby(player);
        game.players().restoreDefaultState(player);
        game.scoreboard().reset(player);
    }
}