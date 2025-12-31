package net.outofhearts.core.command;

import net.outofhearts.core.Core;
import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;

public final class BalanceCommand {

    @Command({"balance", "bal"})
    @Description("View your balance")
    @RequireRank(Rank.GUEST)
    public void balance(Player player) {
        long balance = Core.getInstance().getPlayerHandler().getBalance(player);

        player.sendMessage(TextUtil.mm("<yellow><bold>Your Balance: <green><bold>$ <green>" + balance));
    }
}