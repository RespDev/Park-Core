package net.outofhearts.core.command;

import net.outofhearts.core.Core;
import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import net.outofhearts.core.util.VanishUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;

@Command({"vanish", "v"})
@Description("Vanish from guests")
@RequireRank(Rank.DIRECTOR)
public final class VanishCommand {

    @DefaultFor({"vanish", "v"})
    public void vanish(CommandSender sender, @Optional Player target) {
        Player targetPlayer;

        if (target == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(TextUtil.mm("<red>Please specify a player when executing this command from the console!"));
                return;
            }
            targetPlayer = (Player) sender;
        } else {
            targetPlayer = target;
        }

        toggleVanish(sender, targetPlayer);
    }

    private void toggleVanish(CommandSender sender, Player targetPlayer) {
        boolean isVanished = VanishUtil.isVanished(targetPlayer);

        if (isVanished) {
            VanishUtil.unvanishPlayer(targetPlayer);
            targetPlayer.sendMessage(TextUtil.mm("<dark_aqua>You have become visible."));
        } else {
            VanishUtil.vanishPlayer(targetPlayer);
            targetPlayer.sendMessage(TextUtil.mm("<dark_aqua>You have vanished. Poof."));
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Minimum rank to receive vanish notifications
            if (Core.getInstance().getPlayerHandler().getRank(onlinePlayer).getRankId() >= Rank.DIRECTOR.getRankId() &&
                    !onlinePlayer.getUniqueId().equals(targetPlayer.getUniqueId())) {
                onlinePlayer.sendMessage(TextUtil.mm("<yellow>" + targetPlayer.getName() +
                        (isVanished ? " has become visible." : " has vanished. Poof.")));
            }
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(TextUtil.mm("<yellow>" + targetPlayer.getName() +
                    " is now " + (isVanished ? "visible" : "invisible") + "."));
        }
    }
}