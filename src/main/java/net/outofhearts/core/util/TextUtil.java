package net.outofhearts.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/*
 * Methods used to format text color and replace variables
 */
public class TextUtil {

    public static String mm(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String result = input
                // Colors
                .replace("<black>", "&0")
                .replace("<dark_blue>", "&1")
                .replace("<dark_green>", "&2")
                .replace("<dark_aqua>", "&3")
                .replace("<dark_red>", "&4")
                .replace("<dark_purple>", "&5")
                .replace("<gold>", "&6")
                .replace("<gray>", "&7")
                .replace("<grey>", "&7")
                .replace("<dark_gray>", "&8")
                .replace("<dark_grey>", "&8")
                .replace("<blue>", "&9")
                .replace("<green>", "&a")
                .replace("<aqua>", "&b")
                .replace("<red>", "&c")
                .replace("<light_purple>", "&d")
                .replace("<yellow>", "&e")
                .replace("<white>", "&f")
                // Formatting
                .replace("<bold>", "&l")
                .replace("<b>", "&l")
                .replace("<italic>", "&o")
                .replace("<i>", "&o")
                .replace("<underline>", "&n")
                .replace("<u>", "&n")
                .replace("<strikethrough>", "&m")
                .replace("<st>", "&m")
                .replace("<obfuscated>", "&k")
                .replace("<obf>", "&k")
                .replace("<reset>", "&r")
                .replace("<r>", "&r");

        return ChatColor.translateAlternateColorCodes('&', result);
    }

    public static String[] mm(String... inputs) {
        String[] results = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            results[i] = mm(inputs[i]);
        }
        return results;
    }

    public static Component legacyComponent(String input) {
        return LegacyComponentSerializer.legacySection()
                .deserialize(TextUtil.mm(input));
    }

    // TODO: Add placeholders like {global_player_count}
    public static String replaceVariables(Player player, String message) {
        return message.replace("{player}", player.getName());
    }
}
