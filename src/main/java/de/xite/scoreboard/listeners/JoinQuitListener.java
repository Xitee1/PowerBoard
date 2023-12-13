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
	private static final PowerBoard instance = PowerBoard.getInstance();
	private static final Updater updater = PowerBoard.getUpdater();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.hasPermission("powerboard.update") || p.isOp()) {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				if(updater.isUpdateAvailable()) {
					if(updater.infoMessageEnabled()) {
						p.sendMessage(PowerBoard.pr+ChatColor.RED+"A new update is available ("+ChatColor.AQUA+"v"+updater.getLatestVersion()+ChatColor.RED+")! Installed version: "+ChatColor.AQUA+"v"+updater.getCurrentVersion());
						p.sendMessage(PowerBoard.pr+ChatColor.RED+"You can download the newest version here: https://www.spigotmc.org/resources/powerboard-scoreboard-tablist-prefix-chat-animated.73854/");
					}
				}
			});
		}
		// Set a new scoreboard for the player to prevent bugs
		if((instance.getConfig().getBoolean("tablist.ranks") || instance.getConfig().getBoolean("scoreboard"))
				&& !instance.getConfig().getBoolean("scoreboard-advanced-settings.use-existing-scoreboard")) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
			// Register Teams if chat ranks or tablist ranks are used
			if(instance.getConfig().getBoolean("chat.ranks") || instance.getConfig().getBoolean("tablist.ranks"))
				if(Teams.get(p) == null) {
					RankManager.register(p);
					if(instance.getConfig().getBoolean("tablist.ranks"))
						RankManager.setTablistRanks(p);
				}

			if(instance.getConfig().getBoolean("scoreboard"))
				ScoreboardPlayer.setScoreboard(p, false, null);

			if(instance.getConfig().getBoolean("tablist.text"))
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
