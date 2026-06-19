package com.repance.warsim.listeners;

import com.repance.warsim.game.Game;
import com.repance.warsim.game.GameState;
import com.repance.warsim.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Game game = GameManager.getInstance().getCurrentGame();

        if (game == null) {
            return;
        }

        // ✅ ADMIN BYPASS (BELANGRIJK)
        if (player.hasPermission("warsim.admin")) {
            return;
        }

        if (!game.players().isInGame(player)) {
            return;
        }

        String raw = event.getMessage().toLowerCase().trim();

        // ✅ /spawn → leave game
        if (raw.equals("/spawn") || raw.startsWith("/spawn ")) {
            event.setCancelled(true);
            game.flow().leave(player);
            player.sendMessage("§eJe hebt /spawn gebruikt en bent uit WarSim gehaald.");
            return;
        }

        // ✅ /hub → leave game + hub
        if (raw.equals("/hub") || raw.startsWith("/hub ")) {
            event.setCancelled(true);
            game.flow().leave(player);
            player.performCommand("hub");
            return;
        }

        // ✅ altijd toegestaan
        if (raw.equals("/ws leave") || raw.equals("/warsim leave")) {
            return;
        }

        // ❌ blokkeer alleen tijdens actieve fases
        if (game.getState() == GameState.ACTIVE 
                || game.getState() == GameState.STARTING 
                || game.getState() == GameState.ENDING) {

            event.setCancelled(true);
            player.sendMessage("§cJe kunt dit command niet gebruiken tijdens WarSim.");
        }
    }
}