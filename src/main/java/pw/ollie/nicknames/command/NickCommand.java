package pw.ollie.nicknames.command;

import pw.ollie.nicknames.NickNameManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Deals with the /nick command in NickNames
 */
public final class NickCommand implements CommandExecutor {
    private final NickNameManager nickManager;

    public NickCommand(final NickNameManager nickManager) {
        this.nickManager = nickManager;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        // This should only be called for the 'nick' command
        assert cmd.getName().equalsIgnoreCase("nick");

        if (!(sender instanceof Player)) {
            sender.sendMessage("[NickNames] Only players can have nicknames!");
            return true;
        }
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.DARK_RED + "Usage: /nick <nickname> or /nick <playername> <nickname>");
            return true;
        }

        final Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("off")) {
            if (args.length == 1) {
                if (player.hasPermission("nicknames.nick.self")) {
                    if (nickManager.setNickName(player.getUniqueId(), null)) {
                        player.setDisplayName(player.getName());
                        player.setPlayerListName(player.getName());
                        player.sendMessage(ChatColor.GRAY + "Nickname successfully removed!");
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "Failed to change nickname");
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "You don't have permission to change your nickname!");
                }
            } else {
                if (player.hasPermission("nicknames.nick.other")) {
                    final String other = args[1];
                    @SuppressWarnings("deprecation")
                    final Player target = Bukkit.getPlayer(other);
                    if (target == null) {
                        sender.sendMessage(ChatColor.DARK_RED + "That player isn't online!");
                    } else {
                        if (nickManager.setNickName(target.getUniqueId(), null)) {
                            target.setDisplayName(target.getName());
                            target.setPlayerListName(target.getName());
                            sender.sendMessage(ChatColor.GRAY + "Disabled nickname for " + other);
                            player.sendMessage(ChatColor.GRAY + "Your nickname was disabled");
                        } else {
                            sender.sendMessage(ChatColor.DARK_RED + "Couldn't disable nickname for " + other);
                        }
                    }
                }
            }
        } else {
            if (args.length == 1) {
                if (player.hasPermission("nicknames.nick.self")) {
                    final String nick = ChatColor.translateAlternateColorCodes('&', args[0]);
                    if (nickManager.setNickName(player.getUniqueId(), nick)) {
                        player.setDisplayName(player.getName());
                        player.setDisplayName(nick);
                        player.setPlayerListName(nick);
                        player.sendMessage(ChatColor.GRAY + "Nickname successfully changed!");
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "Couldn't set nick! Is it already taken?");
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "You don't have permission to change your nickname!");
                }
            } else if (args.length == 2) {
                if (player.hasPermission("nicknames.nick.other")) {
                    @SuppressWarnings("deprecation")
                    final Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        final String nick = ChatColor.translateAlternateColorCodes('&', args[1]);
                        if (nickManager.setNickName(target.getUniqueId(), nick)) {
                            target.setDisplayName(nick);
                            target.setPlayerListName(nick);
                            sender.sendMessage(ChatColor.GRAY + "Set " + target.getName() + "'s nick to " + nick);
                        } else {
                            sender.sendMessage(ChatColor.DARK_RED + "Couldn't set nick! Is it already taken?");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Player " + args[0] + " is currently offline. :(");
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + "You don't have permission to change other people's nicknames!");
                }
            }
        }

        return true;
    }
}
