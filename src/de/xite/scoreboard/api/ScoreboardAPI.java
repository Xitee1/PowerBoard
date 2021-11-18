package de.xite.scoreboard.api;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class ScoreboardAPI {
	// ---- Placeholders ---- //
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI.registerCustomPlaceholders(ph)} instead.  
	 */
	public static void registerCustomPlaceholders(CustomPlaceholders ph) {
		PowerBoardAPI.registerCustomPlaceholders(ph);
	}
	
	// ---- Scoreboard ---- //
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI.setScoreboard(p)} instead.  
	 */
	@Deprecated
	public static void enableScoreboard(Player p, String s) {
		PowerBoardAPI.setScoreboard(p);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI.setScoreboard(p)} instead.  
	 */
	@Deprecated
	public static void disableScoreboard(Player p, String s) {
		PowerBoardAPI.removeScoreboard(p);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI.setScoreboard(p)} instead.  
	 */
	@Deprecated
	public static void enableScoreboard(Player p) {
		PowerBoardAPI.setScoreboard(p);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI.setScoreboard(p)} instead.  
	 */
	@Deprecated
	public static void disableScoreboard(Player p) {
		PowerBoardAPI.removeScoreboard(p);
	}
	
	
	
	
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI.setScoreboard(p)} instead.  
	 */
	@Deprecated
	public static void setScoreboard(Player p) {
		PowerBoardAPI.setScoreboard(p);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI.setScoreboard(p)} instead.  
	 */
	public static void removeScoreboard(Player p) {
		PowerBoardAPI.removeScoreboard(p);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI.setScoreboard(p, title, usePlaceholders)} instead.  
	 */
	public static void setScoreboardTitle(Player p, String title, boolean usePlaceholders) {
		PowerBoardAPI.setScoreboardTitle(p, title, usePlaceholders);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI.setScoreboardScore(p, score, index, usePlaceholders)} instead.  
	 */
	public static void setScoreboardScore(Player p, String score, int index, boolean usePlaceholders) {
		PowerBoardAPI.setScoreboardScore(p, score, index, usePlaceholders);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI.setScoreboardScores(p, scores, usePlaceholders)} instead.  
	 */
	public static void setScoreboardScores(Player p, ArrayList<String> scores, boolean usePlaceholders) {
		PowerBoardAPI.setScoreboardScores(p, scores, usePlaceholders);
	}
}
