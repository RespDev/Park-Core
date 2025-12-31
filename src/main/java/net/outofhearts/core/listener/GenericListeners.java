package net.outofhearts.core.listener;

import net.outofhearts.core.Core;
import net.outofhearts.core.sql.Database;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GenericListeners implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        // Check if the server is still starting
        if (Core.isStarting()) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(TextUtil.mm("<aqua>Server is still starting."));
        }

        // Check if the database is connected
        if (!Database.getInstance().isConnected()) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(TextUtil.mm("<red>Not connected to database."));
        }

        // Check if they are connected to the network and if they are not kick them
        if (!Database.getInstance().isOnline(event.getUniqueId())) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(TextUtil.mm("<red>You are not connected to our network!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        player.getInventory().clear();
        Core.getInstance().getPlayerHandler().handleLogin(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        Core.getInstance().getPlayerHandler().handleQuit(player);
    }
}
