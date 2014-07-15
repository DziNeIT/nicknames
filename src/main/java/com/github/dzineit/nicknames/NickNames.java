package com.github.dzineit.nicknames;

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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!nicksFile.exists()) {
			try {
				nicksFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config = YamlConfiguration.loadConfiguration(configFile);
		nicks = YamlConfiguration.loadConfiguration(nicksFile);

		duplicates = config.getBoolean("allow-duplicate-nicknames", false);

		manager = new NickNameManager(this);
		manager.loadNicks();

		NickCommand nickCommand = new NickCommand(manager);
		getCommand("nick").setExecutor(nickCommand);
		getCommand("nicknames").setExecutor(nickCommand);

		getServer().getPluginManager().registerEvents(manager, this);
	}

	@Override
	public void onDisable() {
		manager.saveNicks(nicksFile);
	}

	public boolean allowDuplicates() {
		return duplicates;
	}

	public YamlConfiguration getNicksConfig() {
		return nicks;
	}
}
