package de.xite.scoreboard.board;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.utils.Placeholders;
import de.xite.scoreboard.utils.Version;

public class ScoreTitleUtils {
	// ---- Set the scoreboard title ---- //
	public static boolean setTitle(Player p, Scoreboard board, String title, boolean usePlaceholders, ScoreboardManager sm) {
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		if(obj == null)
			return false;
		if(usePlaceholders)
			title = Placeholders.replace(p, title);
		if(Main.getBukkitVersion().compareTo(new Version("1.13")) == 1) {// In version 1.13+ you can use more than 16 chars
			if(title.length() <= 16) {
				obj.setDisplayName(title);
			}else {
				obj.setDisplayName(ChatColor.RED+"-too long-");
				Main.pl.getLogger().warning(" ");
				Main.pl.getLogger().warning("-> The scoreboard title is too long! The limit is 16 chars!");
				if(sm != null)
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
				if(sm != null)
					Main.pl.getLogger().warning("-> Scoreboard: "+sm.getName());
				Main.pl.getLogger().warning("-> Title: "+Main.pl.getConfig().getString("scoreboard.name"));
				Main.pl.getLogger().warning("-> Player: "+p.getName());
				Main.pl.getLogger().warning(" ");
			}
		}
		if(sm != null)
			sm.addPlayer(p);
		return true;
	}
	// ---- Set scores ---- //
	public static boolean setScores(Player p, Scoreboard board, ArrayList<String> scores, boolean usePlaceholders, ScoreboardManager sm) {
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		if(obj == null)
			return false;
		for(int i = 0; i < scores.size(); i++) {
			int id = scores.size()-i-1;
			setScore(p, board, scores.get(id), i, usePlaceholders, sm);
		}
		return true;
	}
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
		if(Main.getBukkitVersion().compareTo(new Version("1.13")) == 1) {//Under version 1.13+ you can just use up to 16 chars.
			// Set the score for 1.12-
			String[] s = getScorePrefixSuffix(score, 16, 30);
			if(s == null) {
				team.setPrefix(ChatColor.RED+"-too long-");
				Main.pl.getLogger().warning(" ");
				Main.pl.getLogger().warning("-> The scoreboard-score is too long! The limit is 30 chars!");
				if(sm != null)
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
				if(sm != null)
					Main.pl.getLogger().warning("-> Scoreboard: "+sm.getName());
				Main.pl.getLogger().warning("-> Score: \""+score+"\", chars: "+score.length());
				Main.pl.getLogger().warning("-> Player: "+p.getName());
				Main.pl.getLogger().warning(" ");
			}else {
				team.setPrefix(s[0]);
				team.setSuffix(s[1]);
			}
		}
		if(sm != null)
			sm.addPlayer(p);
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
				lastColor = ChatColor.WHITE+"";
			
			s[1] = lastColor+score.substring(limit);// Get last color + everything in the string after 16 chars
		}else
			s[0] = score; // Set prefix
		return s;
	}
}
