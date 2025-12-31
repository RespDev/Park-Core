package net.outofhearts.core.command;

import net.outofhearts.core.Core;
import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;

public final class CmChatCommand {

    @Command({"cmchat", "staffchat", "sc", "adminchat", "ac"})
    @Description("Send a message to all online cast members.")
    @RequireRank(Rank.DIRECTOR)
    public void cmchat(CommandSender sender, @Optional String message) {
        if (message == null) {
            sender.sendMessage(TextUtil.mm("<red>Usage: /cmchat <message>"));
            return;
        }

        String senderName = (sender instanceof Player) ? ((Player) sender).getName() :
                (sender instanceof ConsoleCommandSender) ? "Console" : "CommandBlock";

        String formattedMessage = ChatColor.AQUA + "[CM CHAT] " + ChatColor.GRAY + senderName + ": " + ChatColor.WHITE + message;

        for (Player onlinePlayer : sender.getServer().getOnlinePlayers()) {
            if (Core.getInstance().getPlayerHandler().getRank(onlinePlayer).getRankId() >= Rank.DIRECTOR.getRankId())
                onlinePlayer.sendMessage(formattedMessage);
        }
    }
}