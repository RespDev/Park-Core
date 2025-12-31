package net.outofhearts.core.sql;

import lombok.Getter;
import net.outofhearts.core.Core;
import net.outofhearts.core.config.Settings;
import net.outofhearts.core.player.InventoryData;
import net.outofhearts.core.util.LoggingUtil;
import net.outofhearts.core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Database {

    @Getter private static final Database instance = new Database();
    private final Settings settings;
    private Connection connection;

    private Database() {
        this.settings = Settings.getInstance();

        this.connect();
        this.createTables();
    }

    /**
     * Connect to the database
     */
    private void connect() {
        try {
            if (isConnected())
                return;

            Class.forName("com.mysql.jdbc.Driver");

            final String jdbcUrl =
                    "jdbc:mysql://" + this.settings.getDatabaseHost() + ":" + this.settings.getDatabasePort() + "/" + this.settings.getDatabase() + "?useSSL=true&autoReconnect=true";

            LoggingUtil.logMessage("Database", "Attempting to connect to the database.");

            this.connection = DriverManager.getConnection(jdbcUrl, this.settings.getDatabaseUsername(), this.settings.getDatabasePassword());

            LoggingUtil.logMessage("Database", "Successfully connected to the database.");
        } catch (Exception e) {
            LoggingUtil.logError("Failed to connect to the database: " + e.getMessage());
            if (Settings.getInstance().isProduction()) Bukkit.shutdown();
        }
    }

    /**
     * Create the tables for the database
     */
    private void createTables() {
        Core.runTaskAsynchronously(() -> {
            if (!isConnected()) return;

            /*
             * Create the player profiles table
             */
            try {
                Statement statement = connection.createStatement();

                final String query = "CREATE TABLE IF NOT EXISTS player_profiles (" +
                        "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                        "username VARCHAR(16) NOT NULL, " +
                        "money BIGINT NOT NULL DEFAULT 0, " +
                        "online BOOL NOT NULL DEFAULT FALSE, " +
                        "first_join TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "last_seen TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);";

                statement.executeUpdate(query);
                LoggingUtil.logMessage("Database", "Table 'player_profiles' has been created or already exists.");
            } catch (SQLException e) {
                LoggingUtil.logError("Failed to create the table 'player_profiles': " + e.getMessage());
            }

            /*
             * Create player backpacks table
             */
            try {
                Statement statement = connection.createStatement();

                final String query = "CREATE TABLE IF NOT EXISTS player_backpacks (" +
                        "player_uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                        "backpack_data MEDIUMTEXT NOT NULL, " +
                        "last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);";

                statement.executeUpdate(query);
                LoggingUtil.logMessage("Database", "Table 'player_backpacks' has been created or already exists.");
            } catch (SQLException e) {
                LoggingUtil.logError("Failed to create the table 'player_backpacks': " + e.getMessage());
            }

            /*
             * Create player inventorys table
             */
            try {
                Statement statement = connection.createStatement();

                final String query = "CREATE TABLE IF NOT EXISTS player_inventories (" +
                        "player_uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                        "inventory_data MEDIUMTEXT NOT NULL, " +
                        "armor_data MEDIUMTEXT NOT NULL, " +
                        "last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);";

                statement.executeUpdate(query);
                LoggingUtil.logMessage("Database", "Table 'player_inventories' has been created or already exists.");
            } catch (SQLException e) {
                LoggingUtil.logError("Failed to create the table 'player_inventories': " + e.getMessage());
            }

            /*
             * Create the punishments table
             */

            /*
             * Create the shops table
             */

            /*
             * Create the restaurants table
             */

            /*
             * Create the cosmetics table
             */

            /*
             * Create the warps table
             */

            /*
             * Create the ride leaderboard table
             */
        });
    }

    /**
     * Disconnect from the database
     */
    public void disconnect() {
        try {
            if (isConnected()) {
                LoggingUtil.logMessage("Database", "Attempting to disconnect from the database.");

                this.connection.close();

                LoggingUtil.logMessage("Database", "Successfully disconnected from the database.");
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if still connected to the database
     */
    public boolean isConnected() {
        try {
            if (this.connection != null && !this.connection.isClosed()) return true;
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create a new player profile
     */
    public void createProfile(UUID uuid, String username) {
        Core.runTaskAsynchronously(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO player_profiles (uuid, username, money, online) VALUES (?, ?, 0, ?) " +
                            "ON DUPLICATE KEY UPDATE username = VALUES(username)"
            )) {
                statement.setString(1, uuid.toString());
                statement.setString(2, username);
                statement.setBoolean(3, true);

                statement.executeUpdate();
            } catch (SQLException e) {
                LoggingUtil.logError("Failed to create player profile: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Set player online status
     */
    public void setOnline(UUID uuid, boolean online) {
        Core.runTaskAsynchronously(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "UPDATE player_profiles SET online = ? WHERE uuid = ?"
            )) {
                statement.setBoolean(1, online);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                LoggingUtil.logError("Failed to set online status: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Get player online status
     */
    public boolean isOnline(UUID uuid) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT online FROM player_profiles WHERE uuid = ?"
        )) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBoolean("online");
            }
        } catch (SQLException e) {
            LoggingUtil.logError("Failed to get online status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get player money
     */
    public long getMoney(UUID uuid) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT money FROM player_profiles WHERE uuid = ?"
        )) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong("money");
            }
        } catch (SQLException e) {
            LoggingUtil.logError("Failed to get player money: " + e.getMessage());
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * Set player money to exact amount
     */
    public void setMoney(UUID uuid, long amount) {
        Core.runTaskAsynchronously(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "UPDATE player_profiles SET money = ? WHERE uuid = ?"
            )) {
                statement.setLong(1, amount);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                LoggingUtil.logError("Failed to set player money: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Add money to player balance
     */
    public void addMoney(UUID uuid, long amount) {
        Core.runTaskAsynchronously(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "UPDATE player_profiles SET money = money + ? WHERE uuid = ?"
            )) {
                statement.setLong(1, amount);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                LoggingUtil.logError("Failed to add player money: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Remove money from player balance
     */
    public void removeMoney(UUID uuid, long amount) {
        Core.runTaskAsynchronously(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "UPDATE player_profiles SET money = money - ? WHERE uuid = ?"
            )) {
                statement.setLong(1, amount);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                LoggingUtil.logError("Failed to remove player money: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Load backpack items for a specific player.
     */
    public List<ItemStack> loadBackpackItems(UUID uuid) {
        List<ItemStack> items = new ArrayList<>();

        try (PreparedStatement statement = this.connection.prepareStatement(
                "SELECT backpack_data FROM player_backpacks WHERE player_uuid = ?"
        )) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                ItemStack[] emptyItems = new ItemStack[54];
                for (int i = 0; i < 54; i++) {
                    emptyItems[i] = new ItemStack(Material.AIR);
                }
                saveBackpackItems(uuid, emptyItems);
                return items;
            }

            String serializedData = resultSet.getString("backpack_data");
            ItemStack[] loadedItems = Utils.deserializeItemStackArray(serializedData);

            for (ItemStack item : loadedItems) {
                items.add(item);
            }

        } catch (SQLException e) {
            LoggingUtil.logError("Failed to load backpack items: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Save backpack items for a specific player.
     */
    public void saveBackpackItems(UUID uuid, ItemStack[] items) {
        Core.runTaskAsynchronously(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "REPLACE INTO player_backpacks (player_uuid, backpack_data) VALUES (?, ?)"
            )) {
                String serializedData = Utils.serializeItemStackArray(items);

                statement.setString(1, uuid.toString());
                statement.setString(2, serializedData);

                statement.executeUpdate();
            } catch (SQLException e) {
                LoggingUtil.logError("Failed to save backpack items: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // TODO: Cross server inventory database methods
    // TODO: Need locking with BEGIN; and COMMIT;

    /**
     * Save inventory asynchronously for a specific player.
     */
    public void saveInventory(Player player, ItemStack[] inventory, ItemStack[] armor) {
        Core.runTaskAsynchronously(() -> {
            saveInventoryInternal(player, inventory, armor);
        });
    }

    /**
     * Save inventory synchronously for a specific player.
     */
    public void saveInventorySynchronously(Player player, ItemStack[] inventory, ItemStack[] armor) {
        saveInventoryInternal(player, inventory, armor);
    }

    private void saveInventoryInternal(Player player, ItemStack[] inventory, ItemStack[] armor) {
        try {
            this.connection.setAutoCommit(false);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO player_inventories (player_uuid, inventory_data, armor_data) " +
                            "VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE inventory_data = VALUES(inventory_data), armor_data = VALUES(armor_data)"
            )) {
                String inventoryData = Utils.serializeItemStackArray(inventory);
                String armorData = Utils.serializeItemStackArray(armor);

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, inventoryData);
                statement.setString(3, armorData);

                statement.executeUpdate();

                this.connection.commit();
            } catch (SQLException e) {
                this.connection.rollback();
                LoggingUtil.logError("Failed to save inventory items: " + e.getMessage());
                e.printStackTrace();
            } finally {
                this.connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LoggingUtil.logError("Failed to manage transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load inventory for a specific player.
     */
    public InventoryData loadInventory(UUID uuid) {
        try {
            this.connection.setAutoCommit(false);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT inventory_data, armor_data FROM player_inventories WHERE player_uuid = ? FOR UPDATE"
            )) {
                statement.setString(1, uuid.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    InventoryData data = null;

                    if (resultSet.next()) {
                        String inventoryData = resultSet.getString("inventory_data");
                        String armorData = resultSet.getString("armor_data");

                        ItemStack[] contentsArray = Utils.deserializeItemStackArray(inventoryData);
                        ItemStack[] armorArray = Utils.deserializeItemStackArray(armorData);

                        List<ItemStack> inventory = Arrays.asList(contentsArray);
                        List<ItemStack> armor = Arrays.asList(armorArray);

                        data = new InventoryData(inventory, armor);
                    }

                    this.connection.commit();
                    return data;
                }
            } catch (SQLException e) {
                this.connection.rollback();
                LoggingUtil.logError("Failed to load inventory items: " + e.getMessage());
                e.printStackTrace();
                return null;
            } finally {
                this.connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LoggingUtil.logError("Failed to manage transaction: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}