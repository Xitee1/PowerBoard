 package de.xite.scoreboard.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xite.scoreboard.files.Config;
import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.utils.Updater;
import net.md_5.bungee.api.ChatColor;

public class scoreboard_cmd implements CommandExecutor{
	String designLine1 = Main.pr+"§7X§e§m-----§6Scoreboard§e§m-----§7X";
	String designLine2 = Main.pr+"§7X§e§m-----§6Scoreboard§e§m-----§7X";
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("info")) {
				s.sendMessage(designLine1);
				s.sendMessage(Main.pr+ChatColor.YELLOW+"Your version: §3v"+Main.pl.getDescription().getVersion());
				s.sendMessage(Main.pr+ChatColor.YELLOW+"Newest version: §3v"+Updater.getVersion());
				s.sendMessage(Main.pr+ChatColor.YELLOW+"Author: §3Xitee TEST");
				s.sendMessage(designLine2);
			}else if(args[0].equalsIgnoreCase("update")) {
				if(!(s instanceof Player) || (s instanceof Player && ((Player) s).hasPermission("scoreboard.update"))) {
					s.sendMessage(Main.pr+ChatColor.GREEN+"Downloading the newest version...");
					if(Updater.downloadFile()) {
						s.sendMessage(Main.pr+ChatColor.GREEN+"Download finished! To apply the new update, you have to restart your server.");
					}else {
						s.sendMessage(Main.pr+ChatColor.RED+"Download failed! Please try it later again. More infos are in the console.");
					}
				}else {
					s.sendMessage(Main.pr+ChatColor.RED+"The following permission is required to execute this command: §7scoreboard.update");
				}
			}else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
				if(!(s instanceof Player) || (s instanceof Player && ((Player) s).hasPermission("scoreboard.reload"))) {
					s.sendMessage(Main.pr+ChatColor.GRAY+"Reloading scoreboard. Warning: You should not use this command regularly because of bugs and it may needs more performance. If you want that this plugin runs stable, restart the server.");
					s.sendMessage(Main.pr+ChatColor.GRAY+"loading §econfig.yml §7...");
					Bukkit.getScheduler().cancelTasks(Main.pl);
					Config.loadConfig();
					if(Main.pl.getConfig().getBoolean("scoreboard")) {
						Main.unregisterScoreboards();
						Main.registerScoreboards();
					}
					s.sendMessage(Main.pr+ChatColor.GREEN+"All config files have been reloaded!");
				}else {
					s.sendMessage(Main.pr+ChatColor.RED+"The following permission is required to execute this command: §7scoreboard.reload");
				}
			}else {
				sendMainPage(s);
			}
		}else {
			sendMainPage(s);
		}
		return false;
	}
	public void sendMainPage(CommandSender s) {
		s.sendMessage(designLine1);
		s.sendMessage(Main.pr+"§7- §c/sb info §8- §7Shows all infos about the plugin");
		s.sendMessage(Main.pr+"§7- §c/sb reload §8- §7Reload all configs");
		s.sendMessage(Main.pr+"§7- §c/sb update §8- §7Downloading the newest version");
		s.sendMessage(designLine2);
	}
}
