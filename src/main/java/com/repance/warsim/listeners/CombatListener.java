package com.repance.warsim.listeners;

import com.repance.warsim.game.Game;
import com.repance.warsim.game.GameState;
import com.repance.warsim.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        Game game = GameManager.getInstance().getCurrentGame();
        if (game == null) return;

        if (!game.players().isInGame(attacker) || !game.players().isInGame(victim)) {
            return;
        }

        if (game.getState() != GameState.ACTIVE) {
            event.setCancelled(true);
            return;
        }

        if (game.teams().areTeammates(attacker, victim)) {
            event.setCancelled(true);
        }
    }
}