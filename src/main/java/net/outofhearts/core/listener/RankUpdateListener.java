package net.outofhearts.core.listener;

import com.google.common.eventbus.Subscribe;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.model.user.User;
import net.outofhearts.core.Core;
import net.outofhearts.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RankUpdateListener {

    /*
     * Updates a players cached rank when their rank changes
     */
    @Subscribe
    public void onRankUpdate(NodeMutateEvent event) {
        if (!event.isUser()) return;

        User user = (User) event.getTarget();
        Core.runTask(Core.getInstance(), () -> {
            Player player = Bukkit.getPlayer(user.getUniqueId());

            if (player == null || !player.isOnline()) {
                return;
            }

            Rank rank = Rank.getPlayerRank(player);

            if (rank != null) {
                Core.getInstance().getPlayerHandler().setCachedRank(player, rank);
            }
        });
    }
}
