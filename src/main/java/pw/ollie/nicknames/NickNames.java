package pw.ollie.nicknames;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class NickNames extends JavaPlugin {
	private NickNameManager manager;
	private File configFile;
	private YamlConfiguration config;
	private File nicksFile;
	private YamlConfiguration nicks;

	// Options
	private boolean duplicates;
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

		config = YamlConfiguration.loadConfiguration(configFile);
		nicks = YamlConfiguration.loadConfiguration(nicksFile);

		if (!config.contains("allow-duplicate-nicknames")) {
			config.set("allow-duplicate-nicknames", false);
		}
		if (!config.contains("join-and-quit-message-colour")) {
			config.set("join-and-quit-message-colour", "&e");
		}

		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		duplicates = config.getBoolean("allow-duplicate-nicknames", false);
		jqMsgColour = config.getString("join-and-quit-message-colour", "&e");

		manager = new NickNameManager(this);
		manager.loadNicks();

		final NickCommand nickCommand = new NickCommand(manager);
		getCommand("nick").setExecutor(nickCommand);
		getCommand("nicknames").setExecutor(nickCommand);

		getServer().getPluginManager().registerEvents(new NickNamesListener(this), this);
	}

	@Override
	public void onDisable() {
		manager.saveNicks(nicksFile);
	}

	public NickNameManager getNickManager() {
		return manager;
	}

	public String getJoinQuitMessageColour() {
		return jqMsgColour;
	}

	public boolean allowDuplicates() {
		return duplicates;
	}

	public YamlConfiguration getNicksConfig() {
		return nicks;
	}
}
