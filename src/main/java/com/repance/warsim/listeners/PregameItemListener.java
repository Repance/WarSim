package com.repance.warsim.listeners;

import com.repance.warsim.game.Game;
import com.repance.warsim.game.GameState;
import com.repance.warsim.game.TeamPreference;
import com.repance.warsim.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PregameItemListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK
                && action != Action.LEFT_CLICK_AIR
                && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Game game = GameManager.getInstance().getCurrentGame();

        if (game == null) {
            return;
        }

        if (!game.players().isInGame(player)) {
            return;
        }

        if (game.getState() != GameState.WAITING && game.getState() != GameState.COUNTDOWN) {
            return;
        }

        ItemStack item = event.getItem();
        if (!game.pregameItems().isPregameItem(item)) {
            return;
        }

        event.setCancelled(true);

        if (game.pregameItems().isTeamAItem(item)) {
            game.teams().setPreference(player, TeamPreference.TEAM_A);
            return;
        }

        if (game.pregameItems().isTeamBItem(item)) {
            game.teams().setPreference(player, TeamPreference.TEAM_B);
            return;
        }

        if (game.pregameItems().isMapItem(item)) {
            player.sendMessage("§eMap selector komt later.");
            return;
        }

        if (game.pregameItems().isLeaveItem(item)) {
            game.flow().leave(player);
        }
    }
}