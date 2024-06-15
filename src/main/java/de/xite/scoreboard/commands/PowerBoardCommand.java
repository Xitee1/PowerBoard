package de.xite.scoreboard.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.Config;
import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.utils.Updater;
import net.md_5.bungee.api.ChatColor;

public class PowerBoardCommand implements CommandExecutor, TabCompleter {
	private static final PowerBoard instance = PowerBoard.getInstance();
	private static final Updater updater = PowerBoard.getUpdater();
	private static final String permPrefix = PowerBoard.getPermissionPrefix();

	private static final String designLine = PowerBoard.pbChatPrefix +ChatColor.GRAY+"X"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GOLD+"PowerBoard"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GRAY+"X";

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String arg2, String[] args) {
		int len = args.length;

		if(len == 1 && args[0].equalsIgnoreCase("info")) {
			// info command
			sendInfo(s);

		} else if(len == 1 && args[0].equalsIgnoreCase("toggle")) {
			// toggle scoreboard command
			toggleScoreboard(s);

		}else if(len == 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
			// scoreboard reload command
			reloadPowerBoard(s);

		}else if((len == 1 || len == 2) && args[0].equalsIgnoreCase("update")) {
			// powerboard update command (download latest file to plugins folder)
			updatePowerBoard(s, cmd, args);

		}else if(len == 1 && args[0].equalsIgnoreCase("debug")) {
			// temporarily toggle powerboard debug mode
			toggleDebug(s);

		}else {
			sendHelpPage(s);
		}

		return true;
	}

	/**
	 * Checks if the command sender has a specific permission and prints a no permission message if he does not.
	 *
	 * @param s command sender to check permission for
	 * @param permission the permission string
	 * @return true if the player has the permission, false otherwise
	 */
	private boolean checkPermission(CommandSender s, String permission) {
		if(!s.hasPermission(permission)) {
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"Sorry, but you need the permission "+ChatColor.GRAY+permission+ChatColor.RED+" to do that.");
			return false;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String alias, String[] args) {
		boolean isPlayer = s instanceof Player;

		List<String> list = new ArrayList<>();
		if(args.length == 1) {
			list.add("info");

			if(isPlayer && s.hasPermission(permPrefix+"toggle.scoreboard"))
				list.add("toggle");

			if(s.hasPermission(permPrefix+"reload"))
				list.add("reload");

			if(s.hasPermission(permPrefix+"update"))
				list.add("update");

			if(s.hasPermission(permPrefix+"debug"))
				list.add("debug");
		}
		return list;
	}

	// -- sub-commands -- //
	private void sendHelpPage(CommandSender s) {
		boolean isPlayer = s instanceof Player;

		s.sendMessage(designLine);
		s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"/pb info "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Shows all infos about the plugin.");

		if(isPlayer && s.hasPermission(permPrefix+"toggle.scoreboard"))
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"/pb toggle "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Toggle the scoreboard.");

		if(s.hasPermission(permPrefix+"reload"))
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"/pb reload "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Reload all configs.");

		if(s.hasPermission(permPrefix+"update"))
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"/pb update "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Download the newest version.");

		if(s.hasPermission(permPrefix+"debug"))
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"/pb debug "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Toggle the debug (temporarily).");

		s.sendMessage(designLine);
	}

	private void sendInfo(CommandSender s) {
		// PowerBoard info
		String newestVersion = ChatColor.GRAY + "disabled";
		if(updater.isUpdateCheckEnabled()) {
			newestVersion = ChatColor.DARK_AQUA + "v" + updater.getLatestVersion();
		}

		s.sendMessage(designLine);
		s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.YELLOW+"Installed version: "+ChatColor.DARK_AQUA+"v"+updater.getCurrentVersion());
		s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.YELLOW+"Newest version: "+newestVersion);
		s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.YELLOW+"Author: "+ChatColor.DARK_AQUA+String.join(",", instance.getDescription().getAuthors()));
		s.sendMessage(designLine);
	}

	private void toggleScoreboard(CommandSender s) {
		// check if sender is a player
		if(!(s instanceof Player)) {
			s.sendMessage("Only players can execute this command!");
			return;
		}

		// check if sender has permission for this command
		if(!checkPermission(s, permPrefix + "toggle.scoreboard")) {
			return;
		}

		// check if the scoreboard is enabled before trying to toggle it
		if(!instance.getConfig().getBoolean("scoreboard")) {
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"Sorry, but the scoreboard is disabled on this server.");
			return;
		}

		Player p = (Player) s;

		// toggle the scoreboard for the player
		if(ScoreboardPlayer.players.containsKey(p)) {
			ScoreboardPlayer.removeScoreboard(p, true);
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.GRAY+"Scoreboard "+ChatColor.RED+"disabled.");
		}else {
			ScoreboardPlayer.setScoreboard(p, false, null);
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.GRAY+"Scoreboard "+ChatColor.GREEN+"enabled.");
		}
	}

	private void reloadPowerBoard(CommandSender s) {
		// check if sender has permission for this command
		if(!checkPermission(s, permPrefix + "reload"))
			return;

		// reload PB
		Config.reloadConfigs(s);
	}

	private void updatePowerBoard(CommandSender s, Command cmd, String[] args) {
		if(!checkPermission(s, permPrefix + "update"))
			return;

		int len = args.length;

		if(len == 1) {
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"Warning: "+ChatColor.GRAY+
					"Please "+ChatColor.GOLD+"restart your server"+ChatColor.GRAY+" as soon as possible after updating! " +ChatColor.GOLD+
					"Use the updater with caution!"+ChatColor.GRAY+" The built in updater can cause various bugs and unexpected behavior. " +
					"If possible, manually update the plugin. " +
					"Please type "+ChatColor.YELLOW+"/"+cmd.getName()+" update confirm"+ChatColor.GRAY+" to "+ChatColor.GOLD+"accept the risks.");
			return;
		}

		if(len == 2) {
			if(args[1].equalsIgnoreCase("confirm")) {
				s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.GREEN+"Downloading the latest version...");
				if(updater.downloadFile(false)) {
					s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.GREEN+"Update finished. Please restart your server as soon as possible!");
				}else {
					s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"Update failed! More infos are available in the console.");
					s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"You can also try it with '/"+cmd.getName()+" update force'");
				}
				return;
			}

			if(args[1].equalsIgnoreCase("force")) {
				s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.GREEN+"Downloading the latest version...");
				if(updater.downloadFile(true)) {
					s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.GREEN+"Update finished. Please restart your server as soon as possible!");
				}else {
					s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"Sorry, force update did not work. Please update the plugin manually.");
				}
				return;
			}

			sendHelpPage(s);
		}
	}

	private void toggleDebug(CommandSender s) {
		// check if sender has permission for this command
		if(!checkPermission(s, permPrefix + ".debug"))
			return;

		PowerBoard.debug = !PowerBoard.debug;
		if(PowerBoard.debug) {
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.GRAY+"Debug "+ChatColor.GREEN+"enabled.");
		}else {
			s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.GRAY+"Debug "+ChatColor.RED+"disabled.");
		}
	}
}
