 package de.xite.scoreboard.commands;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.Config;
import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.modules.board.ScoreboardManager;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.modules.tablist.TabManager;
import de.xite.scoreboard.utils.Updater;
import net.md_5.bungee.api.ChatColor;

public class ScoreboardCommand implements CommandExecutor{
	String designLine = Main.pr+ChatColor.GRAY+"X"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GOLD+"Scoreboard"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GRAY+"X";
	Main pl = Main.pl;
	
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("info")) {
				s.sendMessage(designLine);
				s.sendMessage(Main.pr+ChatColor.YELLOW+"Your version: "+ChatColor.DARK_AQUA+"v"+Main.pl.getDescription().getVersion());
				s.sendMessage(Main.pr+ChatColor.YELLOW+"Newest version: "+ChatColor.DARK_AQUA+"v"+Updater.getVersion());
				s.sendMessage(Main.pr+ChatColor.YELLOW+"Author: "+ChatColor.DARK_AQUA+"Xitee");
				s.sendMessage(designLine);

			}else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
				if(!(s instanceof Player) || (s instanceof Player && ((Player) s).hasPermission("scoreboard.reload"))) {
					s.sendMessage(Main.pr+ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"config"+ChatColor.GRAY+"...");
					Config.loadConfig();
					if(Main.pl.getConfig().getBoolean("scoreboard")) {
						s.sendMessage(Main.pr+ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"scoreboards"+ChatColor.GRAY+"...");
						for(Entry<Player, String> all : ScoreboardPlayer.players.entrySet())
							ScoreboardPlayer.removeScoreboard(all.getKey(), true);
						ScoreboardManager.unregisterAllScoreboards();
						ScoreboardManager.registerAllScoreboards();
						ScoreboardPlayer.players.clear();
						Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
							@Override
							public void run() {
								for(Player all : Bukkit.getOnlinePlayers())
									ScoreboardPlayer.setScoreboard(all);		
							}
						}, 5);
					}
					if(Main.pl.getConfig().getBoolean("tablist.text")) {
						s.sendMessage(Main.pr+ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"tablist"+ChatColor.GRAY+"...");
						TabManager.unregister();
						TabManager.register();
						for(Player all : Bukkit.getOnlinePlayers()) {
							for(int line : TabManager.headers.keySet())
								TabManager.setHeader(all, line, TabManager.headers.get(line).get(0));
							for(int line : TabManager.footers.keySet())
								TabManager.setFooter(all, line, TabManager.footers.get(line).get(0));
						}
					}
					s.sendMessage(Main.pr+ChatColor.GREEN+"Plugin reloaded!");
				}else {
					s.sendMessage(Main.pr+ChatColor.RED+"The following permission is required to execute this command: "+ChatColor.GRAY+"scoreboard.reload");
				}
			}else if(args[0].equalsIgnoreCase("update")) {
				if(!(s instanceof Player) || (s instanceof Player && ((Player) s).hasPermission("scoreboard.update"))) {
					s.sendMessage(Main.pr+ChatColor.GREEN+"Downloading the newest version...");
					if(Updater.downloadFile()) {
						s.sendMessage(Main.pr+ChatColor.GREEN+"Download finished! To apply the new update, you have to restart your server.");
					}else {
						s.sendMessage(Main.pr+ChatColor.RED+"Download failed! Please try it later again. More infos are in the console.");
					}
				}else {
					s.sendMessage(Main.pr+ChatColor.RED+"The following permission is required to execute this command: "+ChatColor.GRAY+"scoreboard.update");
				}
			}else if(args[0].equalsIgnoreCase("debug")) {
				if(!(s instanceof Player) || (s instanceof Player && ((Player) s).hasPermission("scoreboard.debug"))) {
					if(Main.debug) {
						Main.debug = false;
						s.sendMessage(Main.pr+ChatColor.RED+"Disabled debug.");
					}else {
						Main.debug = true;
						s.sendMessage(Main.pr+ChatColor.GREEN+"Enabled debug.");
					}
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
		s.sendMessage(designLine);
		s.sendMessage(Main.pr+ChatColor.RED+"/pb info "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Shows all infos about the plugin.");
		s.sendMessage(Main.pr+ChatColor.RED+"/pb reload "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Reload all configs.");
		s.sendMessage(Main.pr+ChatColor.RED+"/pb update "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Download the newest version.");
		s.sendMessage(Main.pr+ChatColor.RED+"/pb debug "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Toggle the debug.");
		s.sendMessage(designLine);
	}
}
