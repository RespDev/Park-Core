package net.outofhearts.core.command;

import net.outofhearts.core.lamp.RequireRank;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;

import java.util.HashMap;
import java.util.Map;

public final class GamemodeCommand {

    private static final Map<String, GameMode> GAMEMODE_ALIASES = new HashMap<>();

    static {
        GAMEMODE_ALIASES.put("s", GameMode.SURVIVAL);
        GAMEMODE_ALIASES.put("c", GameMode.CREATIVE);
        GAMEMODE_ALIASES.put("a", GameMode.ADVENTURE);
        GAMEMODE_ALIASES.put("sp", GameMode.SPECTATOR);
    }

    @Command({"gamemode", "gm"})
    @Description("Changes the players gamemode")
    @RequireRank(Rank.DIRECTOR)
    public void gamemode(Player player, @Named("gamemode") String gamemodeInput, @Optional @Named("player") Player target) {
        GameMode gameMode = parseGameMode(gamemodeInput);
        if (gameMode == null) {
            player.sendMessage(TextUtil.mm("<red>Invalid gamemode!"));
            return;
        }

        Player targetPlayer = target != null ? target : player;

        targetPlayer.setGameMode(gameMode);
        if (targetPlayer == player) {
            player.sendMessage(TextUtil.mm("<green>Your gamemode has been set to: <aqua>" + gameMode.name().toUpperCase() + "<green>."));
        } else {
            player.sendMessage(TextUtil.mm("<aqua>" + targetPlayer.getName() + "'s <green>gamemode has been set to: <aqua>" + gameMode.name().toUpperCase() + "<green>."));
            targetPlayer.sendMessage(TextUtil.mm("<green>Your gamemode has been set to: <aqua>" + gameMode.name().toUpperCase() + "<green>."));
        }
    }

    @Command("gmc")
    @Description("Changes your gamemode to creative")
    @RequireRank(Rank.DIRECTOR)
    public void gmc(Player player) {
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(TextUtil.mm("<green>Your gamemode has been set to: <aqua>CREATIVE<green>."));
    }

    @Command("gms")
    @Description("Changes your gamemode to survival")
    @RequireRank(Rank.DIRECTOR)
    public void gms(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(TextUtil.mm("<green>Your gamemode has been set to: <aqua>SURVIVAL<green>."));
    }

    @Command("gma")
    @Description("Changes your gamemode to adventure")
    @RequireRank(Rank.DIRECTOR)
    public void gma(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.sendMessage(TextUtil.mm("<green>Your gamemode has been set to: <aqua>ADVENTURE<green>."));
    }

    @Command("gmsp")
    @Description("Changes your gamemode to spectator")
    @RequireRank(Rank.DIRECTOR)
    public void gmsp(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(TextUtil.mm("<green>Your gamemode has been set to: <aqua>SPECTATOR<green>."));
    }

    private GameMode parseGameMode(String input) {
        input = input.toLowerCase();
        if (GAMEMODE_ALIASES.containsKey(input)) {
            return GAMEMODE_ALIASES.get(input);
        }

        try {
            return GameMode.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}