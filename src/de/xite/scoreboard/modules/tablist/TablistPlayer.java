package de.xite.scoreboard.modules.tablist;

import java.util.HashMap;

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
		if(players.containsKey(p)) {
			TablistManager.get(players.get(p)).removePlayer(p, sendBlankTablist);
			players.remove(p);
		}
	}
	
	public static TablistManager getMatchingTablist(Player p) {
		// ConditionListener.checkConditions will be used here once multiple tablists are implemented
		return null;
	}
}
