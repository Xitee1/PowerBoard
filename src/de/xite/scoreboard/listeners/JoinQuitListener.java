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
import de.xite.scoreboard.modules.ranks.RankManager;
import de.xite.scoreboard.modules.tablist.TabManager;
import de.xite.scoreboard.modules.tablist.Tabpackage;
import de.xite.scoreboard.utils.Teams;
import de.xite.scoreboard.utils.Updater;

public class JoinQuitListener implements Listener {
	PowerBoard pl = PowerBoard.pl;
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setDisplayName(p.getName());
		if(p.hasPermission("powerboard.update") || p.isOp()) {
			if(Updater.checkVersion()) {
				if(pl.getConfig().getBoolean("update.notification")) {
					p.sendMessage(PowerBoard.pr+ChatColor.RED+"A new update is available ("+ChatColor.AQUA+"v"+Updater.version+ChatColor.RED+")! Your version: "+ChatColor.AQUA+pl.getDescription().getVersion());
					if(pl.getConfig().getBoolean("update.autoupdater")) {
						p.sendMessage(PowerBoard.pr+ChatColor.GREEN+"The plugin will be updated automatically after a server restart.");
					}else {
						p.sendMessage(PowerBoard.pr+ChatColor.RED+"You can download the newest version here: https://www.spigotmc.org/resources/powerboard-scoreboard-tablist-prefix-chat-animated.73854/");
					}
				}
			}
		}
		p.sendMessage("hi");
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() { // Wait 0.25 seconds; Set the scoreboard if enabled
			@Override
			public void run() {
				// Register Teams if chat ranks or tablist ranks are used
				if(pl.getConfig().getBoolean("chat.ranks") || pl.getConfig().getBoolean("tablist.ranks")) {
					Teams teams = Teams.get(p);
					if(teams == null)
						RankManager.register(p);
				}
				if(pl.getConfig().getBoolean("tablist.ranks"))
					RankManager.setRanks(p);
				if(pl.getConfig().getBoolean("scoreboard"))
					ScoreboardPlayer.setScoreboard(p);
				
				// Set the tablist if enabled
				if(pl.getConfig().getBoolean("tablist.text")) {
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
		ScoreboardPlayer.removeScoreboard(p);
		Teams.removePlayer(p);
	}
}
