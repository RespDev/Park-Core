package net.outofhearts.core.command;

import net.outofhearts.core.Core;
import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.EntitySelector;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("eco")
@Description("Manage player balances")
@CommandPermission("core.staff")
public final class EconomyCommand {

    @DefaultFor("eco")
    @Description("Show economy command help")
    @RequireRank(Rank.DIRECTOR)
    public void help(CommandSender sender) {
        sender.sendMessage(TextUtil.mm("<green>Economy Commands:"));
        sender.sendMessage(TextUtil.mm("<aqua>/eco add <player> <amount> [reason] <green>- Add money to a player"));
        sender.sendMessage(TextUtil.mm("<aqua>/eco remove <player> <amount> <green>- Remove money from a player"));
        sender.sendMessage(TextUtil.mm("<aqua>/eco balance <player> <green>- Check a player's balance"));
        sender.sendMessage(TextUtil.mm("<aqua>/eco help <green>- Show this help menu"));
    }

    @Subcommand("add")
    @Description("Add money to a player")
    @RequireRank(Rank.DIRECTOR)
    public void add(CommandSender sender, @Named("player") EntitySelector<Player> targets, @Named("amount") long amount, @Optional String reason) {
        if (amount <= 0) {
            sender.sendMessage(TextUtil.mm("<red>Amount must be greater than 0."));
            return;
        }

        int count = 0;
        for (Player target : targets) {
            Core.getInstance().getPlayerHandler().addMoney(target, amount);
            target.sendMessage(TextUtil.mm("<yellow>You received <green><bold>$ <green>" + amount + (reason != null ? " <yellow>for " + reason : "") + "<yellow>."));
            count++;
        }

        if (count == 0) {
            sender.sendMessage(TextUtil.mm("<red>No players found."));
        } else {
            sender.sendMessage(TextUtil.mm("<yellow>Added <green><bold>$ <green>" + amount + "<yellow> to <aqua>" + count + " player(s)<yellow>."));
        }
    }

    @Subcommand("remove")
    @Description("Remove money from a player")
    @RequireRank(Rank.DIRECTOR)
    public void remove(CommandSender sender, @Named("player") Player target, @Named("amount") long amount) {
        if (amount <= 0) {
            sender.sendMessage(TextUtil.mm("<red>Amount must be greater than 0."));
            return;
        }

        long oldBalance = Core.getInstance().getPlayerHandler().getBalance(target);
        if (oldBalance - amount < 0) {
            sender.sendMessage(TextUtil.mm("<red>" + target.getName() + " doesn't have enough money for this amount to be removed."));
            return;
        }

        Core.getInstance().getPlayerHandler().removeMoney(target, amount);

        sender.sendMessage(TextUtil.mm("<red>Removed <green><bold>$ <green>" + amount + " <red>from <aqua>" + target.getName() + "'s <red>balance."));

        target.sendMessage(TextUtil.mm("<green><bold>$ <green>" + amount + " <red>was removed from your balance."));
    }

    @Subcommand("balance")
    @Description("Check a player's balance")
    @RequireRank(Rank.DIRECTOR)
    public void balance(CommandSender sender, @Named("player") Player target) {
        long balance = Core.getInstance().getPlayerHandler().getBalance(target);

        sender.sendMessage(TextUtil.mm("<yellow><bold>" + target.getName() + "'s Balance: <green><bold>$ <green>" + balance));
    }
}