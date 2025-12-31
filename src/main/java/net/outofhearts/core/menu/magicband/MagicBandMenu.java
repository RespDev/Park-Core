package net.outofhearts.core.menu.magicband;

import dev.triumphteam.gui.guis.Gui;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.entity.Player;

public class MagicBandMenu {

    public MagicBandMenu(Player player) {
        Gui gui = Gui.gui()
                .title(TextUtil.legacyComponent("<blue>MagicBand"))
                .rows(3)
                .disableAllInteractions()
                .create();

        gui.open(player);
    }
}