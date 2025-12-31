package net.outofhearts.core.menu;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.StorageGui;
import net.outofhearts.core.Core;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BackpackMenu {

    private static final int BACKPACK_SIZE = 54;

    private final Player player;
    private StorageGui gui;

    /**
     * Opens the backpack menu to the provided Player
     */
    public BackpackMenu(Player player) {
        this.player = player;
        if (Core.isStarting()) {
            player.sendMessage(TextUtil.mm("<red>You cannot open your backpack while the server is shutting down!"));
            return;
        }
        Core.runTaskAsynchronously(() -> {
            List<ItemStack> itemsList = Core.getDatabase().loadBackpackItems(player.getUniqueId());

            Core.runTask(Core.getInstance(), () -> {
                gui = Gui.storage()
                        .title(TextUtil.legacyComponent("<blue>Your Backpack"))
                        .rows(6)
                        .enableAllInteractions()
                        .enableOtherActions()
                        .create();

                for (int i = 0; i < BACKPACK_SIZE && i < itemsList.size(); i++) {
                    ItemStack item = itemsList.get(i);
                    if (item != null && item.getType() != Material.AIR) {
                        gui.getInventory().setItem(i, item);
                    }
                }

                gui.setDefaultClickAction(event -> {
                    Core.runTaskLater(Core.getInstance(), this::saveBackpack, 1L);
                });

                gui.setPlayerInventoryAction(event -> {
                    Core.runTaskLater(Core.getInstance(), this::saveBackpack, 1L);
                });

                gui.setDragAction(event -> {
                    Core.runTaskLater(Core.getInstance(), this::saveBackpack, 1L);
                });

                gui.open(player);
            });
        });
    }

    /**
     * Saves the backpack to the database
     */
    private void saveBackpack() {
        ItemStack[] contents = new ItemStack[BACKPACK_SIZE];

        for (int i = 0; i < BACKPACK_SIZE; i++) {
            ItemStack item = gui.getInventory().getItem(i);
            contents[i] = (item != null) ? item : new ItemStack(Material.AIR);
        }

        Core.runTaskAsynchronously(() -> {
            Core.getDatabase().saveBackpackItems(player.getUniqueId(), contents);
        });
    }
}