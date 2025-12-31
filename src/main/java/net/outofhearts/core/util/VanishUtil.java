package net.outofhearts.core.util;

import net.outofhearts.core.Core;
import net.outofhearts.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class VanishUtil {
    private static final Set<UUID> vanishedPlayers = new HashSet<>();

    /*
     * Check if the specified player is vanished.
     */
    public static Boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    /*
     * Return a copy of the vanishedPlayers HashSet.
     */
    public static Set<UUID> getVanishedPlayers() {
        return new HashSet<>(vanishedPlayers); // Return a copy for safety
    }

    /*
     * Unvanish the specified player.
     */
    public static void unvanishPlayer(Player player) {
        if (vanishedPlayers.contains(player.getUniqueId())) {
            vanishedPlayers.remove(player.getUniqueId());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(Core.getInstance(), player);
            }
        }
    }

    /*
     * Vanish the specified player.
     */
    public static void vanishPlayer(Player player) {
        if (!vanishedPlayers.contains(player.getUniqueId())) {
            vanishedPlayers.add(player.getUniqueId());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                // Minimum rank to be able to see vanished players
                if (Core.getInstance().getPlayerHandler().getRank(onlinePlayer).getRankId() < Rank.DIRECTOR.getRankId())
                    onlinePlayer.hidePlayer(Core.getInstance(), player);
            }
        }
    }
}