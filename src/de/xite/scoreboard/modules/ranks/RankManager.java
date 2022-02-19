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
	
	public static ArrayList<Player> updateDelay = new ArrayList<>();
	
	public static boolean register(Player p) {
		if(pl.getConfig().getBoolean("ranks.luckperms-api.enable")) {
			return LuckPermsRanks.registerLuckPermsAPIRank(p);
			
		}else if(pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("api")) {
			// Use PowerBoardAPI as perm system
			TeamSetEvent tse = new TeamSetEvent(p);
			Bukkit.getPluginManager().callEvent(tse);
			if(!tse.isCancelled()) {
				Teams.addPlayer(p, tse.getPrefix(), tse.getSuffix(), tse.getNameColorChar(), tse.getChatPrefix(), tse.getPlaceholderName(), tse.getWeight());
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
						String prefix = pl.getConfig().getString("ranks.list."+line+".prefix");
						String suffix = pl.getConfig().getString("ranks.list."+line+".suffix");
						String chatPrefix = pl.getConfig().getString("ranks.list."+line+".chatPrefix");
						String placeholderName = pl.getConfig().getString("ranks.list."+line+".placeholder-name");
						String nameColor = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', prefix));
						if(LuckPermsRanks.isPlayerInGroup(p, permission)) {
							if(PowerBoard.debug)
								pl.getLogger().info("The player "+p.getName()+" has now the rank (luckperms): Prefix: "+prefix+"; Suffix: "+suffix+"; Group: "+permission);

							Teams.addPlayer(p, prefix, suffix, nameColor, chatPrefix, placeholderName, weight);
							Teams t = Teams.get(p);
							t.setPlaceholderName(t.getNameColor()+t.getPlaceholderName());
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
							if(PowerBoard.debug)
								pl.getLogger().info("The player "+p.getName()+" has now the rank (permission/none): Prefix: "+prefix+"; Suffix: "+suffix+"; Permission: "+permission);
							
							Teams.addPlayer(p, prefix, suffix, nameColor, chatPrefix, placeholderName, weight);
							Teams t = Teams.get(p);
							t.setPlaceholderName(t.getNameColor()+t.getPlaceholderName());
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
		delay(p, 120);
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
					
					if(PowerBoard.aboveMC_1_13) {
						if(prefix.length() != 0)
							t.setPrefix(prefix.substring(0, Math.min(prefix.length(), 64)));
						if(suffix.length() != 0)
							t.setSuffix(suffix.substring(0, Math.min(suffix.length(), 64)));
					}else {
						if(prefix.length() != 0)
							t.setPrefix(prefix.substring(0, Math.min(prefix.length(), 16)));
						if(suffix.length() != 0)
							t.setSuffix(suffix.substring(0, Math.min(suffix.length(), 16)));
					}
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
			String prefix = teams.getPrefix();
			String suffix = teams.getSuffix();
			ChatColor nameColor = teams.getNameColor();
			if(pl.getConfig().getBoolean("ranks.useUnlimitedLongRanks"))
				p.setPlayerListName(prefix+p.getDisplayName()+suffix);
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				Team t = all.getScoreboard().getTeam(teams.getTeamName());
				if(t == null)
					t = all.getScoreboard().registerNewTeam(teams.getTeamName());
				
				if(PowerBoard.aboveMC_1_13) {
					if(prefix.length() != 0)
						t.setPrefix(prefix.substring(0, Math.min(prefix.length(), 64)));
					if(suffix.length() != 0)
						t.setSuffix(suffix.substring(0, Math.min(suffix.length(), 64)));
				}else {
					if(prefix.length() != 0)
						t.setPrefix(prefix.substring(0, Math.min(prefix.length(), 16)));
					if(suffix.length() != 0)
						t.setSuffix(suffix.substring(0, Math.min(suffix.length(), 16)));
				}
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
		if(updateDelay.contains(p))
			return false;
		delay(p, 40);
		
		try {
			Teams teams = Teams.get(p);
			if(teams != null) {
				String prefix = teams.getPrefix();
				String suffix = teams.getSuffix();
				ChatColor nameColor = teams.getNameColor();
				if(pl.getConfig().getBoolean("ranks.useUnlimitedLongRanks"))
					p.setPlayerListName(prefix+p.getDisplayName()+suffix);
				
				for(Player all : Bukkit.getOnlinePlayers()) {
					Team t = all.getScoreboard().getTeam(teams.getTeamName());
					if(t == null)
						t = all.getScoreboard().registerNewTeam(teams.getTeamName());
					
					if(PowerBoard.aboveMC_1_13) {
						if(prefix.length() != 0)
							t.setPrefix(prefix.substring(0, Math.min(prefix.length(), 64)));
						if(suffix.length() != 0)
							t.setSuffix(suffix.substring(0, Math.min(suffix.length(), 64)));
					}else {
						if(prefix.length() != 0)
							t.setPrefix(prefix.substring(0, Math.min(prefix.length(), 16)));
						if(suffix.length() != 0)
							t.setSuffix(suffix.substring(0, Math.min(suffix.length(), 16)));
					}
					if(nameColor != null && PowerBoard.aboveMC_1_13)
						t.setColor(nameColor);
					t.addEntry(p.getName());
				}
			}
		}catch (Exception e) {}
		if(PowerBoard.debug)
			pl.getLogger().info("Tablist ranks updated for player "+p.getName()+"!");
		return true;
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
