package de.xite.scoreboard.modules.ranks;

import java.util.ArrayList;

import de.xite.scoreboard.depend.LuckPermsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import de.xite.scoreboard.api.TeamSetEvent;
import de.xite.scoreboard.depend.LuckPermsRanks;
import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Teams;

public class RankManager {
	static PowerBoard pl = PowerBoard.pl;
	
	private static final ArrayList<Player> tablistRankUpdateDelay = new ArrayList<>();
	private static final ArrayList<Player> tablistRankUpdateWaiting = new ArrayList<>();

	private static BukkitTask tablistRanksUpdateScheduler = null;

	public static boolean register(Player p) {
		if(pl.getConfig().getBoolean("ranks.luckperms-api.enable")) {
			//--- Perm System: LuckPerms API ---//
			return LuckPermsRanks.registerLuckPermsAPIRank(p);
			
		}else if(pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("api")) {
			//--- Perm System: PB API ---//

			TeamSetEvent tse = new TeamSetEvent(p);
			Bukkit.getScheduler().runTask(pl, () -> {
				Bukkit.getPluginManager().callEvent(tse);
				if(!tse.isCancelled()) {
					Teams.addPlayer(p, tse.getPrefix(), tse.getSuffix(), tse.getNameColor(), tse.getChatPrefix(), tse.getRankDisplayName(), tse.getPlayerListName(), tse.getWeight());

					if(PowerBoard.debug) {
						pl.getLogger().info("------------------------------------------------------");
						pl.getLogger().info("(PB API) The player "+p.getName()+" has now the rank:");
						pl.getLogger().info("Prefix:          "+tse.getPrefix());
						pl.getLogger().info("ChatPrefix:      "+tse.getChatPrefix());
						pl.getLogger().info("Suffix:          "+tse.getSuffix());
						pl.getLogger().info("NameColor:       "+tse.getNameColor()+"COLOR");
						pl.getLogger().info("RankDisplayName: "+tse.getRankDisplayName());
						pl.getLogger().info("PlayerListName:  "+tse.getPlayerListName());
						pl.getLogger().info("Weight:          "+tse.getWeight());
						pl.getLogger().info("------------------------------------------------------");
					}
				}else
					if(PowerBoard.debug)
						pl.getLogger().info("TeamSetEvent cancelled for player: "+p.getName());
			});
			return true;
			
		}else {
			//--- PB's rank system ---//
			int weight = 0;
			for(String line : pl.getConfig().getConfigurationSection("ranks.list").getValues(false).keySet()) {
				if(!line.contains(".")) {
					String permission = pl.getConfig().getString("ranks.list."+line+".permission");

					boolean luckperms = false;
					if(LuckPermsAPI.isActive() && pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("luckperms"))
						if(LuckPermsRanks.isPlayerInGroup(p, permission))
							luckperms = true;
					
					if(luckperms || p.hasPermission(permission)) {
						String prefix = 			pl.getConfig().getString("ranks.list."+line+".prefix");
						String suffix = 			pl.getConfig().getString("ranks.list."+line+".suffix");
						String chatPrefix = 		pl.getConfig().getString("ranks.list."+line+".chatPrefix");
						String placeholderName =	pl.getConfig().getString("ranks.list."+line+".placeholder-name");
						String nameColor = 			ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', prefix));

						Teams t = Teams.addPlayer(p, prefix, suffix, nameColor, chatPrefix, placeholderName, null, weight);
						t.setRankDisplayName(t.getNameColor()+t.getRankDisplayName());
						
						if(PowerBoard.debug)
							if(luckperms) {
								pl.getLogger().info("The player "+p.getName()+" has now the rank (PermSystem: none): Prefix: "+prefix+"; Suffix: "+suffix+"; Permission: "+permission);
							}else
								pl.getLogger().info("The player "+p.getName()+" has now the rank (PermSystem: LuckPerms): Prefix: "+prefix+"; Suffix: "+suffix+"; Permission: "+permission);

						return true;
					}
					weight++;
				}
			}
			if(Teams.get(p) == null && !pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("api")) {
				Teams.addPlayer(p, "", "", "f", "noRank", null, null, 0);
				pl.getLogger().warning("The player "+p.getName()+" has no Rank! Make sure that he has the correct permissions.");
			}
		}
		return false;
	}

