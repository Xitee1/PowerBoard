package de.xite.scoreboard.api;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.board.ScoreTitleUtils;
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
	public static void setScoreboard(Player p) {
		ScoreboardPlayer.setScoreboard(p);
	}
	public static void removeScoreboard(Player p) {
		ScoreboardPlayer.removeScoreboard(p, false);
	}
	
	public static void setScoreboardTitle(Player p, String title, boolean usePlaceholders) {
		if(!ScoreTitleUtils.setTitle(p, p.getScoreboard(), title, usePlaceholders, null))
				PowerBoard.pl.getLogger().severe("Failed to set the Scoreboard-Title! The scoreboard is not registered yet - please set the scoreboard first with 'setScoreboard(p);' !");
	}
	public static void setScoreboardScore(Player p, String score, int index, boolean usePlaceholders) {
		if(!ScoreTitleUtils.setScore(p, p.getScoreboard(), score, index, usePlaceholders, null))
			PowerBoard.pl.getLogger().severe("Failed to set the Scoreboard-Score! The scoreboard is not registered yet - please set the scoreboard first with 'setScoreboard(p);' !");
	}
	public static void setScoreboardScores(Player p, ArrayList<String> scores, boolean usePlaceholders) {
		if(!ScoreTitleUtils.setScores(p, p.getScoreboard(), scores, usePlaceholders, null))
			PowerBoard.pl.getLogger().severe("Failed to set the Scoreboard-Scores! The scoreboard is not registered yet - please set the scoreboard first with 'setScoreboard(p);' !");
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
	public static boolean setNameColorChar(Player p, String colorChar) {
		Teams t = Teams.get(p);
		if(t == null)
			return false;
		t.setNameColor(colorChar);
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
