package de.xite.scoreboard.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import de.xite.scoreboard.modules.board.ScoreboardPlayer;

public class ScoreboardConditionListener implements Listener {
	
	@EventHandler
	public void GameModeSwitchEvent(PlayerGameModeChangeEvent e) {
		if(ScoreboardPlayer.scoreboards.size() > 1)
			ScoreboardPlayer.updateScoreboard(e.getPlayer());
	}
	
	@EventHandler
	public void WorldSwitchEvent(PlayerChangedWorldEvent e) {
		if(ScoreboardPlayer.scoreboards.size() > 1)
			ScoreboardPlayer.updateScoreboard(e.getPlayer());
	}
}
