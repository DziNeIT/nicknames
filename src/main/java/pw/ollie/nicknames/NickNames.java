package pw.ollie.nicknames;

import java.io.File;
import java.io.IOException;

import pw.ollie.nicknames.command.NickCommand;
import pw.ollie.nicknames.command.NickNamesCommand;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the NickNames plugin for Bukkit
 */
public final class NickNames extends JavaPlugin {
    /**
     * The plugin nick name manager
     */
    private NickNameManager manager;
    /**
     * The file for the plugin's configuration
     */
    private File configFile;
    /**
     * The plugin's configuration object
     */
    private YamlConfiguration config;
    /**
     * The file for storing player nicks
     */
    private File nicksFile;
    /**
     * The configuration object for storing player nicks
     */
    private YamlConfiguration nicks;

    // Options

    /**
     * Whether to allow duplicate nick names
     */
    private boolean duplicates;
    /**
     * The colour for join and quit messages
     */
    private String jqMsgColour;

    @Override
    public void onEnable() {
        configFile = new File(getDataFolder(), "config.yml");
        nicksFile = new File(getDataFolder(), "nicks.yml");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        if (!nicksFile.exists()) {
            try {
                nicksFile.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        // Load YML files
        config = YamlConfiguration.loadConfiguration(configFile);
        nicks = YamlConfiguration.loadConfiguration(nicksFile);

        // Create and save default configuration
        if (!config.contains("allow-duplicate-nicknames")) {
            config.set("allow-duplicate-nicknames", false);
        }
        if (!config.contains("join-and-quit-message-colour")) {
            config.set("join-and-quit-message-colour", "&e");
        }

        try {
            config.save(configFile);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        // Get options from config
        duplicates = config.getBoolean("allow-duplicate-nicknames", false);
        jqMsgColour = config.getString("join-and-quit-message-colour", "&e");

        // Load nicks from storage
        manager = new NickNameManager(this);
        manager.loadNicks();

        // Setup commands
        getCommand("nick").setExecutor(new NickCommand(manager));
        getCommand("nicknames").setExecutor(new NickNamesCommand(manager));

        // Register listener with Bukkit
        getServer().getPluginManager().registerEvents(new NickNamesListener(this), this);
    }

    @Override
    public void onDisable() {
        // Store nicks to file
        manager.saveNicks(nicksFile);
    }

    /**
     * Gets the plugin nick manager, used for storage and management of player
     * nick names
     * 
     * @return The plugin nick manager
     */
    public NickNameManager getNickManager() {
        return manager;
    }

    public String getJoinQuitMessageColour() {
        return jqMsgColour;
    }

    public boolean allowDuplicates() {
        return duplicates;
    }

    /**
     * Gets the YamlConfiguration object for the file used to store player nick
     * names
     * 
     * @return The YamlConfiguration for storing player nicks
     */
    public YamlConfiguration getNicksConfig() {
        return nicks;
    }
}
