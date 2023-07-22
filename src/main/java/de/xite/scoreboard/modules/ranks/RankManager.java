package de.xite.scoreboard.modules.ranks;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import de.xite.scoreboard.api.TeamSetEvent;
import de.xite.scoreboard.depend.LuckPermsRanks;
import de.xite.scoreboard.main.ExternalPlugins;
import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Teams;

public class RankManager {
	static PowerBoard pl = PowerBoard.pl;
	
	private static ArrayList<Player> tablistRankUpdateDelay = new ArrayList<>();
	private static ArrayList<Player> tablistRankUpdateWaiting = new ArrayList<>();

	public static boolean register(Player p) {
		if(pl.getConfig().getBoolean("ranks.luckperms-api.enable")) {
			//--- Perm System: LuckPerms API ---//
			boolean b = LuckPermsRanks.registerLuckPermsAPIRank(p);
			if(b)
				if(pl.getConfig().getBoolean("tablist.ranks"))
					RankManager.setTablistRanks(p);
			return b;
			
		}else if(pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("api")) {
			//--- Perm System: PB API ---//

			TeamSetEvent tse = new TeamSetEvent(p);
			Bukkit.getScheduler().runTask(pl, new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(tse);
					if(!tse.isCancelled()) {
						Teams.addPlayer(p, tse.getPrefix(), tse.getSuffix(), tse.getNameColor(), tse.getChatPrefix(), tse.getRankDisplayName(), tse.getPlayerListName(), tse.getWeight());
						if(pl.getConfig().getBoolean("tablist.ranks"))
							RankManager.setTablistRanks(p);
						if(PowerBoard.debug) {
							pl.getLogger().info("------------------------------------------------------");
							pl.getLogger().info("(PB API) The player "+p.getName()+" has now the rank:");
							pl.getLogger().info("Prefix:          "+tse.getPrefix());
							pl.getLogger().info("ChatPrefix:      "+tse.getChatPrefix());
							pl.getLogger().info("Suffix:          "+tse.getSuffix());
							pl.getLogger().info("NameColor:       "+tse.getNameColor()+"(Example Text)");
							pl.getLogger().info("RankDisplayName: "+tse.getRankDisplayName());
							pl.getLogger().info("PlayerListName:  "+tse.getPlayerListName());
							pl.getLogger().info("Weight:          "+tse.getWeight());
							pl.getLogger().info("------------------------------------------------------");
						}
					}else
						if(PowerBoard.debug)
							pl.getLogger().info("TeamSetEvent cancelled for player: "+p.getName());
				}
			});
			return true;
			
		}else {
			//--- Perm System: None ---//
			int weight = 0;
			for(String line : pl.getConfig().getConfigurationSection("ranks.list").getValues(false).keySet()) {
				if(!line.contains(".")) {
					String permission = pl.getConfig().getString("ranks.list."+line+".permission");
					
					
					boolean luckperms = false;
					if(ExternalPlugins.luckPerms != null && pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("luckperms"))
						if(LuckPermsRanks.isPlayerInGroup(p, permission))
							luckperms = true;
					
					if(luckperms || p.hasPermission(permission)) {
						String prefix = 			pl.getConfig().getString("ranks.list."+line+".prefix");
						String suffix = 			pl.getConfig().getString("ranks.list."+line+".suffix");
						String chatPrefix = 		pl.getConfig().getString("ranks.list."+line+".chatPrefix");
						String placeholderName =	pl.getConfig().getString("ranks.list."+line+".placeholder-name");
						String nameColor = 			ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', prefix));

						Teams.addPlayer(p, prefix, suffix, nameColor, chatPrefix, placeholderName, null, weight);
						Teams t = Teams.get(p);
						t.setRankDisplayName(t.getNameColor()+t.getRankDisplayName());
						
						if(PowerBoard.debug)
							if(luckperms) {
								pl.getLogger().info("The player "+p.getName()+" has now the rank (PermSystem: none): Prefix: "+prefix+"; Suffix: "+suffix+"; Permission: "+permission);
							}else
								pl.getLogger().info("The player "+p.getName()+" has now the rank (PermSystem: LuckPerms): Prefix: "+prefix+"; Suffix: "+suffix+"; Permission: "+permission);
						if(pl.getConfig().getBoolean("tablist.ranks"))
							RankManager.setTablistRanks(p);
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
		
		// Set all players that are online for the new player
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
				if(t == null)
					t = all.getScoreboard().registerNewTeam(teams.getTeamName());
				
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
	
	public static boolean updateTablistRanks(Player p) {
		if(p == null)
			return false;

		// Only let it update every 3 seconds
		if(tablistRankUpdateDelay.contains(p)) {
			if(!tablistRankUpdateWaiting.contains(p))
				tablistRankUpdateWaiting.add(p);
			if(PowerBoard.debug)
				pl.getLogger().info("Updating "+p.getName()+"'s rank has been delayed to prevent lags and performance issues. The rank will automatically update in a few seconds.");
			return false;
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
			pl.getLogger().info("Rank has been updated.");
		return true;
	}
	
	public static void startTablistRanksUpdateScheduler() {
		int interval = pl.getConfig().getInt("ranks.update-interval");
		if(interval <= 0) // Do not auto-update ranks if set to -1 (or anything below 0)
			return;
		
		interval = interval * 20 * 60; // Convert minutes to ticks
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
			@Override
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(PowerBoard.debug)
						pl.getLogger().info("Updating all ranks (rank.update-interval)");
					updateTablistRanks(all);
				}
			}
		}, interval, interval);
	}
	
	//-------//
	// Utils //
	//-------//
	public static void setPrefixSuffix(Player p, Team t, String prefix, String suffix, String playerListName) {
		boolean showPrefixInTab = pl.getConfig().getBoolean("ranks.options.show-prefix-in-tab");
		boolean showSuffixInTab = pl.getConfig().getBoolean("ranks.options.show-suffix-in-tab");

		try {
			if(showPrefixInTab && prefix.length() != 0)
				t.setPrefix(prefix);
			if(showSuffixInTab && suffix.length() != 0)
				t.setSuffix(suffix);
			p.setPlayerListName(null);
		}catch (IllegalArgumentException e) {
			// IllegalArgumentException in this case is thrown if prefix or suffix is too long
			// With setPlayerListName you can bypass this limit, however the prefix and suffix will no longer be displayed above the player head
			playerListName = "";
			if(showPrefixInTab)
				playerListName += prefix;

			playerListName += p.getDisplayName();

			if(showSuffixInTab)
				playerListName += suffix;
		}
		if(playerListName != null) {
			t.setPrefix("");
			t.setSuffix("");
			p.setPlayerListName(playerListName);
			
			if(PowerBoard.debug) {
				pl.getLogger().info("Using prefix/suffix too long bypass for player "+p.getName()+".");
				pl.getLogger().info("With this, there will be no prefix or suffix displayed above the player head, only in the tablist.");
				pl.getLogger().info("To prevent this, use less than 64 chars or less than 16 chars in MC 1.12 or below.");
			}
		}
	}

	private static void delay(Player p, int i) {
		if(tablistRankUpdateDelay.contains(p))
			tablistRankUpdateDelay.remove(p);
		tablistRankUpdateDelay.add(p);
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				if(tablistRankUpdateDelay.contains(p))
					tablistRankUpdateDelay.remove(p);
				if(tablistRankUpdateWaiting.contains(p)) {
					tablistRankUpdateWaiting.remove(p);
					updateTablistRanks(p);
				}
			}
		}, i);
	}
}
