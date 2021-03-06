package pw.ollie.nicknames.command;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import pw.ollie.nicknames.NickNameManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Handles the /nicknames command in NickNames
 */
public class NickNamesCommand implements CommandExecutor {
    /**
     * The plugin nick name manager
     */
    private final NickNameManager nickManager;

    public NickNamesCommand(final NickNameManager nickManager) {
        this.nickManager = nickManager;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        // This should only be called for the 'nicknames' command
        assert cmd.getName().equalsIgnoreCase("nicknames");

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "[NickNames]" + ChatColor.GREEN
                    + " /nick <nickname> - Allows changing your nick.");
            sender.sendMessage(ChatColor.GREEN
                    + " /nick <playername> <nickname> - Allows changing <playername>'s nick.");
            sender.sendMessage(ChatColor.GOLD + "[NickNames]" + ChatColor.RED
                    + " /nick off - Allows removing your nick.");
            sender.sendMessage(ChatColor.GOLD + "[NickNames]" + ChatColor.RED
                    + " /nicknames lookup <nickname> - Look up <nickname>");
            return true;
        }

        final String arg = args[0].toLowerCase();
        if (arg.equals("lookup")) {
            if (sender instanceof ConsoleCommandSender || sender.hasPermission("nicknames.lookup")) {
                if (args.length > 1) {
                    if (nickManager.allowingDuplicates()) {
                        sender.sendMessage(ChatColor.DARK_RED
                                + "Can't lookup a nick when duplicate nicknames are allowed!");
                    } else {
                        final Set<UUID> players = nickManager.getPlayersFromNickName(args[1]);
                        if (players.size() == 0) {
                            sender.sendMessage(ChatColor.DARK_RED + "No players have that nick!");
                        } else if (players.size() == 1) {
                            sender.sendMessage(ChatColor.GRAY + "Player with nick '" + args[1] + "' is called " + Bukkit.getPlayer(players.iterator().next()).getName());
                        } else if (players.size() > 1) {
                            sender.sendMessage(ChatColor.GRAY + "Players with nick " + args[1] + ":");
                            sender.sendMessage(ChatColor.GRAY + formatPlayerNames(players));
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "Usage: /nicknames lookup <nick>");
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do that!");
            }
        }

        return true;
    }

    private String formatPlayerNames(final Set<UUID> uuids) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<UUID> it = uuids.iterator();
        for (int i = 0; i < uuids.size(); i++) {
            final UUID id = it.next();
            builder.append(Bukkit.getPlayer(id).getName());
            if (i != uuids.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
}
