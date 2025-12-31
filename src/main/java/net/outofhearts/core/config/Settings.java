package net.outofhearts.core.config;

import lombok.Getter;
import net.outofhearts.core.util.LoggingUtil;

/*
 * Core's settings file
 */
public class Settings {

    @Getter private final static Settings instance = new Settings();

    @Getter private Config config;

    @Getter private boolean defaultScoreboard;
    @Getter private String serverName;
    @Getter private boolean production;

    @Getter private String databaseHost;
    @Getter private String databasePort;
    @Getter private String database;
    @Getter private String databaseUsername;
    @Getter private String databasePassword;

    private Settings() {
    }

    public void load() {
        config = new Config("settings.yml");

        // General
        defaultScoreboard = config.getBoolean("Default_Scoreboard");
        serverName = config.getString("Server_Name");
        production = config.getBoolean("Production");

        // Database
        databaseHost = config.getString("Database.Host");
        databasePort = config.getString("Database.Port");
        database = config.getString("Database.Database");
        databaseUsername = config.getString("Database.Username");
        databasePassword = config.getString("Database.Password");

        LoggingUtil.logMessage("Settings", "Successfully cached the settings file.");
    }

    public void save() {
        if (config != null) {
            config.save();
        }
    }
}