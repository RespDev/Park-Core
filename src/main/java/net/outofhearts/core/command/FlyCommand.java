package net.outofhearts.core.command;

import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;

public final class FlyCommand {

    @Command("fly")
    @Description("Toggle your flight state")
    @RequireRank(Rank.DIRECTOR)
    public void fly(Player player) {
        player.setAllowFlight(!player.getAllowFlight());
        player.sendMessage(player.getAllowFlight() ? TextUtil.mm("<green>Flight enabled.") : TextUtil.mm("<red>Flight disabled."));
    }
}