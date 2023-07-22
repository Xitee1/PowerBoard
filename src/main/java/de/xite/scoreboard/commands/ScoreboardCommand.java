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

public class ScoreboardCommand implements CommandExecutor, TabCompleter {
	String designLine = PowerBoard.pr+ChatColor.GRAY+"X"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GOLD+"Scoreboard"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GRAY+"X";
	// String designLine = PowerBoard.pr+"§7X§e§m-----§6Scoreboard§e§m-----§7X";

	// TODO: 03/06/2023 Remove this
	PowerBoard pl = PowerBoard.pl;
	
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
			s.sendMessage(designLine);
			s.sendMessage(PowerBoard.pr+ChatColor.YELLOW+"Your version: "+ChatColor.DARK_AQUA+"v"+PowerBoard.pl.getDescription().getVersion());
			s.sendMessage(PowerBoard.pr+ChatColor.YELLOW+"Newest version: "+ChatColor.DARK_AQUA+"v"+Updater.getVersion());
			s.sendMessage(PowerBoard.pr+ChatColor.YELLOW+"Author: "+ChatColor.DARK_AQUA+"Xitee");
			s.sendMessage(designLine);
		} else if((s instanceof Player) && (args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("toggle")) {
			Player p = (Player) s;
			if(p.hasPermission("powerboard.toggle.scoreboard")) {
				if(ScoreboardPlayer.players.containsKey(p)) {
					ScoreboardPlayer.removeScoreboard(p, true);
					s.sendMessage(PowerBoard.pr+ChatColor.GRAY+"Disabled scoreboard.");
					return true;
				}else {
					if(pl.getConfig().getBoolean("scoreboard")) {
						ScoreboardPlayer.setScoreboard(p, false, null);
						s.sendMessage(PowerBoard.pr+ChatColor.GRAY+"Enabled scoreboard.");
						return true;
					}else
						s.sendMessage(PowerBoard.pr+ChatColor.RED+"Sorry, but the scoreboard is disabled on this server.");
				}
			}else
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"Sorry, but you need the permission "+ChatColor.GRAY+"powerboard.toggle.scoreboard"+ChatColor.RED+" to do that.");

		}else if(args.length == 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
			if(!(s instanceof Player) || s.hasPermission("powerboard.reload")) {
				Config.reloadConfigs(s);
				return true;
			}else
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"Sorry, but you need the permission "+ChatColor.GRAY+"powerboard.reload"+ChatColor.RED+" to do that.");
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("update")) {
			if(args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
				if(!(s instanceof Player) || s.hasPermission("powerboard.update")) {
					s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Downloading the newest version...");
					if(Updater.downloadFile(false)) {
						s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Download finished. Please restart your server as soon as possible!");
						return true;
					}else {
						s.sendMessage(PowerBoard.pr+ChatColor.RED+"Download failed! Please try it later again. More infos are available in the console.");
						s.sendMessage(PowerBoard.pr+ChatColor.RED+"If your server is running on Windows, that could cause this problem. Try it with '/pb update confirm force'");
					}
					if(args[2].equalsIgnoreCase("force")) {
						if(Updater.downloadFile(true)) {
							s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Download finished. Please restart your server as soon as possible!");
						}else {
							s.sendMessage(PowerBoard.pr+ChatColor.RED+"Sorry, force update did not work. If the download was successful you can manually stop your server and rename "
							+ "PowerBoard.jar.update to PowerBoard.jar. If there is no update file or that file is corrupted, you have to download the plugin manually.");
						}
					}
				}else
					s.sendMessage(PowerBoard.pr+ChatColor.RED+"Sorry, but you need the permission "+ChatColor.GRAY+"powerboard.update"+ChatColor.RED+" to do that.");
			}else
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"Warning: After successful update, you have to restart your server as soon as possible! "
						+ "Please type "+ChatColor.YELLOW+"/pb update confirm"+ChatColor.RED+" to confirm.");

		}else if(args.length == 1 && args[0].equalsIgnoreCase("debug")) {
			if(!(s instanceof Player) || s.hasPermission("powerboard.debug")) {
				if(PowerBoard.debug) {
					PowerBoard.debug = false;
					s.sendMessage(PowerBoard.pr+ChatColor.RED+"Disabled debug.");
					return true;
				}else {
					PowerBoard.debug = true;
					s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Enabled debug.");
					return true;
				}
			}else
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"Sorry, but you need the permission "+ChatColor.GRAY+"powerboard.debug"+ChatColor.RED+" to do that.");
		}else
			sendInfoPage(s);
		return true;
	}
	public void sendInfoPage(CommandSender s) {
		if(s instanceof Player) {
			Player p = (Player) s;
			s.sendMessage(designLine);
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb info "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Shows all infos about the plugin.");
			if(p.hasPermission("powerboard.toggle.*"))
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb toggle "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Toggle the scoreboard for me.");
			if(p.hasPermission("powerboard.reload"))
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb reload "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Reload all configs.");
			if(p.hasPermission("powerboard.update"))
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb update "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Download the newest version.");
			if(p.hasPermission("powerboard.debug"))
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb debug "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Toggle the debug.");
			s.sendMessage(designLine);
		}else {
			s.sendMessage(designLine);
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb info "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Shows all infos about the plugin.");
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb reload "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Reload all configs.");
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb update "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Download the newest version.");
			s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb debug "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Toggle the debug.");
			s.sendMessage(designLine);
		}
	}
	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if(s instanceof Player) {
			Player p = (Player) s;
			if(args.length == 1) {
				list.add("info");
				if(p.hasPermission("powerboard.toggle.*"))
					list.add("toggle");
				if(p.hasPermission("powerboard.reload"))
					list.add("reload");
				if(p.hasPermission("powerboard.update"))
					list.add("update");
				if(p.hasPermission("powerboard.debug"))
					list.add("debug");
			}
		}else if(args.length == 1) {
			list.add("info");
			list.add("reload");
			list.add("update");
			list.add("debug");
		}
		return list;
	}
}
