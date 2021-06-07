package de.xite.scoreboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.xite.scoreboard.files.TabConfig;
import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.manager.ScoreboardPlayer;
import de.xite.scoreboard.manager.Tabpackage;
import de.xite.scoreboard.utils.Updater;

public class EventListener implements Listener {
	Main pl = Main.pl;
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.hasPermission("scoreboard.update") || p.isOp()) {
			if(Updater.checkVersion()) {
				if(pl.getConfig().getBoolean("update.notification")) {
					p.sendMessage(Main.pr+ChatColor.RED+"A new version is available (§bv"+Updater.version+ChatColor.RED+")! Your version: §bv"+pl.getDescription().getVersion());
					if(pl.getConfig().getBoolean("update.autoupdater")) {
						p.sendMessage(Main.pr+ChatColor.GREEN+"The plugin will be updated automatically after a server restart.");
					}else {
						p.sendMessage(Main.pr+ChatColor.RED+"The auto-updater is disabled in your config.yml. Type §6/sb update §cto update to the newest version.");
					}
				}
				pl.getLogger().info("-> A new version (v."+Updater.version+") is available! Your version: "+pl.getDescription().getVersion());
				pl.getLogger().info("-> Update me! :)");
			}
		}
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() { // Wait 0.25 seconds; Set the scoreboard if enabled
			@Override
			public void run() {
				ScoreboardPlayer.setScoreboard(p);
				if(pl.getConfig().getBoolean("tablist.text")) {// Set the Scoreboard text if enabled
					for(int line : TabConfig.headers.keySet()) {
						TabConfig.setHeader(p, line, TabConfig.headers.get(line).get(0));
					}
					for(int line : TabConfig.footers.keySet()) {
						TabConfig.setFooter(p, line, TabConfig.footers.get(line).get(0));
					}
					Tabpackage.send(p);
				}
			}
		}, 5);

	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(TabConfig.currentHeader.containsKey(p))
			TabConfig.currentHeader.remove(p);
		if(TabConfig.currentFooter.containsKey(p))
			TabConfig.currentFooter.remove(p);
		ScoreboardPlayer.removeScoreboard(p, true);
	}
}
