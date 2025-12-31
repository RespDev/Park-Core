package net.outofhearts.core.util;

import org.bukkit.Bukkit;

/*
 * These methods are used to log to console
 */
public final class LoggingUtil {

    public static void logMessage(String name, String message) {
        Bukkit.getConsoleSender().sendMessage(TextUtil.mm("<blue>" + name + "> <white>" + message));
    }

    public static void logError(String message) {
        Bukkit.getLogger().severe("[Core ERROR] " + message);
    }
}
