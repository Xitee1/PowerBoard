package de.xite.scoreboard.api;

import java.util.ArrayList;

import org.apache.commons.lang.Validate;
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

	/**
	 * Set a scoreboard for the player.
	 * If you want to set a scoreboard that is configured in the PB's config files, set custom to false.
	 * Then you can either define a specific scoreboardName (name of the xxx.yml file) or set it to null.
	 * If it is null, it will automatically set a scoreboard based on the conditions of that scoreboard.
	 * If you want to set a complete custom scoreboard with this API, set custom to true and set scoreboardName to null.
	 *
	 * @param p the player
	 * @param custom is this a custom scoreboard (set through the api)?
	 * @param scoreboardName the name of the scoreboard.
	 */
	public static void setScoreboard(Player p, boolean custom, String scoreboardName) {
		Validate.notNull(p, "The player cannot be null!");

		if(custom) {
			ScoreboardPlayer.setScoreboard(p, true, null);
		}else {
			ScoreboardManager sm = null;
			if(scoreboardName != null)
				sm = ScoreboardManager.get(scoreboardName);
			ScoreboardPlayer.setScoreboard(p, false, sm);
		}
	}

	public static void removeScoreboard(Player p) {
		ScoreboardPlayer.removeScoreboard(p, true);
	}

	public static boolean hasConfigScoreboard(Player p) {
		return ScoreboardPlayer.players.containsKey(p);
	}

	/**
	 * Set the scoreboard title.
	 * Before using this method, first set a scoreboard using {@link PowerBoardAPI#setScoreboard(Player, boolean, String)}.
	 *
	 * @param p the player
	 * @param title The title of the scoreboard
	 * @param usePlaceholders if placeholders should be replaced by PB
	 */
	public static void setScoreboardTitle(Player p, String title, boolean usePlaceholders) {
		Validate.notNull(p, "The player cannot be null!");

		if(!ScoreTitleUtils.setTitle(p, title, usePlaceholders, null))
				PowerBoard.pl.getLogger().severe("Failed to set the Scoreboard-Title! "+p.getName()+"'s scoreboard is not registered yet - please set the scoreboard first!");
	}

	/**
	 * Set the scoreboard title.
	 * Before using this method, first set a scoreboard using {@link PowerBoardAPI#setScoreboard(Player, boolean, String)}.
	 *
	 * @param p the player
	 * @param score The text of the score
	 * @param index The index of the score
	 * @param usePlaceholders if placeholders should be replaced by PB
	 */
	public static void setScoreboardScore(Player p, String score, int index, boolean usePlaceholders) {
		Validate.notNull(p, "The player cannot be null!");

		if(!ScoreTitleUtils.setScore(p, score, index, usePlaceholders, null))
			PowerBoard.pl.getLogger().severe("Failed to set the Scoreboard-Score! "+p.getName()+"'s scoreboard is not registered yet - please set the scoreboard first!");
	}

	/**
	 * Set the scoreboard title.
	 * Before using this method, first set a scoreboard using {@link PowerBoardAPI#setScoreboard(Player, boolean, String)}.
	 *
	 * @param p the player
	 * @param scores A list with all scores that should be displayed
	 * @param usePlaceholders if placeholders should be replaced by PB
	 */
	public static void setScoreboardScores(Player p, ArrayList<String> scores, boolean usePlaceholders) {
		Validate.notNull(p, "The player cannot be null!");
		Validate.notNull(scores, "The scores array cannot be null!");

		if(!ScoreTitleUtils.setScores(p, scores, usePlaceholders, null))
			PowerBoard.pl.getLogger().severe("Failed to set the Scoreboard-Scores! "+p.getName()+"'s scoreboard is not registered yet - please set the scoreboard first with!");
	}


	// ----------------//
	// ---- Ranks ---- //
	// ----------------//

	/**
	 *
	 * @param p the player
	 * @return the player's prefix
	 */
	public String getPrefix(Player p) {
		Validate.notNull(p, "The player cannot be null!");

		Teams t = Teams.get(p);
		if(t == null)
			return null;
		return t.getPrefix();
	}

	/**
	 * Set the player's prefix. This suffix is used in the chat and tablist.
	 * It will also appear above the player's head and standard MC messages.
	 * Important: The player needs to already have a rank! You are only able to modify the prefix.
	 *
	 * @param p the player
	 * @param prefix the prefix for that player
	 * @return true if successful
	 */
	public static boolean setPrefix(Player p, String prefix) {
		Validate.notNull(p, "The player cannot be null!");

		Teams t = Teams.get(p);
		if(t == null)
			return false;
		t.setPrefix(prefix);
		return true;
	}


	/**
	 *
	 * @param p the player
	 * @return the player's suffix
	 */
	public String getSuffix(Player p) {
		Validate.notNull(p, "The player cannot be null!");

		Teams t = Teams.get(p);
		if(t == null)
			return null;
		return t.getSuffix();
	}

	/**
	 * Set the player's suffix. This suffix is used in the chat and tablist.
	 * It will also appear above the player's head and standard MC messages.
	 * Important: The player needs to already have a rank! You are only able to modify the suffix.
	 *
	 * @param p the player
	 * @param suffix the player's suffix
	 * @return true if successful
	 */
	public static boolean setSuffix(Player p, String suffix) {
		Validate.notNull(p, "The player cannot be null!");

		Teams t = Teams.get(p);
		if(t == null)
			return false;
		t.setSuffix(suffix);
		return true;
	}

	/**
	 * Gets the ChatColor of the player's name in the tablist.
	 *
	 * @param p the player
	 * @return The ChatColor
	 */
	public ChatColor getNameColor(Player p) {
		Validate.notNull(p, "The player cannot be null!");

		Teams t = Teams.get(p);
		if(t == null)
			return null;
		return t.getNameColor();
	}

	/**
	 *
	 * @deprecated use {@link PowerBoardAPI#setNameColor(Player, ChatColor)} instead.
	 */
	public static boolean setNameColorChar(Player p, ChatColor color) {
		Validate.notNull(p, "The player cannot be null!");

		return setNameColor(p, color);
	}

	/**
	 * The ChatColor for the player's name in the tablist. If not set, the player's name will be white.
	 * Important: The player needs to already have a rank! You are only able to modify the nameColor.
	 *
	 * @param p the player
	 * @param color the ChatColor
	 * @return true if successful
	 */
	public static boolean setNameColor(Player p, ChatColor color) {
		Validate.notNull(p, "The player cannot be null!");

		Teams t = Teams.get(p);
		if(t == null)
			return false;
		t.setNameColor(color);
		return true;
	}

	// Utils

	/**
	 * Updates the ranks in the tablist for that player.
	 * This is needed after prefix, suffix or name color has been changed,
	 * or they include a placeholder that needs to be updated.
	 *
	 * @param p the player
	 * @param queueIfDelayed if the player should be added to the queue if delayed
	 */
	public static void updateTablistRanks(Player p, boolean queueIfDelayed) {
		Validate.notNull(p, "The player cannot be null!");

		if(PowerBoard.debug)
			PowerBoard.pl.getLogger().info("Updating "+p.getName()+"'s rank from PB API.");
		RankManager.updateTablistRanks(p, queueIfDelayed);
	}
}
