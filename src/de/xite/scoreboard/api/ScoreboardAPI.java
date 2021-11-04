package de.xite.scoreboard.api;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.modules.board.ScoreTitleUtils;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.utils.Placeholders;

public class ScoreboardAPI {
	// ---- Placeholders ---- //
	public static void registerCustomPlaceholders(CustomPlaceholders ph) {
		Placeholders.ph.add(ph);
	}
	
	// ---- Scoreboard ---- //
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link #setScoreboard(p)} instead.  
	 */
	@Deprecated
	public static void enableScoreboard(Player p, String s) {
		ScoreboardPlayer.setScoreboard(p);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link #setScoreboard(p)} instead.  
	 */
	@Deprecated
	public static void disableScoreboard(Player p, String s) {
		ScoreboardPlayer.removeScoreboard(p, false);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link #setScoreboard(p)} instead.  
	 */
	@Deprecated
	public static void enableScoreboard(Player p) {
		ScoreboardPlayer.setScoreboard(p);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link #setScoreboard(p)} instead.  
	 */
	@Deprecated
	public static void disableScoreboard(Player p) {
		ScoreboardPlayer.removeScoreboard(p, false);
	}
	
	
	
	
	
	public static void setScoreboard(Player p) {
		ScoreboardPlayer.setScoreboard(p);
	}
	public static void removeScoreboard(Player p) {
		ScoreboardPlayer.removeScoreboard(p, false);
	}
	
	public static void setScoreboardTitle(Player p, String title, boolean usePlaceholders) {
		if(!ScoreTitleUtils.setTitle(p, p.getScoreboard(), title, usePlaceholders, null))
				Main.pl.getLogger().severe("Failed to set the Scoreboard-Title! The scoreboard is not registered yet - please set the scoreboard first with setScoreboard(p) !");
	}
	public static void setScoreboardScore(Player p, String score, int index, boolean usePlaceholders) {
		if(!ScoreTitleUtils.setScore(p, p.getScoreboard(), score, index, usePlaceholders, null))
			Main.pl.getLogger().severe("Failed to set the Scoreboard-Score! The scoreboard is not registered yet - please set the scoreboard first with setScoreboard(p) !");
	}
	public static void setScoreboardScores(Player p, ArrayList<String> scores, boolean usePlaceholders) {
		if(!ScoreTitleUtils.setScores(p, p.getScoreboard(), scores, usePlaceholders, null))
			Main.pl.getLogger().severe("Failed to set the Scoreboard-Scores! The scoreboard is not registered yet - please set the scoreboard first with setScoreboard(p) !");
	}
	// ---- Ranks ---- //
	/* Planned
	public void setPrefix(String prefix) {
		
	}
	public void setSuffix(String suffix) {
		
	}
	public void setNameColor(ChatColor color) {
		
	}
	
	public String getPrefix() {
		return null;
	}
	public String getSuffix() {
		return null;
	}
	public ChatColor getNameColor() {
		return null;
	}
	 */
}
