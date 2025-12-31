package net.outofhearts.core.lamp;

import net.outofhearts.core.Core;
import net.outofhearts.core.model.Message;
import net.outofhearts.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.process.CommandCondition;

import java.util.List;
import java.util.UUID;

public enum RequireRankCondition implements CommandCondition {
    INSTANCE;

    @Override public void test(CommandActor actor, ExecutableCommand command, List<String> arguments) {
        if (command.hasAnnotation(RequireRank.class)) {
            RequireRank rankAnnotation = command.getAnnotation(RequireRank.class);
            Rank requiredRank = rankAnnotation.value();

            UUID playerUUID = actor.getUniqueId();
            Player player = Bukkit.getPlayer(playerUUID);

            Rank playerRank = (player == null) ? Rank.DIRECTOR : Core.getInstance().getPlayerHandler().getRank(player);

            if (playerRank.getRankId() < requiredRank.getRankId()) {
                throw new CommandErrorException(Message.NO_PERMISSION);
            }
        }
    }
}