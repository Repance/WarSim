package com.repance.warsim.service;

import com.repance.warsim.WarSim;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PregameItemService {

    public static final int TEAM_A_SLOT = 3;
    public static final int MAP_SLOT = 4;
    public static final int TEAM_B_SLOT = 5;
    public static final int LEAVE_SLOT = 8;

    private static final String KEY_ITEM_TYPE = "pregame_item_type";

    private final NamespacedKey itemTypeKey;

    public PregameItemService() {
        this.itemTypeKey = new NamespacedKey(WarSim.getInstance(), KEY_ITEM_TYPE);
    }

    public void givePregameItems(Player player) {
        clearPregameItems(player);

        player.getInventory().setItem(TEAM_A_SLOT, createItem(
                Material.RED_WOOL,
                "§c§lTeam A",
                "team_a",
                List.of("§7Klik om §cTeam A §7te kiezen")
        ));

        player.getInventory().setItem(MAP_SLOT, createItem(
                Material.MAP,
                "§e§lMap Selector",
                "map",
                List.of("§7Map kiezen komt later")
        ));

        player.getInventory().setItem(TEAM_B_SLOT, createItem(
                Material.BLUE_WOOL,
                "§9§lTeam B",
                "team_b",
                List.of("§7Klik om §9Team B §7te kiezen")
        ));

        player.getInventory().setItem(LEAVE_SLOT, createItem(
                Material.BARRIER,
                "§c§lLeave Queue",
                "leave",
                List.of("§7Klik om WarSim te verlaten")
        ));

        player.updateInventory();
    }

    public void clearPregameItems(Player player) {
        clearSlot(player, TEAM_A_SLOT);
        clearSlot(player, MAP_SLOT);
        clearSlot(player, TEAM_B_SLOT);
        clearSlot(player, LEAVE_SLOT);
        player.updateInventory();
    }

    private void clearSlot(Player player, int slot) {
        player.getInventory().setItem(slot, null);
    }

    public boolean isPregameItem(ItemStack item) {
        return getItemType(item) != null;
    }

    public boolean isTeamAItem(ItemStack item) {
        return "team_a".equals(getItemType(item));
    }

    public boolean isMapItem(ItemStack item) {
        return "map".equals(getItemType(item));
    }

    public boolean isTeamBItem(ItemStack item) {
        return "team_b".equals(getItemType(item));
    }

    public boolean isLeaveItem(ItemStack item) {
        return "leave".equals(getItemType(item));
    }

    private String getItemType(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }

        if (!item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(itemTypeKey, PersistentDataType.STRING);
    }

    private ItemStack createItem(Material material, String name, String itemType, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(itemTypeKey, PersistentDataType.STRING, itemType);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isProtectedSlot(int slot) {
        return slot == TEAM_A_SLOT
                || slot == MAP_SLOT
                || slot == TEAM_B_SLOT
                || slot == LEAVE_SLOT;
    }
}