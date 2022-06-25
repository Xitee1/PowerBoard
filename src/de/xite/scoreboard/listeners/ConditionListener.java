package de.xite.scoreboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;

public class ConditionListener implements Listener {
	
	@EventHandler
	public void GameModeSwitchEvent(PlayerGameModeChangeEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(PowerBoard.pl, new Runnable() {
			@Override
			public void run() {
				ScoreboardPlayer.updateScoreboard(e.getPlayer());
			}
		});
	}
	
	@EventHandler
	public void WorldSwitchEvent(PlayerChangedWorldEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(PowerBoard.pl, new Runnable() {
			@Override
			public void run() {
				ScoreboardPlayer.updateScoreboard(e.getPlayer());
			}
		});
	}
}
