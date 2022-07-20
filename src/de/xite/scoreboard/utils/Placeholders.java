package de.xite.scoreboard.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.xite.scoreboard.api.CustomPlaceholders;
import de.xite.scoreboard.depend.VaultAPI;
import de.xite.scoreboard.main.ExternalPlugins;
import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.ranks.RankManager;
import de.xite.scoreboard.utils.iridiumcolorapi.IridiumColorAPI;
import de.xite.scoreboard.versions.version_1_08;
import de.xite.scoreboard.versions.version_1_09;
import de.xite.scoreboard.versions.version_1_10;
import de.xite.scoreboard.versions.version_1_11;
import de.xite.scoreboard.versions.version_1_12;
import de.xite.scoreboard.versions.version_1_13;
import de.xite.scoreboard.versions.version_1_14;
import de.xite.scoreboard.versions.version_1_15;
import de.xite.scoreboard.versions.version_1_16;
import me.clip.placeholderapi.PlaceholderAPI;

public class Placeholders {
	static PowerBoard pl = PowerBoard.pl;
	public static String hexColorBegin = "", hexColorEnd = ""; // hex color Syntax
	
	// All registered custom placeholders
	public static ArrayList<CustomPlaceholders> ph = new ArrayList<>();
	
	public static String replace(Player p, String s) {
		// Import placeholders from APIs
		for(CustomPlaceholders ph : ph)
			s = ph.replace(p, s);
		
		// Placeholder API if PAPI prefered
		if(ExternalPlugins.hasPapi && !pl.getConfig().getBoolean("prefer-plugin-placeholders"))
			try {
				s = PlaceholderAPI.setPlaceholders(p, s);
			}catch (Exception e) {
				if(PowerBoard.debug) {
					pl.getLogger().severe("Could not replace PAPI Placeholders in String "+s+". This is NOT a bug of PowerBoard!"
							+ "Instead it is caused by an external plugin that provides placeholders to PAPI."
							+ "It just says PowerBoard there, because PB requests these placeholders from PAPI and therefore is the root cause.");
					e.printStackTrace();
				}
			}
  			
		
		// ---- Deprecated ---- //
  		if(s.contains("%tps%")) {
  			s = s.replace("%tps%", "%server_tps%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %tps% to %server_tps% - The old placeholder will be removed in v3.7");
  		}
  			
  		if(s.contains("%ping%")) {
  			s = s.replace("%ping%", "%player_ping%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %ping% to %player_ping% - The old placeholder will be removed in v3.7");
  		}
  		if(s.contains("%money%")) {
  			s = s.replace("%money%", "%player_money%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %money% to %player_money% - The old placeholder will be removed in v3.7");
  		}
  		if(s.contains("%rank%")) {
  			s = s.replace("%rank%", "%player_rank%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %rank% to %player_rank% - The old placeholder will be removed in v3.7");
  		}
  		if(s.contains("%name%")) {
  			s = s.replace("%name%", "%player_name%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %name% to %player_name% - The old placeholder will be removed in v3.7");
  		}
  		if(s.contains("%loc_x%")) {
  			s = s.replace("%loc_x%", "%player_loc_x%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %loc_x% to %player_loc_x% - The old placeholder will be removed in v3.7");
  		}
  		if(s.contains("%loc_y%")) {
  			s = s.replace("%loc_y%", "%player_loc_y%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %loc_y% to %player_loc_y% - The old placeholder will be removed in v3.7");
  		}
  		if(s.contains("%loc_z%")) {
  			s = s.replace("%loc_z%", "%player_loc_z%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %loc_z% to %player_loc_z% - The old placeholder will be removed in v3.7");
  		}
  		if(s.contains("%world%")) {
  			s = s.replace("%world%", "%player_world%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %world% to %player_world% - The old placeholder will be removed in v3.7");
  		}
		if(s.contains("%playeronline%")) {
			s = s.replace("%playeronline%", "%server_online_players%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %playeronline% to %server_online_players% - The old placeholder will be removed in v3.7");
  		}
  		if(s.contains("%playermax%")) {
  			s = s.replace("%playermax%", "%server_max_players%");
  			pl.getLogger().warning("You are using deprecated placeholders! Change %playermax% to %server_max_players% - The old placeholder will be removed in v3.7");
  		}
		
		// ---- Placeholders from Scoreboard Plugin ---- //
		// TPS
		if(s.contains("%server_tps%"))
			s = s.replace("%server_tps%", ""+TPS.getTPS());
		
		// Players on server
		if(s.contains("%server_online_players%"))
  			s = s.replace("%server_online_players%", ""+Bukkit.getOnlinePlayers().size());
		
		// Max players on server
  		if(s.contains("%server_max_players%"))
  			s = s.replace("%server_max_players%", ""+Bukkit.getMaxPlayers());
  		
  		// Player name
  		if(s.contains("%player_name%")) {
  			s = s.replace("%player_name%", p.getName());
  		}
  		
  		// Player name
  		if(s.contains("%player_displayname%")) {
  			s = s.replace("%player_name%", p.getDisplayName());
  		}
  			
  		// World from the player
  		if(s.contains("%player_world%")) {
  			String world = p.getWorld().getName();
  			if(pl.getConfig().isString("placeholder.world-names."+world))
  				world = ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("placeholder.world-names."+world));
  			s = s.replace("%player_world%", world);
  		}
  		// Location
  		if(s.contains("%player_loc_x%"))
  			s = s.replace("%player_loc_x%", p.getLocation().getBlockX()+"");
  		if(s.contains("%player_loc_y%"))
  			s = s.replace("%player_loc_y%", p.getLocation().getBlockY()+"");
  		if(s.contains("%player_loc_z%"))
  			s = s.replace("%player_loc_z%", p.getLocation().getBlockZ()+"");
  		
