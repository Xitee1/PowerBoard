 package de.xite.scoreboard.commands;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.Config;
import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.board.ScoreboardManager;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.modules.ranks.RankManager;
import de.xite.scoreboard.modules.tablist.TabManager;
import de.xite.scoreboard.utils.Teams;
import de.xite.scoreboard.utils.Updater;
import net.md_5.bungee.api.ChatColor;

public class ScoreboardCommand implements CommandExecutor{
	String designLine = PowerBoard.pr+ChatColor.GRAY+"X"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GOLD+"Scoreboard"+ChatColor.YELLOW+""+ChatColor.STRIKETHROUGH+"-----"+ChatColor.GRAY+"X";
	PowerBoard pl = PowerBoard.pl;
	
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
			s.sendMessage(designLine);
			s.sendMessage(PowerBoard.pr+ChatColor.YELLOW+"Your version: "+ChatColor.DARK_AQUA+"v"+PowerBoard.pl.getDescription().getVersion());
			s.sendMessage(PowerBoard.pr+ChatColor.YELLOW+"Newest version: "+ChatColor.DARK_AQUA+"v"+Updater.getVersion());
			s.sendMessage(PowerBoard.pr+ChatColor.YELLOW+"Author: "+ChatColor.DARK_AQUA+"Xitee");
			s.sendMessage(designLine);

		}else if(args.length == 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
			if(!(s instanceof Player) || (s instanceof Player && ((Player) s).hasPermission("powerboard.reload"))) {
				s.sendMessage(PowerBoard.pr+ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"config"+ChatColor.GRAY+"...");
				Config.loadConfig();
				if(PowerBoard.pl.getConfig().getBoolean("scoreboard")) {
					s.sendMessage(PowerBoard.pr+ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"scoreboards"+ChatColor.GRAY+"...");
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
				if(pl.getConfig().getBoolean("tablist.ranks")) {
					for(Player all : Bukkit.getOnlinePlayers()) {
						Teams.removePlayer(all);
						RankManager.register(all);
						RankManager.setTablistRanks(all);
					}
				}

				if(PowerBoard.pl.getConfig().getBoolean("tablist.text")) {
					s.sendMessage(PowerBoard.pr+ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"tablist"+ChatColor.GRAY+"...");
					TabManager.unregister();
					TabManager.register();
					for(Player all : Bukkit.getOnlinePlayers()) {
						for(int line : TabManager.headers.keySet())
							TabManager.setHeader(all, line, TabManager.headers.get(line).get(0));
						for(int line : TabManager.footers.keySet())
							TabManager.setFooter(all, line, TabManager.footers.get(line).get(0));
					}
				}
				s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Plugin reloaded!");
			}else {
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"The following permission is required to execute this command: "+ChatColor.GRAY+"powerboard.reload");
			}
		}else if(args.length >= 1 && args[0].equalsIgnoreCase("update")) {
			if(args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
				if(!(s instanceof Player) || (s instanceof Player && ((Player) s).hasPermission("powerboard.update"))) {
					s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Downloading the newest version...");
					if(Updater.downloadFile()) {
						s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Download finished! Stopping server..");
						Bukkit.spigot().restart();
					}else {
						s.sendMessage(PowerBoard.pr+ChatColor.RED+"Download failed! Please try it later again. More infos are available in the console.");
					}
				}else
					s.sendMessage(PowerBoard.pr+ChatColor.RED+"The following permission is required to execute this command: "+ChatColor.GRAY+"powerboard.update");
			}else
				s.sendMessage(PowerBoard.pr+ChatColor.RED+"Warning: If you update the plugin, the server will be automatically restarted (if you have a restart script) when the download is finished. "
						+ "Please type "+ChatColor.YELLOW+"/pb update confirm"+ChatColor.RED+" to update the plugin.");

		}else if(args.length == 1 && args[0].equalsIgnoreCase("debug")) {
			if(!(s instanceof Player) || (s instanceof Player && ((Player) s).hasPermission("powerboard.debug"))) {
				if(PowerBoard.debug) {
					PowerBoard.debug = false;
					s.sendMessage(PowerBoard.pr+ChatColor.RED+"Disabled debug.");
				}else {
					PowerBoard.debug = true;
					s.sendMessage(PowerBoard.pr+ChatColor.GREEN+"Enabled debug.");
				}
			}
		}else
			sendInfoPage(s);
		return true;
	}
	public void sendInfoPage(CommandSender s) {
		s.sendMessage(designLine);
		s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb info "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Shows all infos about the plugin.");
		s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb reload "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Reload all configs.");
		s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb update "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Download the newest version.");
		s.sendMessage(PowerBoard.pr+ChatColor.RED+"/pb debug "+ChatColor.DARK_GRAY+"- "+ChatColor.GRAY+"Toggle the debug.");
		s.sendMessage(designLine);
	}
}
