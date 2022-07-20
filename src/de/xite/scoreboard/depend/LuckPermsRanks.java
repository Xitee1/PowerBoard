package de.xite.scoreboard.depend;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.ExternalPlugins;
import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Teams;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

public class LuckPermsRanks {
	static PowerBoard pl = PowerBoard.pl;
	
	public static boolean registerLuckPermsAPIRank(Player p) {
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
		int weight = 0;
		try {
			int i = group.getWeight().getAsInt();
			if(i > 999) {
				PowerBoard.pl.getLogger().severe("Sorry, but PB does not support LP weights higher than 999! Please use smaller values.");
				return false;
			}
			weight = 999 - i;
		}catch (Exception e) {
			PowerBoard.pl.getLogger().severe("---------------------------------------------------------------------------------------------------------------------------");
			PowerBoard.pl.getLogger().severe("The group \""+group.getName()+"\" has no weight! Please set the weight with /lp group <group> setweight <weight>");
			PowerBoard.pl.getLogger().severe("Read the wiki to see which weights you have to set: https://github.com/Xitee1/PowerBoard/wiki#configure-the-ranks-optional");
			PowerBoard.pl.getLogger().severe("---------------------------------------------------------------------------------------------------------------------------");
			return false;
		}
		
		String suffix = "", prefix = "", displayname = "", nameColor = "";
		
		// Get the data
		prefix = user.getCachedData().getMetaData().getPrefix();
		suffix = user.getCachedData().getMetaData().getSuffix();
		displayname = group.getDisplayName();
		
		// Add space to prefix/suffix if enabled
		if(prefix == null) {
			prefix = "";
		}else
			if(pl.getConfig().getBoolean("ranks.luckperms-api.prefix-suffix-space"))
				prefix = prefix+" ";
		
		if(suffix == null) {
			suffix = "";
		}else
			if(pl.getConfig().getBoolean("ranks.luckperms-api.prefix-suffix-space"))
				suffix = " "+suffix;
		
		
		if(displayname == null) {
			PowerBoard.pl.getLogger().severe("--------------------------------------------------------------------------------------------------------------------------------------------------");
			PowerBoard.pl.getLogger().severe("The group \""+group.getName()+"\" has no Displayname! Give the group the permission 'displayname.<displayname>', for example 'displayname.&4Owner'");
			PowerBoard.pl.getLogger().severe("--------------------------------------------------------------------------------------------------------------------------------------------------");
			return false;
		}
		
		YamlConfiguration configNoDefaultSettings = YamlConfiguration.loadConfiguration(new File(PowerBoard.pluginfolder+"/config.yml"));
		
		// Get chat for the rank
		String chat = configNoDefaultSettings.getString("ranks.luckperms-api.chat-prefix."+group.getName());
		
		// Get fallback chat if rank is not listed
		if(chat == null)
			chat = configNoDefaultSettings.getString("ranks.luckperms-api.chat-layout");
		
		// Deprecated - Check for old configuration - support will be removed on v3.7
		if(chat == null) 
			chat = configNoDefaultSettings.getString("ranks.luckperms.chat-layout");
		
		// Send error if there is no chat prefix
		if(chat == null) {
			pl.getLogger().severe("The rank "+group.getName()+" has no valid chat configuration! Please check the setting 'chat-layout' in the 'luckperms-api' section in your config.yml.");
			chat = "(invalid config) %name% > ";
		}
		chat = chat.replace("%prefix%", prefix).replace("%name%", p.getName()).replace("%displayname%", displayname); // Replace chat placeholders
		
		// Get the name color and check for errors
		nameColor = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', prefix));
		if(PowerBoard.debug) {
			if(nameColor == null)
				pl.getLogger().warning("Could not get the last color from "+p.getName()+"'s prefix. Make sure to put a colorcode at the end of your prefix, otherwise the player name will always be white.");
			
			// Send debug about the rank
			pl.getLogger().info("The player "+p.getName()+" has the rank (luckperms-api): Group: "+group.getName()+"; Prefix: "+prefix+"; Suffix: "+suffix+"; NamColor: "+nameColor+"; Displayname: "+displayname+"; Chatprefix: "+chat);
		}
		
		// Register the player with all the collected data
		try {
			Teams.addPlayer(p, prefix, suffix, nameColor, chat, displayname, null, weight);
			return true;
		}catch (Exception e) {
			// If somehow something does no work, send a error message to configure the rank properly.
			pl.getLogger().severe("The player "+p.getName()+" could not be added to a team! Please check your rank configuration!");
		}
		return false;
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
