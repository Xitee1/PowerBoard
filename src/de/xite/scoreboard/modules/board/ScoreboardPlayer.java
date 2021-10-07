package de.xite.scoreboard.modules.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.modules.ranks.PrefixManager;
import de.xite.scoreboard.utils.Teams;
import de.xite.scoreboard.utils.Version;

public class ScoreboardPlayer {
	static Main pl = Main.pl;
	
	// All registered scoreboards
	public static HashMap<String, ScoreboardManager> scoreboards = new HashMap<>();
	
	// All players with scoreboards
	public static HashMap<Player, String> players = new HashMap<>(); // Player; Scoreboard config file name
	
	@SuppressWarnings("deprecation")
	public static void setScoreboard(Player p, String name) {
		Scoreboard board = p.getScoreboard();

		// ---- Ranks ---- //
		if(pl.getConfig().getBoolean("tablist.ranks")) {
			Teams teams = Teams.get(p);
			if(teams == null)
				PrefixManager.register(p);
		}
		
		// ---- Scoreboard ---- //
		if(name == null || pl.getConfig().getBoolean("tablist.ranks") || pl.getConfig().getBoolean("scoreboard")) { // Set obj if name == null, ranks, scoreboard
			removeScoreboard(p, false);
			Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
			if(obj == null) {
				board = Bukkit.getScoreboardManager().getNewScoreboard();
				
				if(Main.getBukkitVersion().compareTo(new Version("1.13")) == 1 || Main.getBukkitVersion().equals(new Version("1.13"))) { // only for version 1.13+
					obj = board.registerNewObjective("aaa", "bbb", "SBPlugin");
				}else
					obj = board.registerNewObjective("aaa", "bbb");
			}
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		if(name != null && pl.getConfig().getBoolean("scoreboard")) { // Check if the scoreboard is enabled
			ScoreboardManager sm = ScoreboardManager.get(name);
			if(sm == null) {
				pl.getLogger().severe("Could not set scoreboard '"+name+"'! File does not exists!");
				return;
			}
			ScoreTitleUtils.setTitle(p, board, sm.getCurrentTitle(), true, sm); // Get the current title and set it
			ScoreTitleUtils.setScores(p, board, sm.getCurrentScores(), true, sm);
		}
		
		// ---- Set the scoreboard ---- //
		p.setScoreboard(board);
		// Debug
		if(Main.debug)
			Main.pl.getLogger().info("Scoreboard set for player "+p.getName());
		
		// ---- Ranks ---- //
		if(pl.getConfig().getBoolean("tablist.ranks"))
			PrefixManager.setTeams(p, board);
		
		// Update if more scoreboards exists
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				if(ScoreboardPlayer.scoreboards.size() > 1)
					ScoreboardPlayer.updateScoreboard(p);
			}
		}, 20);
	}
	public static void updateScoreboard(Player p) {
		/* Config syntax: 
		conditions:
		  - world:world AND permission:some.permission
		  - world:world AND permission:some.other.permission
		  - world:world AND gamemode:creative
		  - world:world_nether
		*/
		if(!players.containsKey(p))
			return;

		ScoreboardManager newScoreboard = getMatchingScoreboard(p);
		if(newScoreboard == null)
			return;
		if(Main.debug)
			pl.getLogger().info("Changing "+p.getName()+"'s scoreboard to "+newScoreboard.getName());
		// Check if update is required
		if(!players.get(p).equals(newScoreboard.getName())) {
			// Remove old scoreboard
			ScoreboardManager.get(players.get(p)).removePlayer(p);
			players.remove(p);
			
			// Update player's scoreboard

			
			Scoreboard board = p.getScoreboard();
			
			newScoreboard.addPlayer(p);
			ScoreTitleUtils.setTitle(p, board, newScoreboard.getCurrentTitle(), true, newScoreboard); // Get the current title and set it
			ScoreTitleUtils.setScores(p, board, newScoreboard.getCurrentScores(), true, newScoreboard);
			
			p.setScoreboard(board);
		}
	}
	public static ScoreboardManager getMatchingScoreboard(Player p) {
		for(Entry<String, ScoreboardManager> e : scoreboards.entrySet()) {
			ScoreboardManager sm = e.getValue();
			if(sm == null) {
				pl.getLogger().severe("Could not set scoreboard '"+sm+"'! File does not exists!");
				return null;
			}
			for(String condition : sm.conditions) { // For all "OR" conditions (lines)
				ArrayList<String> andConditions = new ArrayList<>();
				if(condition.contains(" AND ")) {
					for(String s : condition.split("AND"))
						andConditions.add(s);
				}else
					andConditions.add(condition);
				
				boolean match = true;
				for(String s : andConditions) {
					if(s.startsWith("world:")) {
						String value = s.split("world:")[1];
						if(!(p.getLocation().getWorld().getName().equals(value)))
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
					return sm;
			}
		}
		return null;
	}
	public static void removeScoreboard(Player p, boolean removeTeams) {
		if(!players.containsKey(p))
			return;
		ScoreboardManager.get(players.get(p)).removePlayer(p);
		players.remove(p);
		
		if(removeTeams) {
			for(Team t : p.getScoreboard().getTeams())
				t.unregister();
			Teams.removePlayer(p);
		}
		Objective obj = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		if(obj != null)
			obj.unregister();
		
		if(Main.debug)
			pl.getLogger().info("Removed "+p.getName()+"'s scoreboard");
	}
}
