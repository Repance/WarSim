package com.repance.warsim.listeners;

import com.repance.warsim.game.Game;
import com.repance.warsim.managers.GameManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Game game = GameManager.getInstance().getCurrentGame();
        if (game == null) {
            return;
        }

        if (!game.players().isFrozen(player)) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) {
            return;
        }

        boolean movedBlock = from.getX() != to.getX()
                || from.getY() != to.getY()
                || from.getZ() != to.getZ();

        if (movedBlock) {
            event.setTo(new Location(
                    from.getWorld(),
                    from.getX(),
                    from.getY(),
                    from.getZ(),
                    to.getYaw(),
                    to.getPitch()
            ));
        }
    }
}