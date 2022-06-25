package de.xite.scoreboard.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
	
	
	public static boolean checkConditions(Player p, List<String> conditions) {
		for(String condition : conditions) { // For all "OR" conditions (lines)
			ArrayList<String> andConditions = new ArrayList<>();
			if(condition.contains(" AND ")) {
				for(String s : condition.split(" AND "))
					andConditions.add(s);
			}else
				andConditions.add(condition);
			
			boolean match = true;
			for(String s : andConditions) {
				if(s.startsWith("world:")) {
					String value = s.split("world:")[1];
					if(!(p.getLocation().getWorld().getName().equalsIgnoreCase(value)))
						match = false;
				}
				if(s.startsWith("permission:")) {
					String value = s.split("permission:")[1];
					if(!(p.hasPermission(value)))
						match = false;
				}
				if(s.startsWith("gamemode:")) {
					String value = s.split("gamemode:")[1];
					if(!(p.getGameMode().name().equalsIgnoreCase(value)))
						match = false;
				}
			}
			
			if(match == true)
				return true;
		}
		return false;
	}
}