  		// Player food level
  		if(s.contains("%player_food%"))
  			s = s.replace("%player_food%", ""+(int)p.getFoodLevel());
  		
  		// Player saturation
  		if(s.contains("%player_saturation%"))
  			s = s.replace("%player_saturation%", ""+(int)p.getSaturation());
  		
  		// Player health
  		if(s.contains("%player_health%"))
  			s = s.replace("%player_health%", ""+(int)p.getHealth());
  		
  		// Server time
  		if(s.contains("%time%")) {
  			String format = pl.getConfig().getString("placeholder.time-format");
  			if(format != null) {
  				try {
  					s = s.replace("%time%", new SimpleDateFormat(format).format(new Date()));
  				}catch (Exception e2) {
  					pl.getLogger().severe("Invalid time format! Please check your placeholder settings in your config.yml!");
				}
  			}else
  				s = s.replace("%time%", new SimpleDateFormat("HH:mm").format(new Date()));
  		}
  		
  		// Server date
  		if(s.contains("%date%")) {
  			String format = pl.getConfig().getString("placeholder.date-format");
  			if(format != null) {
  				try {
  					s = s.replace("%date%", new SimpleDateFormat(format).format(new Date()));
  				}catch (Exception e2) {
  					pl.getLogger().severe("Invalid date format! Please check your placeholder settings in your config.yml!");
				}
  			}else
  				s = s.replace("%date%", new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
  		}
  		
  		// Rank displayname
  		if(s.contains("%player_rank%")) {
  			Teams teams = Teams.get(p);
			/*if(teams == null) {
				RankManager.register(p);
				teams = Teams.get(p);
			}*/
  			if(teams != null) {
  				if(teams.getRankDisplayName() == null) {
  					s = s.replace("%player_rank%", teams.getPrefix());
  				}else {
  					s = s.replace("%player_rank%", teams.getRankDisplayName());
  				}
  			}
  		}
  		
  		// Money
  		if(s.contains("%player_money%")) {
  			if(ExternalPlugins.hasVault == true) {
  				int decimals = pl.getConfig().getInt("placeholder.money-decimals");
  				// If the decimals are set to 0, cast it to int to remove the '.0'
  				if(decimals != 0) {
  					s = s.replace("%player_money%", ""+MathUtils.round(VaultAPI.econ.getBalance(p), decimals));
  				}else
  					s = s.replace("%player_money%", ""+((int) VaultAPI.econ.getBalance(p)));
  			}else {
  				pl.getLogger().severe("Could not get the player's money because you haven't Vault installed or set up! You need Vault and a money system that supports Vault on your server!");
  				s = s.replace("%player_money%", "Error: See console");
  			}
  		}
  		
  		// Memory
  		try {
  	  		if(s.contains("%mem_total%"))
  	  			s = s.replace("%mem_total%", getReadableSize((int) getTotalMemory())+"");
  	  		if(s.contains("%mem_free%"))
  	  			s = s.replace("%mem_free%", getReadableSize((int) getFreeMemory())+"");
  	  		if(s.contains("%mem_used%"))
  	  			s = s.replace("%mem_used%", getReadableSize((int) getUsedMemory())+"");
  	  		if(s.contains("%mem_max%"))
  	  			s = s.replace("%mem_max%", getReadableSize((int) getMaxMemory())+"");
  		}catch (Exception e) {pl.getLogger().severe("Failed to get memory informations! This is not a bug with the plugin - please contact your server-hoster.");}

  		// Ping
  		if(s.contains("%player_ping%")) {
  			int ping = 0;
  			
  			if(PowerBoard.getBukkitVersion().compareTo(new Version("1.17")) >= 0) {
  				ping = p.getPing();
  			}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.16")) >= 0) {
  				ping = version_1_16.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.15")) >= 0) {
  				ping = version_1_15.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.14")) >= 0) {
  				ping = version_1_14.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.13")) >= 0) {
  				ping = version_1_13.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.12")) >= 0) {
  				ping = version_1_12.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.11")) >= 0) {
  				ping = version_1_11.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.10")) >= 0) {
  				ping = version_1_10.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.9")) >= 0) {
  				ping = version_1_09.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.8")) >= 0) {
  				ping = version_1_08.getPing(p);
  			}else
				pl.getLogger().severe("You are using a unsupported Minecraft version!");
  			
  			if(ping > 999) {
  				s = s.replace("%player_ping%", ChatColor.RED+"999+");
  			}else
  				s = s.replace("%player_ping%", ping+"");
  		}
  			
