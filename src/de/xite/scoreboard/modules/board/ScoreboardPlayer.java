package de.xite.scoreboard.modules.board;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.xite.scoreboard.listeners.ConditionListener;
import de.xite.scoreboard.main.Config;
import de.xite.scoreboard.main.PowerBoard;

public class ScoreboardPlayer {
	static PowerBoard pl = PowerBoard.pl;
	
	// All players with scoreboards
	public static HashMap<Player, String> players = new HashMap<>(); // Player; Scoreboard config file name
	
	@SuppressWarnings("deprecation")
	public static void setScoreboard(Player p, boolean API, ScoreboardManager sm) {
		Scoreboard board = p.getScoreboard();
		
		// ---- Scoreboard ---- //
		if(ConditionListener.checkConditions(p, Config.scoreboardBlacklistConditions)) {
			removeScoreboard(p, false);
			if(!API)
				players.put(p, "blacklisted");
			if(PowerBoard.debug)
				pl.getLogger().info("Removed "+p.getName()+"'s scoreboard because blacklist-conditions match.");
			return;
		}
		if(!API) {
			if(sm == null)
				sm = getMatchingScoreboard(p);
			if(sm == null)
				return;
			if(players.containsKey(p))
				if(Objects.equals(players.get(p), sm.getName())) {
					if(PowerBoard.debug)
						pl.getLogger().info("Did not set/update "+p.getName()+"'s scoreboard because he already have the same scoreboard (Current: "+players.get(p)+"; New: "+sm.getName()+").");
					return;
				}else {
					if(PowerBoard.debug)
						pl.getLogger().info("Changing "+p.getName()+"'s scoreboard to "+sm.getName());
					removeScoreboard(p, true);
				}
		}else {
			removeScoreboard(p, false);
		}
		
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		if(obj == null) {
			if(PowerBoard.aboveMC_1_13) {
				obj = board.registerNewObjective("aaa", "bbb", "PowerBoard");
			}else
				obj = board.registerNewObjective("aaa", "bbb");
		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		//p.setScoreboard(board); // Set the scoreboard
		
		// Set the scores if the API isn't used
		if(!API) {
			if(players.containsKey(p)) {
				if(players.get(p).equals("blacklisted")) {
					players.remove(p);
				}else
					ScoreboardManager.get(players.get(p)).removePlayer(p);
			}
			sm.addPlayer(p);
			ScoreTitleUtils.setTitle(p, sm.getCurrentTitle(), true, sm);
			ScoreTitleUtils.setScores(p, sm.getCurrentScores(), true, sm);
		}
		
		
		// Debug
		if(PowerBoard.debug) {
			if(sm == null) {
				PowerBoard.pl.getLogger().info("Custom scoreboard set for player "+p.getName());
			}else {
				PowerBoard.pl.getLogger().info("Scoreboard '"+sm.getName()+"' set for player "+p.getName());
			}
		}
			
	}
	public static void updateScoreboard(Player p) {
		if(!players.containsKey(p))
			return;
		setScoreboard(p, false, null);
	}
	public static ScoreboardManager getMatchingScoreboard(Player p) {
		/* Config syntax: 
		conditions:
		  - world:world AND permission:some.permission
		  - world:world AND permission:some.other.permission
		  - world:world AND gamemode:creative
		  - world:world_nether
		*/
		for(Entry<String, ScoreboardManager> e : ScoreboardManager.scoreboards.entrySet()) {
			ScoreboardManager sm = e.getValue();
			if(sm == null) {
				pl.getLogger().severe("There was a error loading a scoreboard. Please check your configs.");
				return null;
			}
			if(sm.conditions == null) {
				pl.getLogger().severe("Could not get scoreboard '"+sm.getName()+"'! Probably a config error.");
				return null;
			}
			if(ConditionListener.checkConditions(p, sm.conditions))
				return sm;
		}
		return ScoreboardManager.get(pl.getConfig().getString("scoreboard-default"));
	}

	public static void removeScoreboard(Player p, boolean removeFromSBManager) {
		if(removeFromSBManager) {
			if(players.containsKey(p) && !players.get(p).equals("blacklisted"))
				ScoreboardManager.get(players.get(p)).removePlayer(p);
		}
		
		for(Team t : p.getScoreboard().getTeams()) {
			if(t.getName().startsWith("score-"))
				t.unregister();
		}
		
		Objective obj = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		if(obj != null)
			obj.unregister();
		if(PowerBoard.debug)
			pl.getLogger().info("Removed "+p.getName()+"'s (old) scoreboard");
	}
}
