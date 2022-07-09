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
	
	private static ArrayList<Player> updateDelay = new ArrayList<>();
	
	public static boolean register(Player p) {
		if(pl.getConfig().getBoolean("ranks.luckperms-api.enable")) {
			return LuckPermsRanks.registerLuckPermsAPIRank(p);
			
		}else if(pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("api")) {
			// Use PowerBoardAPI as perm system
			TeamSetEvent tse = new TeamSetEvent(p);
			Bukkit.getPluginManager().callEvent(tse);
			if(!tse.isCancelled()) {
				Teams.addPlayer(p, tse.getPrefix(), tse.getSuffix(), tse.getNameColorChar(), tse.getChatPrefix(), tse.getRankDisplayName(), tse.getWeight());
			}
			return true;
			
		}else {
			//--- Other PermSystems ---//
			int weight = 0;
			for(String line : pl.getConfig().getConfigurationSection("ranks.list").getValues(false).keySet()) {
				if(!line.contains(".")) {
					String permission = pl.getConfig().getString("ranks.list."+line+".permission");
					
					//--- LuckPerms (without API) ---//
					if(ExternalPlugins.luckPerms != null && pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("luckperms")) {
						if(LuckPermsRanks.isPlayerInGroup(p, permission)) {
							String prefix = pl.getConfig().getString("ranks.list."+line+".prefix");
							String suffix = pl.getConfig().getString("ranks.list."+line+".suffix");
							String chatPrefix = pl.getConfig().getString("ranks.list."+line+".chatPrefix");
							String placeholderName = pl.getConfig().getString("ranks.list."+line+".placeholder-name");
							String nameColor = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', prefix));
							
							Teams.addPlayer(p, prefix, suffix, nameColor, chatPrefix, placeholderName, weight);
							Teams t = Teams.get(p);
							t.setRankDisplayName(t.getNameColor()+t.getRankDisplayName());
							
							if(PowerBoard.debug)
								pl.getLogger().info("The player "+p.getName()+" has now the rank (luckperms): Prefix: "+prefix+"; Suffix: "+suffix+"; Group: "+permission);
							return true;
						}
					}else {
						//---None---//
						if(p.hasPermission(permission)) {
							String prefix = pl.getConfig().getString("ranks.list."+line+".prefix");
							String suffix = pl.getConfig().getString("ranks.list."+line+".suffix");
							String chatPrefix = pl.getConfig().getString("ranks.list."+line+".chatPrefix");
							String placeholderName = pl.getConfig().getString("ranks.list."+line+".placeholder-name");
							String nameColor = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', prefix));

							Teams.addPlayer(p, prefix, suffix, nameColor, chatPrefix, placeholderName, weight);
							Teams t = Teams.get(p);
							t.setRankDisplayName(t.getNameColor()+t.getRankDisplayName());
							
							if(PowerBoard.debug)
								pl.getLogger().info("The player "+p.getName()+" has now the rank (permission/none): Prefix: "+prefix+"; Suffix: "+suffix+"; Permission: "+permission);
							return true;
						}
					}
					weight++;
				}
			}
			if(Teams.get(p) == null && !pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("api")) {
				Teams.addPlayer(p, "", "", "f", "noRank", null, -5555); // -5555 = error code for no rank
				pl.getLogger().warning("The player "+p.getName()+" has no Rank! Make sure that he has the correct permissions.");
			}
		}
	
		return false;
	}
	public static void setTablistRanks(Player p) {
		delay(p, 20*15);
		
		// Set all players that are online for the new player
		for(Player all : Bukkit.getOnlinePlayers()) {
			if(all != p) {
				Teams teams = Teams.get(all);
				if(teams != null) {
					Team t = p.getScoreboard().getTeam(teams.getTeamName());
					if(t == null)
						t = p.getScoreboard().registerNewTeam(teams.getTeamName());
					
					String prefix = teams.getPrefix();
					String suffix = teams.getSuffix();
					ChatColor nameColor = teams.getNameColor();
					
					setPrefixSuffix(p, t, prefix, suffix);
					
					if(PowerBoard.aboveMC_1_13 && nameColor != null)
						t.setColor(nameColor);
						
					t.addEntry(all.getName());
				}else
					pl.getLogger().warning("Did not set "+all.getName()+"'s tablist rank for player "+p.getName()+"");
			}
		}
		
		Teams teams = Teams.get(p);
		// Set the new player for all players that are online
		if(teams != null) {
			ChatColor nameColor = teams.getNameColor();
			String prefix = teams.getPrefix();
			String suffix = teams.getSuffix();
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				Team t = all.getScoreboard().getTeam(teams.getTeamName());
				if(t == null)
					t = all.getScoreboard().registerNewTeam(teams.getTeamName());
				
				setPrefixSuffix(p, t, prefix, suffix);
				
				if(nameColor != null && PowerBoard.aboveMC_1_13)
					t.setColor(nameColor);
				
				t.addEntry(p.getName());
			}

		}else
			pl.getLogger().severe("Did not set "+p.getName()+"'s rank for the already online players");
		if(PowerBoard.debug)
			pl.getLogger().info("Tablist ranks set for player "+p.getName());
	}
	
	public static boolean updateTablistRanks(Player p) {
		// Only let it update every 3 seconds
		if(updateDelay.contains(p)) {
			if(PowerBoard.debug)
				pl.getLogger().info("Did not update "+p.getName()+"'s rank because of the delay. Trying again automatically in 7 seconds..");
			Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
				@Override
				public void run() {
					updateTablistRanks(p);
				}
			}, 20*5);
			return false;
		}
		delay(p, 20*3);
		
		if(PowerBoard.debug)
			pl.getLogger().info("Updating "+p.getName()+"'s rank..");
		
		// Update the team info
		RankManager.register(p);
		
		// Apply the new team infos to the prefix & suffix
		try {
			Teams teams = Teams.get(p);
			if(teams != null) {
				String prefix = teams.getPrefix();
				String suffix = teams.getSuffix();
				
				if(teams != null) {
					ChatColor nameColor = teams.getNameColor();
					
					for(Player all : Bukkit.getOnlinePlayers()) {
						Team t = all.getScoreboard().getTeam(teams.getTeamName());
						if(t == null)
							t = all.getScoreboard().registerNewTeam(teams.getTeamName());
						
						setPrefixSuffix(p, t, prefix, suffix);
						
						if(nameColor != null && PowerBoard.aboveMC_1_13)
							t.setColor(nameColor);
						t.addEntry(p.getName());
					}
				}
			}

		}catch (Exception e) {
			pl.getLogger().warning("There was an error whilst updating "+p.getName()+"'s rank!");
		}
		pl.getLogger().info("Updated rank.");
		return true;
	}
	
	public static void startTablistRanksUpdateScheduler() {
		int interval = pl.getConfig().getInt("ranks.update-interval");
		if(interval == -1) // Do not auto-update ranks if set to -1
			return;
		
		interval = interval * 20 * 60; // Convert minutes to ticks
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
			@Override
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers())
					updateTablistRanks(all);
			}
		}, interval, interval);
	}
	
	//-------//
	// Utils //
	//-------//
	public static void setPrefixSuffix(Player p, Team t, String prefix, String suffix) {
		try {
			if(prefix.length() != 0)
				t.setPrefix(prefix);
			if(suffix.length() != 0)
				t.setSuffix(suffix);
			p.setPlayerListName(null);
		}catch (IllegalArgumentException e) {
			// IllegalArgumentException == prefix or suffix too long
			// With setPlayerListName you can bypass this limit, however the prefix and suffix will no longer be displayed above the player head
			
			t.setPrefix("");
			t.setSuffix("");
			p.setPlayerListName(prefix+p.getDisplayName()+suffix);
			
			if(PowerBoard.debug) {
				pl.getLogger().info("Using prefix/suffix too long bypass for player "+p.getName()+".");
				pl.getLogger().info("With this, there will be no prefix or suffix displayed above the player head, only in the tablist.");
				pl.getLogger().info("To prevent this, use less than 64 chars or less than 16 chars in MC 1.12 or below.");
			}
		}
	}

	private static void delay(Player p, int i) {
		updateDelay.add(p);
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				if(updateDelay.contains(p))
					updateDelay.remove(p);
			}
		}, i);
	}
}
