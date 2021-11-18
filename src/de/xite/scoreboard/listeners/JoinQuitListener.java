package de.xite.scoreboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.modules.tablist.TabManager;
import de.xite.scoreboard.modules.tablist.Tabpackage;
import de.xite.scoreboard.utils.Updater;

public class JoinQuitListener implements Listener {
	PowerBoard pl = PowerBoard.pl;
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setDisplayName(p.getName());
		if(p.hasPermission("scoreboard.update") || p.isOp()) {
			if(Updater.checkVersion()) {
				if(pl.getConfig().getBoolean("update.notification")) {
					p.sendMessage(PowerBoard.pr+ChatColor.RED+"A new update is available ("+ChatColor.AQUA+"v"+Updater.version+ChatColor.RED+")! Your version: "+ChatColor.AQUA+pl.getDescription().getVersion());
					if(pl.getConfig().getBoolean("update.autoupdater")) {
						p.sendMessage(PowerBoard.pr+ChatColor.GREEN+"The plugin will be updated automatically after a server restart.");
					}else {
						p.sendMessage(PowerBoard.pr+ChatColor.RED+"The auto-updater is disabled in your config.yml. Type /sb update or enable the auto-updater.");
					}
				}
			}
		}
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() { // Wait 0.25 seconds; Set the scoreboard if enabled
			@Override
			public void run() {
				if(pl.getConfig().getBoolean("scoreboard") || pl.getConfig().getBoolean("tablist.ranks")) {
					ScoreboardPlayer.setScoreboard(p);
				}
				if(pl.getConfig().getBoolean("tablist.text")) { // Set the Scoreboard text if enabled
					for(int line : TabManager.headers.keySet())
						TabManager.setHeader(p, line, TabManager.headers.get(line).get(0));
					for(int line : TabManager.footers.keySet())
						TabManager.setFooter(p, line, TabManager.footers.get(line).get(0));
					Tabpackage.send(p);
				}
			}
		}, 5);
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(TabManager.currentHeader.containsKey(p))
			TabManager.currentHeader.remove(p);
		if(TabManager.currentFooter.containsKey(p))
			TabManager.currentFooter.remove(p);
		ScoreboardPlayer.removeScoreboard(p, true);
	}
}