  		// -------------------------------------//
  		// Replace colors (MC colorcodes)
  		s = ChatColor.translateAlternateColorCodes('&', s);
  		// Replace colors (HEX) - only 1.16+
  		s = translateHexColor(s);
  		//s = IridiumColorAPI.process(s);
  		// Replace PAPI if plugin prefered
  		if(ExternalPlugins.hasPapi && pl.getConfig().getBoolean("prefer-plugin-placeholders"))
  			s = PlaceholderAPI.setPlaceholders(p, s);
  		
		return s;
	}
   
	public static long getTotalMemory() { return Runtime.getRuntime().totalMemory() / 1048576L; }
	public static long getFreeMemory() { return Runtime.getRuntime().freeMemory() / 1048576L; }
	public static long getUsedMemory() { return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L; }
	public static long getMaxMemory() { return Runtime.getRuntime().maxMemory() / 1048576L; }
	public static String getReadableSize(int size){
	    String s = "";
	    size *= 1024; // Because we get the size in mb and not kb
	    
	    double m = size/1024.0;
	    double g = size/1048576.0;
	    double t = size/1073741824.0;

	    if (t > 1) {
	    	 DecimalFormat dec = new DecimalFormat("0.00");
	        s = dec.format(t).concat("TB");
	    } else if (g > 1) {
	    	 DecimalFormat dec = new DecimalFormat("0.00");
	        s = dec.format(g).concat("GB");
	    } else if (m > 1) {
	    	 DecimalFormat dec = new DecimalFormat("0");
	        s = dec.format(m).concat("MB");
	    } else {
	    	 DecimalFormat dec = new DecimalFormat("0");
	        s = dec.format(size).concat("KB");
	    }
	    return s;
	}
	
	
    // Credit to https://www.spigotmc.org/threads/hex-color-code-translate.449748/#post-3867804
    public final static char COLOR_CHAR = ChatColor.COLOR_CHAR;
    public static String translateHexColor(String message) {
    	try {
            final Pattern hexPattern = Pattern.compile(hexColorBegin + "([A-Fa-f0-9]{6})" + hexColorEnd);
            Matcher matcher = hexPattern.matcher(message);
            StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                        + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                        + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                        + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                        );
            }
            return matcher.appendTail(buffer).toString();
    	}catch (Exception e) {
    		pl.getLogger().severe("You have an invalid HEX-Color-Code! Please check the syntax! Text: "+message);
    		return "InvalidHexColor";
		}
    }
}
