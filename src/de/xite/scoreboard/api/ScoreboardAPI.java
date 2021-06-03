package de.xite.scoreboard.api;

import org.bukkit.entity.Player;

import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.manager.ScoreboardPlayer;

public class ScoreboardAPI {
	public static boolean useAPI = false;
	
	public static void registerCustomPlaceholders(CustomPlaceholders ph) {
		Main.ph.add(ph);
	}
	
	// ---- API ---- //
	public static void enableScoreboard(Player p) {
		ScoreboardPlayer.removeScoreboard(p, true);
		ScoreboardPlayer.setScoreboard(p);
	}
	public static void disableScoreboard(Player p) {
		ScoreboardPlayer.removeScoreboard(p, false);
	}
	
	public static void setScoreboardTitle(Player p, String title, boolean usePlaceholders) {
		enableAPI(true);
		if(!ScoreboardPlayer.setTitle(p, p.getScoreboard(), title, usePlaceholders, null))
				Main.pl.getLogger().severe("Failed to set the Scoreboard-Title! The scoreboard is not registered yet - please enable the scoreboard first!");
	}
	public static void setScoreboardScore(Player p, String score, int index, boolean usePlaceholders) {
		enableAPI(true);
		if(!ScoreboardPlayer.setScore(p, p.getScoreboard(), score, index, usePlaceholders, null))
			Main.pl.getLogger().severe("Failed to set the Scoreboard-Score! The scoreboard is not registered yet - please enable the scoreboard first!");
	}
	
	public static void enableAPI(boolean status) {
		if(status) {
			if(!useAPI) {
				Main.unregisterScoreboards();
				useAPI = true;
			}
		}else {
			if(useAPI) {
				Main.registerScoreboards();
				useAPI = false;
			}
		}
	}
	/* Next version
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
