package pw.ollie.nicknames;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NickNamesListener implements Listener {
	private final NickNameManager nickManager;
	private final String jqMsgColour;

	public NickNamesListener(final NickNames plugin) {
		nickManager = plugin.getNickManager();
		jqMsgColour = plugin.getJoinQuitMessageColour();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final UUID playerId = player.getUniqueId();
		final String nick = nickManager.getNickName(playerId);

		if (nick != null) {
			player.setDisplayName(nick);
			player.setPlayerListName(nick);

			final String joinMsg = event.getJoinMessage();
			if (joinMsg != null && !joinMsg.equals("")) {
				event.setJoinMessage(joinMsg.replaceAll(player.getName(), nick));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final UUID playerId = player.getUniqueId();
		final String nick = nickManager.getNickName(playerId);

		if (nick != null) {
			final String quitMsg = event.getQuitMessage();
			if (quitMsg != null && !quitMsg.equals("")) {
				event.setQuitMessage(quitMsg.replaceAll(player.getName(), nick + jqMsgColour));
			}
		}
	}
}
