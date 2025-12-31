package net.outofhearts.core.listener.park;

import net.outofhearts.core.handler.park.ServerSign;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignChangeListener implements Listener {

    public SignChangeListener() {
        ServerSign.registerSign("[Disposal]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(Player player, SignChangeEvent event) {
                event.setLine(1, "");
                event.setLine(2, ChatColor.BLACK + "" + ChatColor.BOLD + "Trash");
                event.setLine(3, "");
            }

            @Override
            public void onInteract(Player player, Sign s, PlayerInteractEvent event) {
                player.openInventory(Bukkit.createInventory(player, 36, ChatColor.BLUE + "Disposal"));
            }
        });
        ServerSign.registerSign("[Show]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(Player player, SignChangeEvent event) {
                event.setLine(1, "Click to sync");
                event.setLine(2, "your music!");
                event.setLine(3, "");
            }
        });
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        Block b = event.getBlock();

        for (int i = 0; i < 4; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
        String line1 = event.getLine(0);

        ServerSign.SignEntry signEntry = ServerSign.getByHeader(line1);

        if (signEntry != null) {
            event.setLine(0, ChatColor.BLUE + signEntry.getHeader());
            signEntry.getHandler().onSignChange(player, event);
        }
    }
}