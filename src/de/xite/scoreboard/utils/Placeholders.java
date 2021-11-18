package de.xite.scoreboard.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.xite.scoreboard.api.CustomPlaceholders;
import de.xite.scoreboard.depend.VaultAPI;
import de.xite.scoreboard.main.ExternalPlugins;
import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.versions.version_1_08;
import de.xite.scoreboard.versions.version_1_09;
import de.xite.scoreboard.versions.version_1_10;
import de.xite.scoreboard.versions.version_1_11;
import de.xite.scoreboard.versions.version_1_12;
import de.xite.scoreboard.versions.version_1_13;
import de.xite.scoreboard.versions.version_1_14;
import de.xite.scoreboard.versions.version_1_15;
import de.xite.scoreboard.versions.version_1_16;
import de.xite.scoreboard.versions.version_1_17;
import me.clip.placeholderapi.PlaceholderAPI;

public class Placeholders {
	static PowerBoard pl = PowerBoard.pl;
	static boolean tps = false;
	public static ArrayList<String> deprecatedWarning = new ArrayList<>();
	
	// All registered custom placeholders
	public static ArrayList<CustomPlaceholders> ph = new ArrayList<>();
	
	public static String replace(Player p, String s) {
		// Import placeholders from APIs
		for(CustomPlaceholders ph : ph)
			s = ph.replace(p, s);
		
		// Placeholder API if PAPI prefered
		if(ExternalPlugins.hasPapi && !pl.getConfig().getBoolean("prefer-plugin-placeholders"))
  			s = PlaceholderAPI.setPlaceholders(p, s);
		
		
		
		// ---- Deprecated ---- //
  		if(s.contains("%ping%"))
  			s = s.replace("%ping%", "%player_ping%");
  		if(s.contains("%money%"))
  			s = s.replace("%money%", "%player_money%");
  		if(s.contains("%rank%"))
  			s = s.replace("%rank%", "%player_rank%");
  		if(s.contains("%name%"))
  			s = s.replace("%name%", "%player_name%");
  		if(s.contains("%loc_x%"))
  			s = s.replace("%loc_x%", "%player_loc_x%");
  		if(s.contains("%loc_y%"))
  			s = s.replace("%loc_y%", "%player_loc_y%");
  		if(s.contains("%loc_z%"))
  			s = s.replace("%loc_z%", "%player_loc_z%");
  		if(s.contains("%world%"))
  			s = s.replace("%world%", "%player_world%");
		if(s.contains("%playeronline%"))
			s = s.replace("%playeronline%", "%server_online_players%");
  		if(s.contains("%playermax%"))
  			s = s.replace("%playermax%", "%server_max_players%");
		
		// ---- Placeholders from Scoreboard Plugin ---- //
		// TPS
		if(s.contains("%tps%")) {
			if(!tps) {
				tps = true;
				// Start TPS checker
				Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new TPS(), 100L, 1L);
				TPS.start();
			}
			s = s.replace("%tps%", ""+PowerBoard.round(TPS.currentTPS, 1));
		}
		// Players on server
		if(s.contains("%server_online_players%"))
  			s = s.replace("%server_online_players%", ""+Bukkit.getOnlinePlayers().size());
		// Max players on server
  		if(s.contains("%server_max_players%"))
  			s = s.replace("%server_max_players%", ""+Bukkit.getMaxPlayers());
  		// Player name
  		if(s.contains("%player_name%")) {
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
  		// Player health
  		if(s.contains("%player_health%"))
  			s = s.replace("%player_health%", ""+(int)p.getHealth());
  		// Player saturation
  		if(s.contains("%player_saturation%"))
  			s = s.replace("%player_saturation%", ""+(int)p.getSaturation());
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
  			if(teams != null) {
  				if(teams.getPlaceholderName() == null) {
  					s = s.replace("%player_rank%", teams.getPrefix());
  				}else {
  					s = s.replace("%player_rank%", teams.getPlaceholderName());
  				}
  				
  			}
  		}
  		// Money
  		if(s.contains("%player_money%")) {
  			if(ExternalPlugins.hasVault == true) {
  				int decimals = pl.getConfig().getInt("placeholder.money-decimals");
  				// If the decimals are set to 0, cast it to int to remove the '.0'
  				if(decimals != 0) {
  					s = s.replace("%player_money%", ""+PowerBoard.round(VaultAPI.econ.getBalance(p), decimals));
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
  			if(PowerBoard.getBukkitVersion().equals(new Version("1.8"))) {
  				ping = version_1_08.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().equals(new Version("1.9"))) {
  				ping = version_1_09.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().equals(new Version("1.10"))) {
  				ping = version_1_10.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().equals(new Version("1.11"))) {
  				ping = version_1_11.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().equals(new Version("1.12"))) {
  				ping = version_1_12.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().equals(new Version("1.13"))) {
  				ping = version_1_13.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().equals(new Version("1.14"))) {
  				ping = version_1_14.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().equals(new Version("1.15"))) {
  				ping = version_1_15.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().equals(new Version("1.16"))) {
  				ping = version_1_16.getPing(p);
  			}else if(PowerBoard.getBukkitVersion().equals(new Version("1.17"))) {
  				ping = version_1_17.getPing(p);
  			}
  			if(ping > 999) {
  				s = s.replace("%player_ping%", ChatColor.RED+"999+");
  			}else
  				s = s.replace("%player_ping%", ping+"");
  		}
  		
  		
  		
  			
  		// -------------------------------------//
  		// Replace colors (MC colorcodes)
  		s = ChatColor.translateAlternateColorCodes('&', s);
  		// Replace colors (HEX) - only 1.16+
  		s = PowerBoard.translateHexColor(s);
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
}
