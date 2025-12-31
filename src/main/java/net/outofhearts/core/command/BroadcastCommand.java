package net.outofhearts.core.command;

import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;

public final class BroadcastCommand {

    @Command({"broadcast", "bc"})
    @Description("Broadcast an announcement")
    @RequireRank(Rank.DIRECTOR)
    public void broadcast(CommandSender sender, @Optional String message) {
        if (message == null) {
            sender.sendMessage(TextUtil.mm("<red>/bc [Message]"));
            return;
        }

        Bukkit.broadcastMessage(TextUtil.mm("<white>[<aquaInformation<white>] " +
                message));
    }
}