package de.xite.scoreboard.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.modules.ranks.RankManager;
import de.xite.scoreboard.utils.Teams;

public class ConditionListener implements Listener {
	
	@EventHandler
	public void GameModeSwitchEvent(PlayerGameModeChangeEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(PowerBoard.pl, () -> ScoreboardPlayer.updateScoreboard(e.getPlayer()));
	}
	
	@EventHandler
	public void WorldSwitchEvent(PlayerChangedWorldEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(PowerBoard.pl, () -> {
			Player p = e.getPlayer();
			ScoreboardPlayer.updateScoreboard(p);
			if(PowerBoard.pl.getConfig().getBoolean("tablist.ranks") || PowerBoard.pl.getConfig().getBoolean("chat.ranks")) {
				Teams team = Teams.get(p);
				if(team != null)
					if(team.getRawPrefix().contains("%player_world%") || team.getRawSuffix().contains("%player_world%"))
						RankManager.updateTablistRanks(p);
			}
		});
	}
	/*
	// Debug only!
	@EventHandler
	public void onRankEvent(TeamSetEvent e) {
		Player p = e.getPlayer();
		if(p.getName().equals("Xitecraft")) {
			e.setChatPrefix("Chat Prefix : ");
			e.setNameColor(ChatColor.BLUE);
			e.setPrefix(ChatColor.RED+"[OWNER]");
			e.setSuffix(ChatColor.AQUA+"[SUFFIX]");
			e.setRankDisplayName("Owner's Displayname");
			e.setPlayerListName("HEY");
			e.setWeight(999);
		}
	}
	// Debug only!
	*/
	public static boolean checkConditions(Player p, List<String> conditions) {
		for(String condition : conditions) { // For all "OR" conditions (lines)
			ArrayList<String> andConditions = new ArrayList<>();
			if(condition.contains(" AND ")) {
				andConditions.addAll(Arrays.asList(condition.split(" AND ")));
			}else
				andConditions.add(condition);
			
			boolean match = true;
			for(String s : andConditions) {
				if(s.startsWith("world:")) {
					String value = s.split("world:")[1];
					String world = p.getLocation().getWorld().getName();
					if(value.endsWith("*")) {
						// Ignore everything after the * to allow to check for all maps that begin with the value and ignore the rest of it
						if(!(world.startsWith(value.substring(0, value.length()-2))))
							match = false;
					}else
						if(!(world.equalsIgnoreCase(value)))
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
			
			if(match)
				return true;
		}
		return false;
	}
}