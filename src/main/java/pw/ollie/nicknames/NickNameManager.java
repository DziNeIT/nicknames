package pw.ollie.nicknames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * The manager of player nick names for the NickNames Bukkit plugin
 */
public final class NickNameManager {
    /**
     * The NickNames plugin object
     */
    private final NickNames plugin;
    /**
     * Whether to allow multiple players to have the same nickname
     */
    private final boolean duplicate;
    /**
     * A Map of player identifiers to nicknames
     */
    private final Map<UUID, String> nicknames;

    public NickNameManager(final NickNames plugin) {
        this.plugin = plugin;

        duplicate = plugin.allowDuplicates();
        nicknames = new HashMap<>();
    }

    /**
     * Gets the nick name of the player with the given unique identifier
     * 
     * @param player
     *            The unique identifier of the player to get the nick of
     * @return The nick name of the player with the given unique identifier
     */
    public String getNickName(final UUID player) {
        return nicknames.get(player);
    }

    /**
     * Gets the players who have the given nick name
     * 
     * @param nick
     *            The nick name of the players to get
     * @return The unique identifiers of all players with the given nickname
     */
    public Set<UUID> getPlayersFromNickName(String nick) {
        final Set<UUID> res = new HashSet<>();
        nick = ChatColor.stripColor(nick);
        for (final Entry<UUID, String> entry : nicknames.entrySet()) {
            if (ChatColor.stripColor(entry.getValue()).equals(nick)) {
                res.add(entry.getKey());
            }
        }
        return res;
    }

    /**
     * Sets the nickname of the given player to the given nickname
     * 
     * @param id
     *            The unique identifier of the player to set the nick for
     * @param nickname
     *            The new nickname to set for the given player
     * @return Whether the player's nickname was successfully set
     */
    public boolean setNickName(final UUID id, final String nickname) {
        if (nickname == null) {
            nicknames.remove(id);
            return true;
        }

        if (!duplicate && getPlayersFromNickName(nickname).size() > 0) {
            return false;
        }
        if (nicknames.containsKey(id)) {
            nicknames.remove(id);
        }

        nicknames.put(id, nickname);
        return true;
    }

    /**
     * Loads all nick names from the configuration
     */
    public void loadNicks() {
        final YamlConfiguration conf = plugin.getNicksConfig();
        final Set<String> keys = conf.getKeys(false);

        for (final String key : keys) {
            nicknames.put(UUID.fromString(key), conf.getString(key));
        }
    }

    /**
     * Saves all nick names to the given file
     * 
     * @param file
     *            The file to save the nick names to
     */
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
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public boolean allowingDuplicates() {
        return duplicate;
    }
}
