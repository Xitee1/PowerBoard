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

	private static final String designLine = PowerBoard.pr+ChatColor.GRAY+"X"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GOLD+"PowerBoard"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GRAY+"X";

	// String designLine = PowerBoard.pr+"§7X§e§m-----§6Scoreboard§e§m-----§7X";
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String arg2, String[] args) {
		if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
			// PowerBoard info

			s.sendMessage(designLine);
			s.sendMessage(PowerBoard.pr+ChatColor.YELLOW+"Installed version: "+ChatColor.DARK_AQUA+"v"+updater.getCurrentVersion());
			s.sendMessage(PowerBoard.pr+ChatColor.YELLOW+"Newest version: "+ChatColor.DARK_AQUA+"v"+updater.getLatestVersion());
			s.sendMessage(PowerBoard.pr+ChatColor.YELLOW+"Author: "+ChatColor.DARK_AQUA+instance.getDescription().getAuthors());
			s.sendMessage(designLine);

			return true;
		} else if((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("toggle")) {
			// scoreboard toggle

			// check if sender is a player
			if(!(s instanceof Player)) {
				s.sendMessage("Only players can execute this command!");
				return true;
			}

			// check if sender has permission for this command
			if(!checkPermission(s, permPrefix+"toggle.scoreboard"))
				return true;

			// check if the scoreboard is enabled before trying to toggle it
			if(!instance.getConfig().getBoolean("scoreboard")) {
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"Sorry, but the scoreboard is disabled on this server.");
				return true;
			}

			Player p = (Player) s;

			// toggle the scoreboard for the player
			if(ScoreboardPlayer.players.containsKey(p)) {
				ScoreboardPlayer.removeScoreboard(p, true);
				s.sendMessage(PowerBoard.pr+ChatColor.GRAY+"Scoreboard "+ChatColor.RED+"disabled.");
			}else {
				ScoreboardPlayer.setScoreboard(p, false, null);
				s.sendMessage(PowerBoard.pr+ChatColor.GRAY+"Scoreboard "+ChatColor.GREEN+"enabled.");
			}

			return true;
		}else if(args.length == 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
			// PowerBoard reload

			// check if sender has permission for this command
			if(!checkPermission(s, permPrefix+"reload"))
				return true;

			// reload PB
			Config.reloadConfigs(s);

			return true;
		}else if((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("update")) {
			// PowerBoard update (download newest jar)
			if(!checkPermission(s, permPrefix+"update"))
				return true;

			if(args.length == 1) {
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"Warning: After successful update, you have to restart your server as soon as possible!" +
						"This command also can cause glitches. It is recommended to update the plugin manually."
						+ "Please type "+ChatColor.YELLOW+"/"+cmd.getName()+" update confirm"+ChatColor.RED+" to confirm.");
				return true;
			}

			if(args.length == 2) {
				s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Downloading the newest version...");

				if(args[1].equalsIgnoreCase("confirm")) {
					if(updater.downloadFile(false)) {
						s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Download finished. Please restart your server as soon as possible!");
					}else {
						s.sendMessage(PowerBoard.pr+ChatColor.RED+"Download failed! Please try it later again. More infos are available in the console.");
						s.sendMessage(PowerBoard.pr+ChatColor.RED+"You can also try it with '/"+cmd.getName()+" update force'");
					}
					return true;
				}

				if(args[1].equalsIgnoreCase("force")) {
					if(updater.downloadFile(true)) {
						s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Download finished. Please restart your server as soon as possible!");
					}else {
						s.sendMessage(PowerBoard.pr+ChatColor.RED+"Sorry, force update did not work. Please manually update the plugin.");
					}
					return true;
				}

				sendInfoPage(s);
			}

			return true;
		}else if(args.length == 1 && args[0].equalsIgnoreCase("debug")) {
			// scoreboard debug (temporarily enable debug until next restart)

			// check if sender has permission for this command
			if(!checkPermission(s, permPrefix+".debug"))
				return true;

			PowerBoard.debug = !PowerBoard.debug;
			if(PowerBoard.debug) {
				s.sendMessage(PowerBoard.pr+ChatColor.GRAY+"Debug "+ChatColor.GREEN+"enabled.");
			}else {
				s.sendMessage(PowerBoard.pr+ChatColor.GRAY+"Debug "+ChatColor.RED+"disabled.");
			}

			return true;
		}else
			sendInfoPage(s);
		return true;
	}

	private void sendInfoPage(CommandSender s) {
		boolean isPlayer = s instanceof Player;

		s.sendMessage(designLine);
		s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb info "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Shows all infos about the plugin.");

		if(isPlayer && s.hasPermission(permPrefix+"toggle.scoreboard"))
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb toggle "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Toggle the scoreboard.");

		if(s.hasPermission(permPrefix+"reload"))
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb reload "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Reload all configs.");

		if(s.hasPermission(permPrefix+"update"))
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb update "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Download the newest version.");

		if(s.hasPermission(permPrefix+"debug"))
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb debug "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Toggle the debug (temporarily).");

		s.sendMessage(designLine);
	}

	private boolean checkPermission(CommandSender s, String permission) {
		if(!s.hasPermission(permission)) {
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"Sorry, but you need the permission "+ChatColor.GRAY+permission+ChatColor.RED+" to do that.");
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
}
