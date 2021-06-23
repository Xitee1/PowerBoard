package de.xite.scoreboard.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.xite.scoreboard.api.CustomPlaceholders;
import de.xite.scoreboard.main.Main;
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
	static Main pl = Main.pl;
	static boolean tps = false;
	
	public static String replace(Player p, String s) {
		// Import placeholders from APIs
		for(CustomPlaceholders ph : Main.ph)
			s = ph.replace(p, s);
		if(Main.hasPapi)
  			s = PlaceholderAPI.setPlaceholders(p, s);
		
		// ---- Placeholders from Scoreboard Plugin ---- //
		// TPS
		if(s.contains("%tps%")) {
			if(!tps) {
				tps = true;
				// Start TPS checker
				Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new TPS(), 100L, 1L);
				TPS.start();
			}
			s = s.replace("%tps%", ""+Main.round(TPS.currentTPS, 1));
		}
		// Players on server
		if(s.contains("%server_online_players%"))
  			s = s.replace("%server_online_players%", ""+Bukkit.getOnlinePlayers().size());
		// Max players on server
  		if(s.contains("%server_max_players%"))
  			s = s.replace("%server_max_players%", ""+Bukkit.getMaxPlayers());
  		// Player name
  		if(s.contains("%player_name%"))
  			s = s.replace("%player_name%", p.getName());
  		// World from the player
  		if(s.contains("%player_world%")) {
  			String name = p.getWorld().getName();
  			if(name.equals("world")) {
  				s = s.replace("%player_world%", "Overworld");
  			}else if(name.equals("world_nether")) {
  				s = s.replace("%player_world%", "Nether");
  			}else if(name.equals("world_the_end")) {
  				s = s.replace("%player_world%", "The End");
  			}else
  				s = s.replace("%player_world%", name);
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
  			if(Main.hasVault == true) {
  				double balance = Main.econ.getBalance(p);
  				//config name changed from money-digits to money-decimals - support will drop in v.4.0
  				if(pl.getConfig().getInt("placeholder.money-digits") == 0 && pl.getConfig().getInt("placeholder.money-decimals") != 0) {
  					balance = Main.round(balance, pl.getConfig().getInt("placeholder.money-decimals"));
  				}else {
  					balance = Main.round(balance, pl.getConfig().getInt("placeholder.money-digits"));
  				}
  				
  				s = s.replace("%player_money%", ""+balance);
  			}else {
  				s = s.replace("%player_money%", "Vault is not installed!");
  			}
  		}
  		// Memory
  		try {
  	  		if(s.contains("%mem_total%")) {
  	  			s = s.replace("%mem_total%", getReadableSize((int) getTotalMemory())+"");
  	  		}
  	  		if(s.contains("%mem_free%")) {
  	  			s = s.replace("%mem_free%", getReadableSize((int) getFreeMemory())+"");
  	  		}
  	  		if(s.contains("%mem_used%")) {
  	  			s = s.replace("%mem_used%", getReadableSize((int) getUsedMemory())+"");
  	  		}
  	  		if(s.contains("%mem_max%")) {
  	  			s = s.replace("%mem_max%", getReadableSize((int) getMaxMemory())+"");
  	  		}
  		}catch (Exception e) {
			pl.getLogger().severe("Failed to get memory informations! This is not a bug with the plugin - please contact your server-hoster.");
		}

  		// Ping
  		if(s.contains("%player_ping%")) {
  			int ping = 0;
  			if(Main.getBukkitVersion() <= 18) {
  				ping = version_1_08.getPing(p);
  			}else if(Main.getBukkitVersion() <= 19) {
  				ping = version_1_09.getPing(p);
  			}else if(Main.getBukkitVersion() <= 110) {
  				ping = version_1_10.getPing(p);
  			}else if(Main.getBukkitVersion() <= 111) {
  				ping = version_1_11.getPing(p);
  			}else if(Main.getBukkitVersion() <= 112) {
  				ping = version_1_12.getPing(p);
  			}else if(Main.getBukkitVersion() <= 113) {
  				ping = version_1_13.getPing(p);
  			}else if(Main.getBukkitVersion() <= 114) {
  				ping = version_1_14.getPing(p);
  			}else if(Main.getBukkitVersion() <= 115) {
  				ping = version_1_15.getPing(p);
  			}else if(Main.getBukkitVersion() <= 116) {
  				ping = version_1_16.getPing(p);
  			}else
  				ping = version_1_17.getPing(p);
  			if(ping > 999) {
  				s = s.replace("%player_ping%", ChatColor.RED+"999+");
  			}else {
  				s = s.replace("%player_ping%", ping+"");
  			}
  		}
  		
  		
  		// ---- Deprecated ---- //
  		if(s.contains("%ping%")) {
  			int ping = 0;
  			if(Main.getBukkitVersion() <= 18) {
  				ping = version_1_08.getPing(p);
  			}else if(Main.getBukkitVersion() <= 19) {
  				ping = version_1_09.getPing(p);
  			}else if(Main.getBukkitVersion() <= 110) {
  				ping = version_1_10.getPing(p);
  			}else if(Main.getBukkitVersion() <= 111) {
  				ping = version_1_11.getPing(p);
  			}else if(Main.getBukkitVersion() <= 112) {
  				ping = version_1_12.getPing(p);
  			}else if(Main.getBukkitVersion() <= 113) {
  				ping = version_1_13.getPing(p);
  			}else if(Main.getBukkitVersion() <= 114) {
  				ping = version_1_14.getPing(p);
  			}else if(Main.getBukkitVersion() <= 115) {
  				ping = version_1_15.getPing(p);
  			}else if(Main.getBukkitVersion() <= 116) {
  				ping = version_1_16.getPing(p);
  			}else
  				ping = version_1_17.getPing(p);
  			if(ping > 999) {
  				s = s.replace("%ping%", ChatColor.RED+"999+");
  			}else {
  				s = s.replace("%ping%", ping+"");
  			}
  			pl.getLogger().warning("Please change your config: You are using the placeholder %ping%. This placeholder was changed. Please change it to %player_ping%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
  		}
  		if(s.contains("%money%")) {
  			if(Main.hasVault == true) {
  				double balance = Main.econ.getBalance(p);
  				//config name changed from money-digits to money-decimals - support will drop in v.4.0
  				if(pl.getConfig().getInt("placeholder.money-digits") == 0 && pl.getConfig().getInt("placeholder.money-decimals") != 0) {
  					balance = Main.round(balance, pl.getConfig().getInt("placeholder.money-decimals"));
  				}else {
  					balance = Main.round(balance, pl.getConfig().getInt("placeholder.money-digits"));
  				}
  				
  				s = s.replace("%money%", ""+balance);
  			}else {
  				s = s.replace("%money%", "Vault is not installed!");
  			}
  			pl.getLogger().warning("Please change your config: You are using the placeholder %money%. This placeholder was changed. Please change it to %player_money%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
  		}
  		if(s.contains("%rank%")) {
  			Teams teams = Teams.get(p);
  			if(teams != null) {
  				if(teams.getPlaceholderName() == null) {
  					s = s.replace("%rank%", teams.getPrefix());
  				}else {
  					s = s.replace("%rank%", teams.getPlaceholderName());
  				}
  				
  			}
  			pl.getLogger().warning("Please change your config: You are using the placeholder %rank%. This placeholder was changed. Please change it to %player_rank%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
  		}
  		if(s.contains("%name%")) {
  			s = s.replace("%name%", p.getName());
  			pl.getLogger().warning("Please change your config: You are using the placeholder %name%. This placeholder was changed. Please change it to %player_name%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
  		}
  			
  		if(s.contains("%loc_x%")) {
  			s = s.replace("%loc_x%", p.getLocation().getBlockX()+"");
  			pl.getLogger().warning("Please change your config: You are using the placeholder %loc_x%. This placeholder was changed. Please change it to %player_loc_x%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
  		}
  			
  		if(s.contains("%loc_y%")) {
  			s = s.replace("%loc_y%", p.getLocation().getBlockY()+"");
  			pl.getLogger().warning("Please change your config: You are using the placeholder %loc_y%. This placeholder was changed. Please change it to %player_loc_y%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
  		}
  			
  		if(s.contains("%loc_z%")) {
  			s = s.replace("%loc_z%", p.getLocation().getBlockZ()+"");
  			pl.getLogger().warning("Please change your config: You are using the placeholder %loc_z%. This placeholder was changed. Please change it to %player_loc_z%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
  		}
  			
  		if(s.contains("%world%")) {
  			String name = p.getWorld().getName();
  			if(name.equals("world")) {
  				s = s.replace("%world%", "Overworld");
  			}else if(name.equals("world_nether")) {
  				s = s.replace("%world%", "Nether");
  			}else if(name.equals("world_the_end")) {
  				s = s.replace("%world%", "The End");
  			}else
  				s = s.replace("%world%", name);
  			pl.getLogger().warning("Please change your config: You are using the placeholder %world%. This placeholder was changed. Please change it to %player_world%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
  		}
		if(s.contains("%playeronline%")) {
			s = s.replace("%playeronline%", ""+Bukkit.getOnlinePlayers().size());
			pl.getLogger().warning("Please change your config: You are using the placeholder %playeronline%. This placeholder was changed. Please change it to %server_online_players%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
		}
  		if(s.contains("%playermax%")) {
  			s = s.replace("%playermax%", ""+Bukkit.getMaxPlayers());
  			pl.getLogger().warning("Please change your config: You are using the placeholder %playermax%. This placeholder was changed. Please change it to %server_max_players%."
  					+ " Tip: Use Notepad++ to easily replace all of them.");
  		}
  			
  		// -------------------------------------//
  		// Replace colors (MC colorcodes)
  		s = ChatColor.translateAlternateColorCodes('&', s);
  		// Replace colors (HEX) - only 1.16+
  		s = Main.translateHexColor(s);
  		
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
