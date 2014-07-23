package pw.ollie.nicknames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public final class NickCommand implements CommandExecutor {
	private final NickNameManager nickManager;

	public NickCommand(final NickNameManager nickManager) {
		this.nickManager = nickManager;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {
		final String cn = cmd.getName().toLowerCase();

		if (cn.equals("nick")) {
			if ((sender instanceof Player)) {
				final Player player = (Player) sender;
				if (args.length < 1 || args.length > 2) {
					player.sendMessage(ChatColor.DARK_RED
							+ "Usage: /nick <nickname> or /nick <playername> <nickname>");
				} else {
					if (args[0].equalsIgnoreCase("off")) {
						if (args.length == 1) {
							if (player.hasPermission("nicknames.nick.self")) {
								if (nickManager.setNickName(
										player.getUniqueId(), null)) {
									player.setDisplayName(player.getName());
									player.sendMessage(ChatColor.GRAY
											+ "Nickname successfully removed!");
								} else {
									player.sendMessage(ChatColor.DARK_RED
											+ "Failed to change nickname");
								}
							} else {
								player.sendMessage(ChatColor.DARK_RED
										+ "You don't have permission to change your nickname!");
							}
						} else {
							if (player.hasPermission("nicknames.nick.other")) {
								final String other = args[1];
								@SuppressWarnings("deprecation")
								final Player target = Bukkit.getPlayer(other);
								if (target == null) {
									sender.sendMessage(ChatColor.DARK_RED
											+ "That player isn't online!");
								} else {
									if (nickManager.setNickName(
											target.getUniqueId(), null)) {
										target.setDisplayName(target.getName());
										sender.sendMessage(ChatColor.GRAY
												+ "Disabled nickname for "
												+ other);
										player.sendMessage(ChatColor.GRAY
												+ "Your nickname was disabled");
									} else {
										sender.sendMessage(ChatColor.DARK_RED
												+ "Couldn't disable nickname for "
												+ other);
									}
								}
							}
						}
					} else {
						if (args.length == 1) {
							if (player.hasPermission("nicknames.nick.self")) {
								if (nickManager.setNickName(
										player.getUniqueId(), args[0])) {
									player.setDisplayName(player.getName());
									player.setDisplayName(args[0]);
									player.sendMessage(ChatColor.GRAY
											+ "Nickname successfully changed!");
								} else {
									player.sendMessage(ChatColor.DARK_RED
											+ "Couldn't set nick! Is it already taken?");
								}
							} else {
								player.sendMessage(ChatColor.DARK_RED
										+ "You don't have permission to change your nickname!");
							}
						} else if (args.length == 2) {
							if (player.hasPermission("nicknames.nick.other")) {
								@SuppressWarnings("deprecation")
								final Player target = Bukkit.getPlayer(args[0]);
								if (target != null) {
									if (nickManager.setNickName(
											target.getUniqueId(), args[1])) {
										target.setDisplayName(args[1]);
										sender.sendMessage(ChatColor.GRAY
												+ "Set " + target.getName()
												+ "'s nick to " + args[1]);
									} else {
										sender.sendMessage(ChatColor.DARK_RED
												+ "Couldn't set nick! Is it already taken?");
									}
								} else {
									player.sendMessage(ChatColor.RED
											+ "Player " + args[0]
											+ " is currently offline. :(");
								}
							} else {
								player.sendMessage(ChatColor.DARK_RED
										+ "You don't have permission to change other people's nicknames!");
							}
						}
					}
				}
			} else {
				sender.sendMessage("[NickNames] Only players can have nicknames!");
			}
		} else if (cn.equals("nicknames")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					sender.sendMessage(ChatColor.GOLD + "[NickNames]"
							+ ChatColor.GREEN
							+ " Programmed by PandazNWafflez.");
					sender.sendMessage(ChatColor.GOLD + "[NickNames]"
							+ ChatColor.GREEN
							+ " /nick NICKNAME - Allows changing your nick.");
					sender.sendMessage(ChatColor.GOLD
							+ "[NickNames]"
							+ ChatColor.GREEN
							+ " /nick PLAYERNAME NICKNAME - Allows changing PLAYERNAME's nick.");
					sender.sendMessage(ChatColor.GOLD + "[NickNames]"
							+ ChatColor.RED
							+ " /nick off - Allows removing your nick.");
					sender.sendMessage(ChatColor.GOLD + "[NickNames]"
							+ ChatColor.RED
							+ " /nicknames disable - Disables plugin =(");
				} else {
					sender.sendMessage("[NickNames] Programmed by PandazNWafflez.");
					sender.sendMessage("[NickNames]" + ChatColor.RED
							+ " /nicknames disable - Disables plugin =(");
				}
			} else {
				final String arg = args[0].toLowerCase();
				if (arg.equals("lookup")) {
					if (sender instanceof ConsoleCommandSender
							|| sender.hasPermission("nicknames.lookup")) {
						if (args.length > 1) {
							if (nickManager.allowingDuplicates()) {
								sender.sendMessage(ChatColor.DARK_RED
										+ "Can't lookup a nick when duplicate nicknames are allowed!");
							} else {
								sender.sendMessage(ChatColor.GRAY
										+ "User with the nickname "
										+ args[1]
										+ " is called "
										+ nickManager
												.getPlayerFromNickName(args[1]));
							}
						} else {
							sender.sendMessage(ChatColor.DARK_RED
									+ "Usage: /nicknames lookup <nick>");
						}
					} else {
						sender.sendMessage(ChatColor.DARK_RED
								+ "You don't have permission to do that!");
					}
				}
			}
		}

		return true;
	}
}
