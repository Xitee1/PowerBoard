package de.xite.scoreboard.api;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.board.ScoreTitleUtils;
import de.xite.scoreboard.modules.board.ScoreboardManager;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.modules.ranks.RankManager;
import de.xite.scoreboard.utils.Placeholders;
import de.xite.scoreboard.utils.Teams;

public class PowerBoardAPI {
	// ---- Placeholders ---- //
	public static void registerCustomPlaceholders(CustomPlaceholders ph) {
		Placeholders.ph.add(ph);
	}
	// ---------------------//
	// ---- Scoreboard ---- //
	// ---------------------//
	public static void setScoreboard(Player p, boolean custom, String scoreboardName) {
		if(custom || scoreboardName == null) {
			ScoreboardPlayer.setScoreboard(p, custom, null);
		}else {
			// Custom is always 'false'
			ScoreboardPlayer.setScoreboard(p, false, ScoreboardManager.get(scoreboardName));
		}
	}
	public static void removeScoreboard(Player p) {
		ScoreboardPlayer.removeScoreboard(p, true);
	}
	public static boolean hasConfigScoreboard(Player p) {
		return ScoreboardPlayer.players.containsKey(p);
	}
	
	public static void setScoreboardTitle(Player p, String title, boolean usePlaceholders) {
		if(!ScoreTitleUtils.setTitle(p, title, usePlaceholders, null))
				PowerBoard.pl.getLogger().severe("Failed to set the Scoreboard-Title! "+p.getName()+"'s scoreboard is not registered yet - please set the scoreboard first!");
	}
	public static void setScoreboardScore(Player p, String score, int index, boolean usePlaceholders) {
		if(!ScoreTitleUtils.setScore(p, score, index, usePlaceholders, null))
			PowerBoard.pl.getLogger().severe("Failed to set the Scoreboard-Score! "+p.getName()+"'s scoreboard is not registered yet - please set the scoreboard first!");
	}
	public static void setScoreboardScores(Player p, ArrayList<String> scores, boolean usePlaceholders) {
		if(!ScoreTitleUtils.setScores(p, scores, usePlaceholders, null))
			PowerBoard.pl.getLogger().severe("Failed to set the Scoreboard-Scores! "+p.getName()+"'s scoreboard is not registered yet - please set the scoreboard first with!");
	}
	
	// ----------------//
	// ---- Ranks ---- //
	// ----------------//
	// Set
	public static boolean setPrefix(Player p, String prefix) {
		Teams t = Teams.get(p);
		if(t == null)
			return false;
		t.setPrefix(prefix);
		return true;
	}
	public static boolean setSuffix(Player p, String suffix) {
		Teams t = Teams.get(p);
		if(t == null)
			return false;
		t.setSuffix(suffix);
		return true;
	}
	public static boolean setNameColorChar(Player p, ChatColor color) {
		Teams t = Teams.get(p);
		if(t == null)
			return false;
		t.setNameColor(color);
		return true;
	}
	// Get
	public String getPrefix(Player p) {
		Teams t = Teams.get(p);
		if(t == null)
			return null;
		return t.getPrefix();
	}
	public String getSuffix(Player p) {
		Teams t = Teams.get(p);
		if(t == null)
			return null;
		return t.getSuffix();
	}
	public ChatColor getNameColor(Player p) {
		Teams t = Teams.get(p);
		if(t == null)
			return null;
		return t.getNameColor();
	}
	// Utils
	public static void updateTablistRanks(Player p) {
		RankManager.updateTablistRanks(p);
	}
}
