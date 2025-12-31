package net.outofhearts.core.command;

import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;

public final class InvseeCommand {

    @Command({"invsee", "inventorysee"})
    @Description("View a players inventory")
    @RequireRank(Rank.DIRECTOR)
    public void invsee(Player player, @Named("player") Player target) {
        player.openInventory(target.getInventory());
        player.sendMessage(TextUtil.mm("<green>You are now viewing <aqua>" + target.getName() + "'s <green>inventory."));
    }
}