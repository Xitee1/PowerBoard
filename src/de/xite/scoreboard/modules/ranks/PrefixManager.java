package de.xite.scoreboard.modules.ranks;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.xite.scoreboard.api.TeamSetEvent;
import de.xite.scoreboard.main.ExternalPlugins;
import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Teams;
import de.xite.scoreboard.utils.Version;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

public class PrefixManager {
	static PowerBoard pl = PowerBoard.pl;
	
	public static HashMap<Player, Teams> TeamsList = new HashMap<>();
	public static int TeamCount = 0;
	private static ArrayList<Player> updateDelay = new ArrayList<>();
	
	public static boolean register(Player p) {
		if(pl.getConfig().getBoolean("ranks.luckperms-api.enable")) {
			//--- LuckPerms (with API) ---//
			
			if(ExternalPlugins.luckPerms == null) {
				pl.getLogger().severe("LuckPerms-API enabled, but LuckPerms is not installed!");
				return false;
			}
			//Get user and rank data from LuckPerms
			LuckPerms api = ExternalPlugins.luckPerms;
			User user = api.getUserManager().getUser(p.getUniqueId());
			Group group = api.getGroupManager().getGroup(user.getPrimaryGroup());
			
			// Get the team name
			int i = 0;
			try {
				int loadedGroups = api.getGroupManager().getLoadedGroups().size();
				i = loadedGroups - group.getWeight().getAsInt();
				if(loadedGroups < group.getWeight().getAsInt()) {
					PowerBoard.pl.getLogger().severe("Error in group "+group.getName()+": The group-weight needs to be smaller than the amount of groups!");
					PowerBoard.pl.getLogger().severe("Read the wiki to see which weights you have to set: https://wiki.xitma.de/plugin.php?name=scoreboard#ranks");
					i = 0;
				}
			}catch (Exception e) {
				PowerBoard.pl.getLogger().severe("The group "+group.getName()+" has no weight! Please set weight with /lp group <group> setweight <weight>");
				PowerBoard.pl.getLogger().severe("Read the wiki to see which weights you have to set: https://wiki.xitma.de/plugin.php?name=scoreboard#ranks");
			}
			String i2 = ""+i;
			if(i < 10)
				i2 = "0"+i;
			
			TeamCount++;
			String team = i2+"team-"+TeamCount;
			
			String suffix = "", prefix = "", displayname = "", nameColor = "";
			
			// Get the data
			prefix = user.getCachedData().getMetaData().getPrefix();
			suffix = user.getCachedData().getMetaData().getSuffix();
			displayname = group.getDisplayName();
			
			if(prefix == null)
				prefix = "";
			if(suffix == null)
				suffix = "";
			if(displayname == null) {
				PowerBoard.pl.getLogger().severe("The group "+group.getName()+" has no Displayname! "
						+ "Give the group the permission 'displayname.<enter the displayname>', for example 'displayname.&4Owner'");
				return false;
			}
			
			// Get the chat syntax and check for errors
			String chat = pl.getConfig().getString("ranks.luckperms-api.chat-layout");
			if(chat == null)// Check for old configuration - remove v4.5
				chat = pl.getConfig().getString("ranks.luckperms.chat-layout");
			if(chat == null) {// Send error if there is no chat
				pl.getLogger().severe("The rank "+group.getName()+" has no valid chat configuration! Please check the setting 'chat-layout' in the 'luckperms-api' section in your config.yml.");
				chat = "(invalid config) %name% > ";
			}
			chat = chat.replace("%prefix%", prefix).replace("%name%", p.getName()).replace("%displayname%", displayname); // Replace chat placeholders
			
			if(displayname.length() == 0)
				displayname = prefix; // Make the displayname the prefix if the displayname is not set
			// Get the name color and check for errors
			if(pl.getConfig().getBoolean("placeholder.preferLastPrefixColor")) {
				nameColor = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', displayname));
				if(nameColor == null)
					pl.getLogger().warning("Could not get the last color from the displayname of the rank "+group.getName()+". Make sure to put a colorcode at the end of your displayname, otherwise the player name will always be white.");
			}else {
				nameColor = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', prefix));
				if(nameColor == null)
					pl.getLogger().warning("Could not get the last color from "+p.getName()+"'s prefix. Make sure to put a colorcode at the end of your prefix, otherwise the player name will always be white.");
			}
			
			if(PowerBoard.debug)
				pl.getLogger().info("The player "+p.getName()+" has the rank (luckperms-api): Prefix: "+prefix+"; Suffix: "+suffix+"; NamColor: "+nameColor+"; Displayname: "+displayname+"; Group: "+group.getName());
			
			// Register the player with all the collected data
			try {
				Teams.addPlayer(p, prefix, suffix, nameColor, team, chat, displayname);
				return true;
			}catch (Exception e) {
				// If somehow something does no work, send a error message to configure the rank properly.
				pl.getLogger().severe("The player "+p.getName()+" could not be added to a team! Please check your rank configuration!");
			}
		}else if(pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("api")) {
			//--- Use PowerBoardAPI ---//

			
			TeamSetEvent tse = new TeamSetEvent(p);
			Bukkit.getPluginManager().callEvent(tse);
			if (!tse.isCancelled()) {
				String team;
				int i = tse.getWeight();
				String i2 = ""+i;
				if(i < 10)
					i2 = "0"+i;
				TeamCount++;
				team = i2+"team-"+TeamCount;
				
				Teams.addPlayer(p, tse.getPrefix(), tse.getSuffix(), tse.getNameColorChar(), team, tse.getChatPrefix(), tse.getPlaceholderName());
			}
			
			return true;
		}else {
			//--- Other PermSystems ---//
			int i = 0;
			for(String line : pl.getConfig().getConfigurationSection("ranks.list").getValues(false).keySet()) {
				if(!line.contains(".")) {
					String i2 = ""+i;
					if(i < 10)
						i2 = "0"+i;
					
					String permission = pl.getConfig().getString("ranks.list."+line+".permission");
					String prefix = pl.getConfig().getString("ranks.list."+line+".prefix");
					String suffix = pl.getConfig().getString("ranks.list."+line+".suffix");
					String nameColor = pl.getConfig().getString("ranks.list."+line+".nameColor").replace("&", "");
					String chatPrefix = pl.getConfig().getString("ranks.list."+line+".chatPrefix");
					String placeholderName = pl.getConfig().getString("ranks.list."+line+".placeholder-name");
					String team;
					
					//--- LuckPerms (without API) ---//
					if(ExternalPlugins.luckPerms != null && pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("luckperms")) {
						if(isPlayerInGroup(p, permission)) {
							if(PowerBoard.debug)
								pl.getLogger().info("The player "+p.getName()+" has now the rank (luckperms): Prefix: "+prefix+"; Suffix: "+suffix+"; Group: "+permission);
							TeamCount++;
							team = i2+"team-"+TeamCount;
							Teams.addPlayer(p, prefix, suffix, nameColor, team, chatPrefix, placeholderName);
							Teams t = Teams.get(p);
							t.setPlaceholderName(t.getNameColor()+t.getPlaceholderName());
							return true;
						}
					}else {
						//---None---//
						if(p.hasPermission(permission)) {
							if(PowerBoard.debug)
								pl.getLogger().info("The player "+p.getName()+" has now the rank (permission/none): Prefix: "+prefix+"; Suffix: "+suffix+"; Permission: "+permission);
							
							TeamCount++;
							team = i2+"team-"+TeamCount;
							
							Teams.addPlayer(p, prefix, suffix, nameColor, team, chatPrefix, placeholderName);
							Teams t = Teams.get(p);
							t.setPlaceholderName(t.getNameColor()+t.getPlaceholderName());
							return true;
						}
					}
					i++;
				}
			}
			if(Teams.get(p) == null && !pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("api")) {
				String i2 = ""+i;
				if(i < 10) {
					i2 = "0"+i;
				}
				Teams.addPlayer(p, "", "", "f", i2+"team-noRank", "noRank", null);
				pl.getLogger().warning("The player "+p.getName()+" has no Rank! Make sure that he has the correct permissions.");
			}
		}
	
		return false;
	}
	public static void updateTeams(Player p) {
		if(updateDelay.contains(p))
			return;
		delay(p);
		
		try {
			Teams teams = Teams.get(p);
			for(Player all : Bukkit.getOnlinePlayers()) {
				Team t = all.getScoreboard().getTeam(teams.getTeamName());
				if(t == null)
					t = all.getScoreboard().registerNewTeam(teams.getTeamName());
				
				String prefix = teams.getPrefix();
				String suffix = teams.getSuffix();
				ChatColor nameColor = teams.getNameColor();
				
				if(prefix.length() != 0)
					t.setPrefix(prefix);
				if(suffix.length() != 0)
					t.setSuffix(suffix);
				if(nameColor != null && PowerBoard.getBukkitVersion().compareTo(new Version("1.13")) == 1) // only for version 1.13+
					t.setColor(nameColor);
				t.addEntry(p.getName());
			}
		}catch (Exception e) {}
		if(PowerBoard.debug)
    		PowerBoard.pl.getLogger().info("Updated "+p.getName()+"'s team!");
	}
	public static void setTeams(Player p, Scoreboard board) {
		delay(p);
		// Set for the new player all players that are already online
		for(Player all : Bukkit.getOnlinePlayers()) {
			Teams teams = Teams.get(all);
			if(teams != null) {// Teams are set a few seconds after a player joined and have no team yet. This prevents errors.
				Team t = board.getTeam(teams.getTeamName());
				if(t == null)
					t = board.registerNewTeam(teams.getTeamName());
				
				ChatColor nameColor = teams.getNameColor();
				
				t.setPrefix(teams.getPrefix());
				t.setSuffix(teams.getSuffix());
				if(nameColor != null && PowerBoard.getBukkitVersion().compareTo(new Version("1.13")) == 1) // only for version 1.13+
					t.setColor(nameColor);
				t.addEntry(p.getName());
			}

		}
		// Set for all players that are already online the new player
		Teams teams = Teams.get(p);
		for(Player all : Bukkit.getOnlinePlayers()) {
			Team t = all.getScoreboard().getTeam(teams.getTeamName());
			if(t == null)
				t = all.getScoreboard().registerNewTeam(teams.getTeamName());
			
			String prefix = teams.getPrefix();
			String suffix = teams.getSuffix();
			ChatColor nameColor = teams.getNameColor();
			
			if(prefix.length() != 0)
				t.setPrefix(prefix);
			if(suffix.length() != 0)
				t.setSuffix(suffix);
			if(nameColor != null && PowerBoard.getBukkitVersion().compareTo(new Version("1.13")) == 1) // only for version 1.13+
				t.setColor(nameColor);
			t.addEntry(p.getName());
		}
	}
	private static void delay(Player p) {
		updateDelay.add(p);
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				updateDelay.remove(p);
			}
		}, 20); // wait one second before allow new update to save performance
	}
	
	
	// LuckPerms without API
	public static boolean isPlayerInGroup(Player p, String g) {
		LuckPerms api = ExternalPlugins.luckPerms;
		User user = api.getUserManager().getUser(p.getUniqueId());
		String group = user.getPrimaryGroup();
		if(PowerBoard.debug)
			PowerBoard.pl.getLogger().info("Checking "+p.getName()+"'s group: "+group);
		if(g.equalsIgnoreCase(group)) {
			return true;
		}else {
			if(PowerBoard.debug)
				PowerBoard.pl.getLogger().info(p.getName()+" has no valid group!");
		}
	    return false;
	}
}
