package com.repance.warsim.listeners;

import com.repance.warsim.game.Game;
import com.repance.warsim.game.GameState;
import com.repance.warsim.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PregameProtectionListener implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Game game = GameManager.getInstance().getCurrentGame();

        if (!shouldProtect(game, player)) {
            return;
        }

        ItemStack item = event.getItemDrop().getItemStack();
        if (game.pregameItems().isPregameItem(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Game game = GameManager.getInstance().getCurrentGame();
        if (!shouldProtect(game, player)) {
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (game.pregameItems().isPregameItem(currentItem)
                || game.pregameItems().isPregameItem(cursorItem)) {
            event.setCancelled(true);
            return;
        }

        if (event.getHotbarButton() >= 0) {
            ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
            if (game.pregameItems().isPregameItem(hotbarItem)) {
                event.setCancelled(true);
                return;
            }
        }

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.equals(player.getInventory())) {
            int slot = event.getSlot();
            if (game.pregameItems().isProtectedSlot(slot)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Game game = GameManager.getInstance().getCurrentGame();
        if (!shouldProtect(game, player)) {
            return;
        }

        for (ItemStack item : event.getNewItems().values()) {
            if (game.pregameItems().isPregameItem(item)) {
                event.setCancelled(true);
                return;
            }
        }

        for (int slot : event.getRawSlots()) {
            if (game.pregameItems().isProtectedSlot(slot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Game game = GameManager.getInstance().getCurrentGame();

        if (!shouldProtect(game, player)) {
            return;
        }

        if (game.pregameItems().isPregameItem(event.getMainHandItem())
                || game.pregameItems().isPregameItem(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    private boolean shouldProtect(Game game, Player player) {
        if (game == null) {
            return false;
        }

        if (!game.players().isInGame(player)) {
            return false;
        }

        return game.getState() == GameState.WAITING || game.getState() == GameState.COUNTDOWN;
    }
}