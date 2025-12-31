package net.outofhearts.core.scoreboard.impl;

import net.outofhearts.core.Core;
import net.outofhearts.core.config.Settings;
import net.outofhearts.core.player.Rank;
import net.outofhearts.core.scoreboard.FastBoard;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * The default scoreboard class is the implementation of
 * the default scoreboard which may be toggled in settings.yml
 */
public class DefaultScoreboard implements Runnable {

    private final Map<UUID, FastBoard> boards = new ConcurrentHashMap<>();

    @Override
    public void run() {
        for (FastBoard board : this.boards.values()) {
            updateBoard(board);
        }
    }

    private void updateBoard(FastBoard board) {
        Core.runTaskAsynchronously(() -> {
            Player player = board.getPlayer();

            if (!player.isOnline() || board.isDeleted()) {
                return;
            }

            if (!this.boards.containsKey(player.getUniqueId())) {
                return;
            }

            Rank rank = Core.getInstance().getPlayerHandler().getRank(player);

            board.updateLines(TextUtil.mm(
                    "",
                    "<green><bold>$ <green>" + Core.getInstance().getPlayerHandler().getBalance(player),
                    "",
                    "<green>Rank: " + rank.getScoreboardName(),
                    "",
                    "<green>Online Players: " + Bukkit.getOnlinePlayers().size(),
                    "<green>Server: <green>" + Settings.getInstance().getServerName(),
                    "",
                    "<yellow>play.test.network"
            ));
        });
    }

    public void addPlayer(Player player) {
        Core.runTaskAsynchronously(() -> {
            FastBoard board = new FastBoard(player);

            board.updateTitle(TextUtil.mm("<aqua><bold>Test Network"));

            this.boards.put(player.getUniqueId(), board);
        });
    }

    public void removePlayer(Player player) {
        Core.runTaskAsynchronously(() -> {
            FastBoard board = this.boards.remove(player.getUniqueId());

            if (board != null) {
                board.delete();
            }
        });
    }
}