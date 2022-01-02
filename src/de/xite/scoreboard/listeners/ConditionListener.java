package de.xite.scoreboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.board.ScoreboardManager;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;

public class ConditionListener implements Listener {
	
	@EventHandler
	public void GameModeSwitchEvent(PlayerGameModeChangeEvent e) {
		if(ScoreboardManager.scoreboards.size() > 1)
			Bukkit.getScheduler().runTaskLater(PowerBoard.pl, new Runnable() {
				@Override
				public void run() {
					ScoreboardPlayer.updateScoreboard(e.getPlayer());
				}
			}, 5);
	}
	
	@EventHandler
	public void WorldSwitchEvent(PlayerChangedWorldEvent e) {
		Bukkit.getScheduler().runTaskLater(PowerBoard.pl, new Runnable() {
			@Override
			public void run() {
				if(ScoreboardManager.scoreboards.size() > 1) { // Only if there are more than 1 scoreboards
					ScoreboardPlayer.updateScoreboard(e.getPlayer());
				}
				
			}
		}, 5);
	}
}
