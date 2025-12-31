package net.outofhearts.core.config;

import net.outofhearts.core.Core;
import net.outofhearts.core.util.LoggingUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/*
 * Configuration file implementation layer for Core
 */
public class Config extends YamlConfiguration {
    private final Core plugin = Core.getInstance();
    private final File file;

    public Config(File parent, String name) {
        this.file = new File(parent, name);

        if (!this.file.exists()) {
            (this.options()).copyDefaults(true);
            this.plugin.saveResource(name, false);
        }
        this.load();
    }

    public Config(String name) {
        this(Core.getInstance().getDataFolder(), name);
    }

    public void load() {
        try {
            super.load(this.file);
            LoggingUtil.logMessage("Config", "Successfully loaded the " + file.getName() + " config.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            super.save(this.file);
            LoggingUtil.logMessage("Config", "Successfully saved the " + file.getName() + " config.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}