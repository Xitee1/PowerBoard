package de.xite.scoreboard.modules.board;

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
				
				if(Main.getBukkitVersion().compareTo(new Version("1.13")) == 1 || Main.getBukkitVersion().equals(new Version("1.13"))) { //only for version 1.13+
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
			ScoreTitleUtils.setTitle(p, board, sm.getCurrentTitle(), true, sm);// Get the current title and set it
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
	}
	public static void updateScoreboard(Player p) {
		/* Config syntax: 
		conditions:
		  - world:world AND permission:some.permission
		  - world:world AND permission:some.other.permission
		  - world:world AND gamemode:creative
		  - world:world_nether
		*/
		if(!Main.players.containsKey(p))
			return;
		String newScoreboard = Main.players.get(p);
		
		// Check if update is required
		if(!Main.players.get(p).equals(newScoreboard)) {
			// Update player's scoreboard
			removeScoreboard(p, false);
			ScoreboardManager sm = ScoreboardManager.get(newScoreboard);
			if(sm == null) {
				pl.getLogger().severe("Could not set scoreboard '"+newScoreboard+"'! File does not exists!");
				return;
			}
			ScoreTitleUtils.setTitle(p, p.getScoreboard(), sm.getCurrentTitle(), true, sm);// Get the current title and set it
			ScoreTitleUtils.setScores(p, p.getScoreboard(), sm.getCurrentScores(), true, sm);
		}
	}
	public static void removeScoreboard(Player p, boolean removeTeams) {
		if(!Main.players.containsKey(p))
			return;
		ScoreboardManager.get(Main.players.get(p)).removePlayer(p);
		Main.players.remove(p);
		
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
