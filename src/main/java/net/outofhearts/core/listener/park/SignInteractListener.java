package net.outofhearts.core.listener.park;

import net.outofhearts.core.handler.park.ServerSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        // Check sign clicks
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Block b = event.getClickedBlock();
            if (b.getType().equals(Material.SIGN) || b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST)) {
                Sign s = (Sign) b.getState();
                ServerSign.SignEntry signEntry = ServerSign.getByHeader(s.getLine(0));
                if (signEntry != null) {
                    signEntry.getHandler().onInteract(player, s, event);
                }
                return;
            }
        }
    }
}