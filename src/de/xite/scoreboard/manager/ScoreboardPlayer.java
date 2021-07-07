package de.xite.scoreboard.manager;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.xite.scoreboard.api.ScoreboardAPI;
import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.utils.Placeholders;
import de.xite.scoreboard.utils.Teams;

public class ScoreboardPlayer {
	static Main pl = Main.pl;
	
	@SuppressWarnings("deprecation")
	public static void setScoreboard(Player p, String name) {
		if(!pl.getConfig().getBoolean("scoreboard") && !pl.getConfig().getBoolean("tablist.ranks"))
			return;
		Scoreboard board = p.getScoreboard();

		// ---- Ranks ---- //
		if(pl.getConfig().getBoolean("tablist.ranks")) {
			Teams teams = Teams.get(p);
			if(teams == null)
				PrefixManager.register(p);
		}
			
		// ---- Scoreboard ---- //
		if(pl.getConfig().getBoolean("tablist.ranks") || pl.getConfig().getBoolean("scoreboard") || ScoreboardAPI.useAPI) {
			Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
			if(obj == null) {
				board = Bukkit.getScoreboardManager().getNewScoreboard();
				
				if(Main.getBukkitVersion() >= 113) { //only for version 1.13+
					obj = board.registerNewObjective("aaa", "bbb", "SBPlugin");
				}else
					obj = board.registerNewObjective("aaa", "bbb");
			}
				
				
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		if(pl.getConfig().getBoolean("scoreboard") && !ScoreboardAPI.useAPI) { // Check if the scoreboard is enabled
			ScoreboardManager sm = ScoreboardManager.get(name);
			
			setTitle(p, board, sm.getCurrentTitle(), true, sm);// Get the current title and set it
			
			ArrayList<String> scores = sm.getCurrentScore();
			for(int i = 0; i < scores.size(); i++) {
				int id = scores.size()-i-1;
				
				setScore(p, board, scores.get(id), i, true, sm);
			}
			sm.addPlayer(p);
			if(Main.debug) // Send debug message if enabled
				Main.pl.getLogger().info("Scores amount for "+sm.getName()+": "+sm.getCurrentScore().size());
		}
		// ---- Set the scoreboard ---- //
		p.setScoreboard(board);
		
		// Debug
		if(Main.debug)
			Main.pl.getLogger().info("Set scoreboard for player "+p.getName());
		
		// ---- Ranks ---- //
		if(pl.getConfig().getBoolean("tablist.ranks"))
			PrefixManager.registerTeams(p, board);
	}
	
	public static void removeScoreboard(Player p, boolean removeTeams) {
		if(!Main.players.containsKey(p))
			return;
		Main.players.remove(p);
		if(removeTeams) {
			for(Team t : p.getScoreboard().getTeams())
				t.unregister();
			Objective obj = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
			if(obj != null)
				obj.unregister();
			Teams.removePlayer(p);
		}else {
			Objective obj = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
			if(obj != null)
				obj.unregister();
		}
		if(Main.debug)
			pl.getLogger().info("Removed "+p.getName()+"'s scoreboard");
	}
	
	// ---- Set the scoreboard title ---- //
	public static boolean setTitle(Player p, Scoreboard board, String title, boolean usePlaceholders, ScoreboardManager sm) {
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		if(obj == null)
			return false;
		if(usePlaceholders)
			title = Placeholders.replace(p, title);
		if(Main.getBukkitVersion() < 113) {// In version 1.13+ you can use more than 16 chars
			if(title.length() <= 16) {
				obj.setDisplayName(title);
			}else {
				obj.setDisplayName(ChatColor.RED+"-too long-");
				Main.pl.getLogger().warning(" ");
				Main.pl.getLogger().warning("-> The scoreboard title is too long! The limit is 16 chars!");
				Main.pl.getLogger().warning("-> Scoreboard: "+sm.getName());
				Main.pl.getLogger().warning("-> Title: "+Main.pl.getConfig().getString("scoreboard.name"));
				Main.pl.getLogger().warning("-> Player: "+p.getName());
				Main.pl.getLogger().warning(" ");
			}
		}else {
			if(title.length() <= 64) {
				obj.setDisplayName(title);
			}else {
				obj.setDisplayName(ChatColor.RED+"-too long-");
				Main.pl.getLogger().warning(" ");
				Main.pl.getLogger().warning("-> The scoreboard title is too long! The limit is 64 chars!");
				Main.pl.getLogger().warning("-> Scoreboard: "+sm.getName());
				Main.pl.getLogger().warning("-> Title: "+Main.pl.getConfig().getString("scoreboard.name"));
				Main.pl.getLogger().warning("-> Player: "+p.getName());
				Main.pl.getLogger().warning(" ");
			}
		}
		return true;
	}
	// ---- Set scores ---- //
	public static boolean setScore(Player p, Scoreboard board, String score, int ScoreID, boolean usePlaceholders, ScoreboardManager sm) {
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		if(obj == null)
			return false;
		String colorcode = "§"+ScoreID;
		if(ScoreID > 9) {
			if(ScoreID == 10)
				colorcode = "§a";
			if(ScoreID == 11)
				colorcode = "§b";
			if(ScoreID == 12)
				colorcode = "§c";
			if(ScoreID == 13)
				colorcode = "§d";
			if(ScoreID == 14)
				colorcode = "§e";
		}
		
		Team team = board.getTeam("score-"+ScoreID);
		if(team == null) {
			team = board.registerNewTeam("score-"+ScoreID);	
			if(Main.debug)
				Main.pl.getLogger().info("Added Team: score-"+ScoreID);
			team.addEntry(colorcode);
			obj.getScore(colorcode).setScore(ScoreID);
		}
		
		if(score.length() == 0) // If lenght == 0 set to " " for free space in scoreboard
			score = " ";
		if(!score.equals(" ") && usePlaceholders)
			score = Placeholders.replace(p, score);
		
		// ---- Set all scores ---- //
		if(Main.getBukkitVersion() < 113) {//Under version 1.13+ you can just use up to 16 chars.
			// Set the score for 1.12-
			String[] s = getScorePrefixSuffix(score, 16, 30);
			if(s == null) {
				team.setPrefix(ChatColor.RED+"-too long-");
				Main.pl.getLogger().warning(" ");
				Main.pl.getLogger().warning("-> The scoreboard-score is too long! The limit is 30 chars!");
				Main.pl.getLogger().warning("-> Scoreboard: "+sm.getName());
				Main.pl.getLogger().warning("-> Score: \""+score+"\", chars: "+score.length());
				Main.pl.getLogger().warning("-> Player: "+p.getName());
				Main.pl.getLogger().warning(" ");
			}else {
				team.setPrefix(s[0]);
				team.setSuffix(s[1]);
			}
		}else {
			// Set the score for 1.13+
			String[] s = getScorePrefixSuffix(score, 64, 126);
			if(s == null) {
				team.setPrefix(ChatColor.RED+"-too long-");
				Main.pl.getLogger().warning(" ");
				Main.pl.getLogger().warning("-> The scoreboard-score is too long! The limit is 126 chars!");
				Main.pl.getLogger().warning("-> Scoreboard: "+sm.getName());
				Main.pl.getLogger().warning("-> Score: \""+score+"\", chars: "+score.length());
				Main.pl.getLogger().warning("-> Player: "+p.getName());
				Main.pl.getLogger().warning(" ");
			}else {
				team.setPrefix(s[0]);
				team.setSuffix(s[1]);
			}
		}
		return true;
	}
	public static String[] getScorePrefixSuffix(String score, int limit, int maxchars) {
		String[] s = new String[2];
		s[1] = "";
		
		if(score.length() > maxchars)
			return null;
		
		if(score.length() <= maxchars && score.length() > limit) { // Check if suffix needed
			String prefix = score.substring(0, limit);
			s[0] = prefix; // Set the prefix
			
			
			String lastColor = ChatColor.getLastColors(prefix); // Try to get the last color from prefix
			if(lastColor.length() == 0)
				lastColor = "§f";
			
			s[1] = lastColor+score.substring(limit);// Get last color + everything in the string after 16 chars
		}else
			s[0] = score; // Set prefix
		return s;
	}
	
	public static void updateScoreboard(Player p) {
		/* Config syntax: 
		conditions:
		  - world:world AND permission:some.permission
		  - world:world AND permission:some.other.permission
		  - world:world AND gamemode:creative
		  - world:masterworld
		*/
		String newScoreboard = Main.players.get(p);
		
		
		if(!Main.players.get(p).equals(newScoreboard)) {
			ScoreboardManager.get(Main.players.get(p)).removePlayer(p);
			ScoreboardManager.get(newScoreboard).addPlayer(p);
			Main.players.replace(p, newScoreboard);
			removeScoreboard(p, false);
			setScoreboard(p, newScoreboard);
		}
	}
}
