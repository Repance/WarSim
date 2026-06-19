package com.repance.warsim.listeners;

import com.repance.warsim.game.Game;
import com.repance.warsim.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Game game = GameManager.getInstance().getCurrentGame();
        if (game == null) {
            return;
        }

        Player player = event.getPlayer();

        if (!game.players().isInGame(player)) {
            return;
        }

        game.flow().leave(player);
    }
}