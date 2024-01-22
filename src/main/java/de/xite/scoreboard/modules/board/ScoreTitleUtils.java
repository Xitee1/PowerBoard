package de.xite.scoreboard.modules.board;

import java.util.ArrayList;

import de.xite.scoreboard.utils.Version;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Placeholders;

public class ScoreTitleUtils {
	// ---- Set the scoreboard title ---- //
	public static boolean setTitle(Player p, String title, boolean usePlaceholders, ScoreboardManager sm) {
		Scoreboard board = p.getScoreboard();
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		if(obj == null)
			return false;
		if(usePlaceholders)
			title = Placeholders.replace(p, title);
		
		//try {
		obj.setDisplayName(title);
		/*}catch (IllegalArgumentException e) {
			if(PowerBoard.aboveMC_1_13) { // In version 1.13 you can use up to 128 chars in the title
				obj.setDisplayName(ChatColor.RED+"Error: too long - see console");
				PowerBoard.pl.getLogger().warning(" ");
				PowerBoard.pl.getLogger().warning("-> The scoreboard title is too long! The limit is 128 chars!");
				if(sm != null)
					PowerBoard.pl.getLogger().warning("-> Scoreboard: "+sm.getName());
				PowerBoard.pl.getLogger().warning("-> Title: "+title);
				PowerBoard.pl.getLogger().warning("-> Player: "+p.getName());
				PowerBoard.pl.getLogger().warning(" ");
			}else {
				obj.setDisplayName(ChatColor.RED+"| too long |");
				PowerBoard.pl.getLogger().warning(" ");
				PowerBoard.pl.getLogger().warning("-> The scoreboard title is too long! The limit is 16 chars!");
				if(sm != null)
					PowerBoard.pl.getLogger().warning("-> Scoreboard: "+sm.getName());
				PowerBoard.pl.getLogger().warning("-> Title: "+title);
				PowerBoard.pl.getLogger().warning("-> Player: "+p.getName());
				PowerBoard.pl.getLogger().warning(" ");
			}
		}*/
		
		//if(sm != null)
			//sm.addPlayer(p);
		return true;
	}
	// ---- Set scores ---- //
	public static boolean setScores(Player p, ArrayList<String> scores, boolean usePlaceholders, ScoreboardManager sm) {
		Scoreboard board = p.getScoreboard();
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		if(obj == null)
			return false;

		PowerBoard.pl.getLogger().info("-- Settings scores for player "+p.getName()+" (sb: "+sm.getName()+"): --");
		for(int i = 0; i < scores.size(); i++) {
			int id = scores.size()-i-1;
			PowerBoard.pl.getLogger().info(id+": "+scores.get(id));
			setScore(p, scores.get(id), i, usePlaceholders, sm);
		}
		PowerBoard.pl.getLogger().info("--     --");
		return true;
	}
	public static boolean setScore(Player p, String score, int scoreId, boolean usePlaceholders, ScoreboardManager sm) {
		Scoreboard board = p.getScoreboard();
		Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
		if(obj == null)
			return false;
		String colorcode = "§"+scoreId;
		if(scoreId > 9) {
			if(scoreId == 10)
				colorcode = "§a";
			if(scoreId == 11)
				colorcode = "§b";
			if(scoreId == 12)
				colorcode = "§c";
			if(scoreId == 13)
				colorcode = "§d";
			if(scoreId == 14)
				colorcode = "§e";
		}
		// If the scoreboard switches too fast (especially blacklisted) sometimes there will this error in the console: IllegalStateException: Unregistered scoreboard component
		// We can just ignore it because it seems like it has no effect on functionality.
		try {
			Team team = board.getTeam(PowerBoard.scoreTeamPrefix+scoreId);
			if(team == null) {
				team = board.registerNewTeam(PowerBoard.scoreTeamPrefix+scoreId);
				team.addEntry(colorcode);
				obj.getScore(colorcode).setScore(scoreId);
			}
			if(score.isEmpty()) // If length == 0 set to " " for free space in scoreboard
				score = " ";
			if(!score.equals(" ") && usePlaceholders)
				score = Placeholders.replace(p, score);
			
			// ---- Set all scores ---- //
			int limit = 16;
			if(Version.isAbove_1_13()) // In version 1.13 you can use up to 64 chars in prefix and suffix
				limit = 64;

			String[] s = getScorePrefixSuffix(score, limit);
			try {
				team.setPrefix(s[0]);
				team.setSuffix(s[1]);
			}catch (IllegalArgumentException e) {
				team.setPrefix(ChatColor.RED+"-too long-");

				PowerBoard.pl.getLogger().warning(" ");
				PowerBoard.pl.getLogger().warning("-> The scoreboard-score is too long! The limit is around "+limit*2+" chars!");
				if(sm != null)
					PowerBoard.pl.getLogger().warning("-> Scoreboard: "+sm.getName());
				PowerBoard.pl.getLogger().warning("-> Score: "+score);
				PowerBoard.pl.getLogger().warning("-> Player: "+p.getName());
				PowerBoard.pl.getLogger().warning(" ");
			}
		}catch (IllegalStateException e) { }
		//if(sm != null)
			//sm.addPlayer(p);
		return true;
	}
	public static String[] getScorePrefixSuffix(String score, int split) {
		String[] s = new String[2];
		
		if(score.length() > split) { // Check if suffix is needed
			s[0] = score.substring(0, split); // Set the prefix
			s[1] = ChatColor.getLastColors(s[0])+score.substring(split); // Get last color + everything in the string after the split
		}else {
			s[0] = score; // Set prefix
			s[1] = "";
		}
			
		return s;
	}
}
