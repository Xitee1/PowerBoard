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
import de.xite.scoreboard.modules.tablist.TablistPlayer;
import de.xite.scoreboard.utils.Teams;
import de.xite.scoreboard.utils.Updater;

public class JoinQuitListener implements Listener {
	// TODO: 03/06/2023 Remove this
	PowerBoard pl = PowerBoard.pl;
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.hasPermission("powerboard.update") || p.isOp()) {
			Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
				if(Updater.checkVersion()) {
					if(pl.getConfig().getBoolean("update.notification")) {
						p.sendMessage(PowerBoard.pr+ChatColor.RED+"A new update is available ("+ChatColor.AQUA+"v"+Updater.getVersion()+ChatColor.RED+")! Installed version: "+ChatColor.AQUA+"v"+pl.getDescription().getVersion());
						p.sendMessage(PowerBoard.pr+ChatColor.RED+"You can download the newest version here: https://www.spigotmc.org/resources/powerboard-scoreboard-tablist-prefix-chat-animated.73854/");
					}
				}
			});
		}
		// Set a new scoreboard for the player to prevent bugs
		if((pl.getConfig().getBoolean("tablist.ranks") || pl.getConfig().getBoolean("scoreboard")) && !pl.getConfig().getBoolean("scoreboard-advanced-settings.use-existing-scoreboard")) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(pl, () -> {
			// Register Teams if chat ranks or tablist ranks are used
			if(pl.getConfig().getBoolean("chat.ranks") || pl.getConfig().getBoolean("tablist.ranks"))
				if(Teams.get(p) == null)
					RankManager.register(p);

			if(pl.getConfig().getBoolean("scoreboard"))
				ScoreboardPlayer.setScoreboard(p, false, null);

			if(pl.getConfig().getBoolean("tablist.text"))
				TablistPlayer.addPlayer(p, null);

		}, 3);
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		ScoreboardPlayer.removeScoreboard(p, true);
		TablistPlayer.removePlayer(p, false);
		Teams.removePlayer(p);
	}
}
