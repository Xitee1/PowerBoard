package de.xite.scoreboard.api;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class ScoreboardAPI {
	// ---- Placeholders ---- //
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI#registerCustomPlaceholders(CustomPlaceholders)} instead.
	 */
	public static void registerCustomPlaceholders(CustomPlaceholders ph) {
		PowerBoardAPI.registerCustomPlaceholders(ph);
	}
	
	// ---- Scoreboard ---- //
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI#setScoreboard(Player, boolean, String)} instead.
	 */
	@Deprecated
	public static void enableScoreboard(Player p, String s) {
		PowerBoardAPI.setScoreboard(p, true, null);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI#setScoreboard(Player, boolean, String)} instead.
	 */
	@Deprecated
	public static void disableScoreboard(Player p, String s) {
		PowerBoardAPI.removeScoreboard(p);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI#setScoreboard(Player, boolean, String)} instead.
	 */
	@Deprecated
	public static void enableScoreboard(Player p) {
		PowerBoardAPI.setScoreboard(p, true, null);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated use {@link PowerBoardAPI#setScoreboard(Player, boolean, String)} instead.
	 */
	@Deprecated
	public static void disableScoreboard(Player p) {
		PowerBoardAPI.removeScoreboard(p);
	}
	
	
	
	
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI#setScoreboard(Player, boolean, String)} instead.
	 */
	@Deprecated
	public static void setScoreboard(Player p) {
		PowerBoardAPI.setScoreboard(p, true, null);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI#removeScoreboard(Player)} instead.
	 */
	public static void removeScoreboard(Player p) {
		PowerBoardAPI.removeScoreboard(p);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI#setScoreboardTitle(Player, String, boolean)} instead.
	 */
	public static void setScoreboardTitle(Player p, String title, boolean usePlaceholders) {
		PowerBoardAPI.setScoreboardTitle(p, title, usePlaceholders);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI#setScoreboardScore(Player, String, int, boolean)} instead.
	 */
	public static void setScoreboardScore(Player p, String score, int index, boolean usePlaceholders) {
		PowerBoardAPI.setScoreboardScore(p, score, index, usePlaceholders);
	}
	/**
	 * API has changed.
	 *
	 * @deprecated Use {@link PowerBoardAPI#setScoreboardScores(Player, ArrayList, boolean)} instead.
	 */
	public static void setScoreboardScores(Player p, ArrayList<String> scores, boolean usePlaceholders) {
		PowerBoardAPI.setScoreboardScores(p, scores, usePlaceholders);
	}
}
