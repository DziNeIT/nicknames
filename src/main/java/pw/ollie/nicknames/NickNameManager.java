package pw.ollie.nicknames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class NickNameManager implements Listener {
	private final NickNames plugin;
	private final boolean duplicate;

	private Map<UUID, String> nicknames = new HashMap<>();

	public NickNameManager(final NickNames plugin) {
		this.plugin = plugin;

		duplicate = plugin.allowDuplicates();
	}

	public boolean setNickName(final UUID id, final String nickname) {
		if (nickname == null) {
			nicknames.remove(id);
			return true;
		}

		if (!duplicate && nicknames.containsValue(nickname)) {
			return false;
		}

		if (nicknames.containsKey(id)) {
			nicknames.remove(id);
		}
		nicknames.put(id, nickname);
		return true;
	}

	public void loadNicks() {
		final YamlConfiguration conf = plugin.getNicksConfig();
		final Set<String> keys = conf.getKeys(false);

		for (final String key : keys) {
			nicknames.put(UUID.fromString(key), conf.getString(key));
		}
	}

	public void saveNicks(final File file) {
		final YamlConfiguration conf = plugin.getNicksConfig();
		final List<String> done = new ArrayList<>();

		for (final Entry<UUID, String> entry : nicknames.entrySet()) {
			final String key = entry.getKey().toString();

			conf.set(key, entry.getValue());
			done.add(key);
		}

		for (final String key : conf.getKeys(false)) {
			if (!done.contains(key)) {
				conf.set(key, null);
			}
		}

		try {
			conf.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getNickName(UUID player) {
		return nicknames.get(player);
	}

	public UUID getPlayerFromNickName(String nick) {
		if (duplicate) {
			return null;
		}

		for (Entry<UUID, String> entry : nicknames.entrySet()) {
			if (entry.getValue().equals(nick)) {
				return entry.getKey();
			}
		}

		return null;
	}

	public boolean allowingDuplicates() {
		return duplicate;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final UUID playerId = player.getUniqueId();
		final String nick = nicknames.get(playerId);

		if (nick != null) {
			player.setDisplayName(nick);
			player.setPlayerListName(nick);
		}
	}
}
