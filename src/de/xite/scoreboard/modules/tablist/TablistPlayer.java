package de.xite.scoreboard.modules.tablist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import de.leonhard.storage.shaded.jetbrains.annotations.Nullable;
import de.xite.scoreboard.main.PowerBoard;

public class TablistPlayer {
	static PowerBoard pl = PowerBoard.pl;
	
	public static HashMap<Player, String> players = new HashMap<>();
	
	public static void addPlayer(Player p, @Nullable TablistManager tabmanager) {
		if(tabmanager == null) {
			TablistManager tab = getMatchingTablist(p);
			if(tab == null)
				tab = TablistManager.get(pl.getConfig().getString("tablist.text-default"));
			removePlayer(p, false);
			addPlayer(p, tab);
		}else {
			tabmanager.addPlayer(p);
			players.put(p, tabmanager.getName());
		}
	}
	
	public static void removePlayer(Player p, boolean sendBlankTablist) {
		if(players.containsKey(p))
			TablistManager.get(players.get(p)).removePlayer(p, sendBlankTablist);
	}
	
	public static TablistManager getMatchingTablist(Player p) {
		/* Config syntax: 
		conditions:
		  - world:world AND permission:some.permission
		  - world:world AND permission:some.other.permission
		  - world:world AND gamemode:creative
		  - world:world_nether
		*/
		for(Entry<String, TablistManager> e : TablistManager.tablists.entrySet()) {
			TablistManager tm = e.getValue();
			if(tm == null) {
				pl.getLogger().severe("Could not set scoreboard '"+tm+"'! File does not exists!");
				return null;
			}
			for(String condition : tm.conditions) { // For all "OR" conditions (lines)
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
					return tm;
			}
		}
		return TablistManager.get(pl.getConfig().getString("tablist.text-default"));
	}
}
