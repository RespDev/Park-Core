package net.outofhearts.core.command;

import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TpaUtil;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;

public final class TpaCommand {

    @Command({"tpa", "teleportrequest"})
    @Description("Send a player a teleport request")
    @RequireRank(Rank.GUEST)
    public void tpa(Player player, @Named("player") Player target) {
        // TODO: Check if target has the player blocked
        TpaUtil.addTeleport(player, target);
    }

    @Command("tpaccept")
    @Description("Accept a teleport request")
    @RequireRank(Rank.GUEST)
    public void tpAccept(Player player) {
        TpaUtil.acceptTeleport(player);
    }

    @Command("tpdeny")
    @Description("Deny a teleport request")
    @RequireRank(Rank.GUEST)
    public void tpDeny(Player player) {
        TpaUtil.denyTeleport(player);
    }
}