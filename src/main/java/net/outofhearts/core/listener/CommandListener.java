package net.outofhearts.core.listener;

import net.outofhearts.core.Core;
import net.outofhearts.core.model.Message;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
 * Block unwanted vanilla commands for users who are not a Developer
 */
public class CommandListener implements Listener {

    private static final Set<String> BLOCKED_COMMANDS = new HashSet<>(Arrays.asList(
            "pl", "plugins", "version", "ver", "verbose", "me", "about", "help", "icanhasbukkit", "?", "trigger"
    ));

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String command = message.substring(1).split(" ")[0].toLowerCase();

        Rank rank = Core.getInstance().getPlayerHandler().getRank(event.getPlayer());
        if (rank.getRankId() < Rank.DIRECTOR.getRankId()) {
            if (BLOCKED_COMMANDS.contains(command)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Message.NO_PERMISSION);
                return;
            }

            if (message.contains(":")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(TextUtil.mm("<red>You cannot use commands containing ':'"));
            }
        }
    }
}