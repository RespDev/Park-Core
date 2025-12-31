package net.outofhearts.core;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.outofhearts.core.command.*;
import net.outofhearts.core.config.Settings;
import net.outofhearts.core.lamp.RequireRankCondition;
import net.outofhearts.core.listener.CommandListener;
import net.outofhearts.core.listener.GenericListeners;
import net.outofhearts.core.listener.HotbarListeners;
import net.outofhearts.core.listener.RankUpdateListener;
import net.outofhearts.core.listener.park.SignChangeListener;
import net.outofhearts.core.listener.park.SignInteractListener;
import net.outofhearts.core.player.impl.PlayerHandler;
import net.outofhearts.core.scoreboard.impl.DefaultScoreboard;
import net.outofhearts.core.sql.Database;
import net.outofhearts.core.util.LoggingUtil;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class Core extends JavaPlugin {

    @Getter private static Core instance;
    @Getter private static String version;
    private boolean starting = true;

    private BukkitTask scoreboardTask;
    @Getter private final DefaultScoreboard defaultScoreboard = new DefaultScoreboard();

    @Getter private static Database database;

    private static PlayerHandler playerHandler;

    @Override
    public void onEnable() {
        // Variables
        instance = this;
        version = getDescription().getVersion();

        // Log starting
        LoggingUtil.logMessage("Core", "Starting Core version " + version + " by " + getDescription().getAuthors());

        // Load configurations
        Settings.getInstance().load();

        // Database classes
        database = Database.getInstance();

        // Load luckperms dependency
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms luckPerms = provider.getProvider();

            luckPerms.getEventBus().subscribe(this, NodeMutateEvent.class, new RankUpdateListener()::onRankUpdate);
        } else {
            LoggingUtil.logError("Luckperms not found!");
            Bukkit.shutdown();
        }

        // Load plugin channels
        // TODO: BungeeCord channel communication

        // Handlers
        playerHandler = new PlayerHandler();

        // Register listeners
        registerListeners();

        // Register commands
        registerCommands();

        // Load tasks
        if (Settings.getInstance().isDefaultScoreboard())
            scoreboardTask = Core.runTaskTimerAsynchronously(defaultScoreboard, 0, 1);

        // Log running
        LoggingUtil.logMessage("Core", "Core is now running.");

        // Allow players to join
        runTaskLater(this, () -> {
            setStarting(false);
        }, 20);
    }

    @Override
    public void onDisable() {
        // Keep players from joining
        setStarting(true);

        // Save data
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Save inventory data
            PlayerInventory playerInventory = onlinePlayer.getInventory();
            Database.getInstance().saveInventorySynchronously(onlinePlayer, playerInventory.getContents(), playerInventory.getArmorContents());
            onlinePlayer.getInventory().clear();

            // Kick so they cant change their data anymore before the plugin unloads
            onlinePlayer.kickPlayer(TextUtil.mm("<red>Core is currently reloading."));
        }

        // Log shutdown
        LoggingUtil.logMessage("Core", "Core is now shutting down.");

        // Cancel tasks
        if (scoreboardTask != null && !scoreboardTask.isCancelled())
            scoreboardTask.cancel();

        // Reset scoreboard teams
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : scoreboard.getTeams()) {
            team.unregister();
        }

        // Unload Channels
        // TODO: BungeeCord channel communication

        // Disconnect from Database
        database.disconnect();

        // Log disabled
        LoggingUtil.logMessage("Core", "Core is now disabled.");
    }

    /*
     * Registers all the commands
     */
    private void registerCommands() {
        LoggingUtil.logMessage("Core", "Starting to register commands.");

        /* Setup Lamp */
        BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        handler.registerCondition(RequireRankCondition.INSTANCE);
        handler.register();
        LoggingUtil.logMessage("Core", "Successfully setup Lamp.");

        /* Cast Member */
        handler.register(new FlyCommand());
        handler.register(new GamemodeCommand());
        handler.register(new CmChatCommand());
        handler.register(new InvseeCommand());
        handler.register(new EconomyCommand());
        handler.register(new BroadcastCommand());
        handler.register(new DayCommand());
        handler.register(new NoonCommand());
        handler.register(new NightCommand());
        handler.register(new VanishCommand());

        /* Guest */
        handler.register(new BalanceCommand());
        handler.register(new TpaCommand());

        LoggingUtil.logMessage("Core", "All commands have been registered.");
    }

    /*
     * Registers all the listeners
     */
    private void registerListeners() {
        LoggingUtil.logMessage("Core", "Starting to register listeners.");

        /* Core Listeners */
        registerListener(new GenericListeners());
        registerListener(new CommandListener());
        registerListener(new HotbarListeners());

        /* Park Listeners */
        // TODO: Check if it is a parks server
        registerListener(new SignChangeListener());
        registerListener(new SignInteractListener());

        LoggingUtil.logMessage("Core", "All listeners have been registered.");
    }

    /*
     * Utility function to register a listener
     */
    public static void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getInstance());
        LoggingUtil.logMessage("Listener", "Registered the " + listener.getClass().getSimpleName() + " listener.");
    }

    /*
     * Set the server starting to true or false
     */
    public static void setStarting(boolean isStarting) {
        if (!isStarting) LoggingUtil.logMessage("Core", "<dark_green>The server is now joinable!");
        else LoggingUtil.logMessage("Core", "<dark_red>The server is no longer joinable!");
        getInstance().starting = isStarting;
    }

    /*
     * Check whether core is still starting
     */
    public static boolean isStarting() {
        return getInstance().starting;
    }

    /*
     * Run task asynchronously
     */
    public static void runTaskAsynchronously(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(getInstance(), task);
    }

    /*
     * Run task timer asynchronously
     */
    public static BukkitTask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(getInstance(), task, delay, period);
    }

    /*
     * Run task later
     */
    public static void runTaskLater(Plugin plugin, Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }

    /*
     * Run task
     */
    public static void runTask(Plugin plugin, Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    /*
     * Returns PlayerHandler utility class
     */
    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}