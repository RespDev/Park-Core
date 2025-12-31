package net.outofhearts.core.player.impl;

import net.outofhearts.core.Core;
import net.outofhearts.core.menu.BackpackMenu;
import net.outofhearts.core.menu.magicband.MagicBandMenu;
import net.outofhearts.core.player.InventoryData;
import net.outofhearts.core.player.PlayerData;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.sql.Database;
import net.outofhearts.core.util.TextUtil;
import net.outofhearts.core.util.TpaUtil;
import net.outofhearts.core.util.VanishUtil;
import net.outofhearts.core.util.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHandler {

    /*
     * Player data hashmap which caches the player data
     * of all connected players locally to prevent repetition
     * of database calls.
     */
    private Map<Player, PlayerData> playerData = new HashMap();

    /*
     * Handle player join
     */
    public void handleLogin(Player player) {
        Core.runTaskAsynchronously(() -> {
            loadPlayerProfile(player);
            Core.getInstance().getDefaultScoreboard().addPlayer(player);

            // Hide all vanished players if below the rank shown
            if (Core.getInstance().getPlayerHandler().getRank(player).getRankId() < Rank.DIRECTOR.getRankId()) {
                for (UUID vanishedUUID : VanishUtil.getVanishedPlayers()) {
                    Player vanishedPlayer = Bukkit.getPlayer(vanishedUUID);
                    if (vanishedPlayer != null && vanishedPlayer.isOnline()) {
                        player.hidePlayer(Core.getInstance(), vanishedPlayer);
                    }
                }
            }
        });
    }

    /*
     * Handle player quit
     */
    public void handleQuit(Player player) {
        Core.runTaskAsynchronously(() -> {
            Core.getInstance().getDefaultScoreboard().removePlayer(player);
            TpaUtil.logout(player);
            if (VanishUtil.isVanished(player)) {
                VanishUtil.unvanishPlayer(player);
            }

            PlayerInventory playerInventory = player.getInventory();
            Database.getInstance().saveInventory(player, playerInventory.getContents(), playerInventory.getArmorContents());
            player.getInventory().clear();

            // Clear cached player data after everything unloads
            playerData.remove(player);
        });
    }

    /*
     * Give hotbar items
     */
    private void giveParkItems(Player player) {
        ItemStack rideItem = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
                .setDisplayName("&aThis Slot is Reserved for &7(Ride Items)")
                .addAction(event -> {
                    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
                    event.setCancelled(true);
                    // TODO: Call custom reserved slot click event
                })
                .setUnableToMove(true)
                .setUnableToDrop(true)
                .build();

        ItemStack backpackItem = new ItemBuilder(Material.TRAPPED_CHEST, 1)
                .setDisplayName("&aBackpack &7(Right-Click)")
                .addAction(event -> {
                    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
                    event.setCancelled(true);
                    event.getPlayer().closeInventory();
                    new BackpackMenu(event.getPlayer());
                })
                .setUnableToMove(true)
                .setUnableToDrop(true)
                .build();

        ItemStack autographBookItem = new ItemBuilder(Material.BOOK, 1)
                .setDisplayName("&3Autograph Book")
                .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addAction(event -> {
                    if (!event.getAction().equals(Action.PHYSICAL)) {
                        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(TextUtil.mm("&aAutograph Book coming soon!"));
                    }
                })
                .setUnableToMove(true)
                .setUnableToDrop(true)
                .build();

        ItemStack magicbandItem = new ItemBuilder(Material.FIREWORK_CHARGE, 1)
                .setDisplayName("&aMagicBand &7(Right-Click)")
                .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .addAction(event -> {
                    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
                    event.setCancelled(true);
                    event.getPlayer().closeInventory();
                    new MagicBandMenu(event.getPlayer());
                })
                .setUnableToMove(true)
                .setUnableToDrop(true)
                .build();

        // Set Inventory slots
        player.getInventory().setItem(3, rideItem);
        player.getInventory().setItem(2, backpackItem);
        player.getInventory().setItem(1, autographBookItem);
        player.getInventory().setItem(0, magicbandItem);
    }

    /*
     * Load player profile from the database
     */
    private void loadPlayerProfile(Player player) {
        Core.runTaskAsynchronously(() -> {
            // Load player database profile
            Core.getDatabase().createProfile(player.getUniqueId(), player.getName());

            Rank rank = Rank.getPlayerRank(player);
            long balance = Core.getDatabase().getMoney(player.getUniqueId());
            InventoryData inventoryData = Database.getInstance().loadInventory(player.getUniqueId());

            // Cache data locally
            playerData.put(player, new PlayerData(rank, balance));

            // Load player inventory from database
            player.getInventory().setContents(inventoryData.inventory.toArray(new ItemStack[0]));
            player.getInventory().setArmorContents(inventoryData.armor.toArray(new ItemStack[0]));

            // TODO: Check if it is a parks server and if it is give them the park hotbar items
            giveParkItems(player);
        });
    }

    /*
     * Set a players cached rank
     */
    public void setCachedRank(Player player, Rank rank) {
        Core.runTaskAsynchronously(() -> {
            // TODO: If other information is added retrieve it from the cache first as we are only changing the rank
            long balance = getBalance(player);

            // Cache data locally
            playerData.put(player, new PlayerData(rank, balance));
        });
    }

    /*
     * Get the players cached rank
     */
    public Rank getRank(Player player) {
        if (!playerData.containsKey(player)) return Rank.GUEST;
        return playerData.get(player).rank;
    }

    /*
     * Add money to a users balance
     */
    public void addMoney(Player player, long amount) {
        Core.runTaskAsynchronously(() -> {
            // TODO: If other information is added retrieve it from the cache first as we are only changing the balance
            long balance = getBalance(player);
            Rank rank = getRank(player);

            // Add money from profile
            Core.getDatabase().addMoney(player.getUniqueId(), amount);

            // Cache data locally
            playerData.put(player, new PlayerData(rank, balance + amount));
        });
    }

    /*
     * Remove money to a users balance
     */
    public void removeMoney(Player player, long amount) {
        Core.runTaskAsynchronously(() -> {
            // TODO: If other information is added retrieve it from the cache first as we are only changing the balance
            long balance = getBalance(player);
            Rank rank = getRank(player);

            // Remove money from profile
            Core.getDatabase().removeMoney(player.getUniqueId(), amount);

            // Cache data locally
            playerData.put(player, new PlayerData(rank, balance - amount));
        });
    }

    /*
     * Get the players cached balance
     */
    public long getBalance(Player player) {
        if (!playerData.containsKey(player)) return 0;
        return playerData.get(player).balance;
    }
}