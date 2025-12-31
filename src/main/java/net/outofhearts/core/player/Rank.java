package net.outofhearts.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum Rank {

    DIRECTOR("Director", "<red>Director ", ChatColor.RED, "<yellow>", true, 13),
    GUEST("Guest", "<dark_aqua>Guest ", ChatColor.DARK_AQUA, "<white>", false, 1);

    @Getter private String name;
    @Getter private String scoreboardName;
    @Getter private ChatColor tagColor;
    @Getter private String chatColor;
    @Getter private boolean isOp;
    @Getter private int rankId;

    /*
     * Every time luckperms ranks are added you need to match them to enum ranks
     */
    public static Rank getPlayerRank(Player player) {
        User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(player);

        String primaryGroup = user.getCachedData().getMetaData().getPrimaryGroup();

        if (primaryGroup == null) return Rank.GUEST;

        return switch (primaryGroup) {
            case "director" -> Rank.DIRECTOR;
            default -> Rank.GUEST;
        };
    }
}