	public static void setTablistRanks(Player p) {
		if(p == null)
			return;

		delay(p, 20*10);
		
		// Set all player's ranks for the new player
		for(Player all : Bukkit.getOnlinePlayers()) {
			if(all != p) {
				Teams teams = Teams.get(all);
				if(teams != null) {
					Team t = p.getScoreboard().getTeam(teams.getTeamName());
					if(t == null)
						t = p.getScoreboard().registerNewTeam(teams.getTeamName());
					
					ChatColor nameColor = teams.getNameColor();
					
					setPrefixSuffix(p, t, teams.getPrefix(), teams.getSuffix(), teams.getPlayerListName());
					
					if(PowerBoard.aboveMC_1_13 && nameColor != null)
						t.setColor(nameColor);
						
					t.addEntry(all.getName());
				}else
					pl.getLogger().warning("Did not set "+all.getName()+"'s rank for player "+p.getName());
			}
		}
		
		Teams teams = Teams.get(p);
		// Set the new player for all players that are online
		if(teams != null) {
			ChatColor nameColor = teams.getNameColor();
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				Team t = all.getScoreboard().getTeam(teams.getTeamName());
				if(t == null) {
					try {
						t = all.getScoreboard().registerNewTeam(teams.getTeamName());
					}catch (IllegalArgumentException e) {
						pl.getLogger().warning("Team could not be registered. You can usually ignore this message. If you encounter problems, you can enable the debug to get more details.");
						if(PowerBoard.debug)
							e.printStackTrace();
					}
				}

				
				setPrefixSuffix(p, t, teams.getPrefix(), teams.getSuffix(), teams.getPlayerListName());
				
				if(nameColor != null && PowerBoard.aboveMC_1_13)
					t.setColor(nameColor);
				
				t.addEntry(p.getName());
			}

		}else
			pl.getLogger().warning("Did not set "+p.getName()+"'s rank for the already online players");
		if(PowerBoard.debug)
			pl.getLogger().info("Ranks set for player "+p.getName());
	}

	/**
	 *
	 * @param p the Player
	 * @param queueIfDelayed if the player should be added to the queue if delayed
	 */
	public static void updateTablistRanks(Player p, boolean queueIfDelayed) {
		// Only let it update every 3 seconds
		if(tablistRankUpdateDelay.contains(p)) {
			if(queueIfDelayed && !tablistRankUpdateWaiting.contains(p))
				tablistRankUpdateWaiting.add(p);
			if(PowerBoard.debug)
				pl.getLogger().info("Updating "+p.getName()+"'s rank has been delayed to prevent lags and performance issues. The rank will automatically update in a few seconds.");
			return;
		}
		delay(p, 20*5);
		
		if(PowerBoard.debug)
			pl.getLogger().info("Updating "+p.getName()+"'s rank..");
		
		// Update the team info
		RankManager.register(p);
		
		// Apply the new team info's to the prefix & suffix
		try {
			Teams teams = Teams.get(p);
			if(teams != null) {
				ChatColor nameColor = teams.getNameColor();
				
				for(Player all : Bukkit.getOnlinePlayers()) {
					Team t = all.getScoreboard().getTeam(teams.getTeamName());
					if(t == null)
						t = all.getScoreboard().registerNewTeam(teams.getTeamName());
					
					setPrefixSuffix(p, t, teams.getPrefix(), teams.getSuffix(), teams.getPlayerListName());
					
					if(nameColor != null && PowerBoard.aboveMC_1_13)
						t.setColor(nameColor);
					t.addEntry(p.getName());
				}
			}

		}catch (Exception e) {
			pl.getLogger().warning("There was an error whilst updating "+p.getName()+"'s rank!");
		}
		if(PowerBoard.debug)
			pl.getLogger().info(p.getName() + "'s rank has been updated.");
	}
	
	public static void startTablistRanksUpdateScheduler() {
		if(tablistRanksUpdateScheduler != null)
			tablistRanksUpdateScheduler.cancel();

		int interval = pl.getConfig().getInt("ranks.update-interval");
		if(interval <= 0) // Do not auto-update ranks if below or equal 0
			return;
		
		interval = interval * 20 * 60; // Convert minutes to ticks

		tablistRanksUpdateScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(pl, () -> {
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(PowerBoard.debug)
					pl.getLogger().info("Updating all ranks (rank.update-interval)");
				updateTablistRanks(all, false);
			}
		}, interval, interval);
	}
	
	//-------//
	// Utils //
	//-------//
	public static void setPrefixSuffix(Player p, Team t, String prefix, String suffix, String playerListName) {
		boolean showPrefixInTab = pl.getConfig().getBoolean("ranks.options.show-prefix-in-tab");
		boolean showSuffixInTab = pl.getConfig().getBoolean("ranks.options.show-suffix-in-tab");
		boolean usePlayListName = pl.getConfig().getBoolean("ranks.options.use-player-list-name");
		boolean usePrefixSuffixForPlayerListName = pl.getConfig().getBoolean("ranks.options.try-player-head-with-pln");

		try {
			if(showPrefixInTab && !prefix.isEmpty())
				t.setPrefix(prefix);
			if(showSuffixInTab && !suffix.isEmpty())
				t.setSuffix(suffix);
			p.setPlayerListName(null);
		}catch (IllegalArgumentException e) {
			// IllegalArgumentException in this case is thrown if prefix or suffix is too long
			// With setPlayerListName you can bypass this limit, however the prefix and suffix will no longer be displayed above the player head
			usePlayListName = true;
			if(PowerBoard.debug)
				pl.getLogger().info("Using prefix/suffix too long bypass for player "+p.getName()+". The player won't have a prefix above his head.");
		}

		if(usePlayListName) {
			playerListName = "";
			if(showPrefixInTab)
				playerListName += prefix;

			playerListName += p.getDisplayName();

			if(showSuffixInTab)
				playerListName += suffix;
		}

		if(playerListName != null) {
			if(!usePrefixSuffixForPlayerListName) {
				t.setPrefix("");
				t.setSuffix("");
			}
			p.setPlayerListName(playerListName);
		}
	}

	private static void delay(Player p, int i) {
		if(!tablistRankUpdateDelay.contains(p))
			tablistRankUpdateDelay.add(p);

		Bukkit.getScheduler().runTaskLaterAsynchronously(pl, () -> {
			tablistRankUpdateDelay.remove(p);
			if(tablistRankUpdateWaiting.contains(p)) {
				tablistRankUpdateWaiting.remove(p);
				updateTablistRanks(p, false);
			}
		}, i);
	}
}
