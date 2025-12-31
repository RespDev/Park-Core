package net.outofhearts.core.command;

import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;

public class NightCommand {

    @Command("night")
    @Description("Set time to night")
    @RequireRank(Rank.DIRECTOR)
    public void night(CommandSender sender) {
        if (sender instanceof Player player) {
            player.getWorld().setTime(16000);
            player.sendMessage(TextUtil.mm("<gray>Time has been set to <green>Night <gray>in world <green>" + player.getWorld().getName() + "<gray>."));
        } else if (sender instanceof BlockCommandSender commandSender) {
            commandSender.getBlock().getWorld().setTime(16000);
            commandSender.sendMessage(TextUtil.mm("<gray>Time has been set to <green>Night <gray>in world <green>" + commandSender.getBlock().getWorld().getName() + "<gray>."));
        } else {
            for (World world : Bukkit.getWorlds()) {
                world.setTime(16000);
                sender.sendMessage(TextUtil.mm("<gray>Time has been set to <green>Night <gray>in world <green>" + world.getName() + "<gray>."));
            }
        }
    }
}
