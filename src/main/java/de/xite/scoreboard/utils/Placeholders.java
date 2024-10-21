package de.xite.scoreboard.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.xite.scoreboard.versions.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.xite.scoreboard.api.CustomPlaceholders;
import de.xite.scoreboard.depend.VaultAPI;
import de.xite.scoreboard.main.ExternalPlugins;
import de.xite.scoreboard.main.PowerBoard;
import me.clip.placeholderapi.PlaceholderAPI;

public class Placeholders {
	static PowerBoard pl = PowerBoard.pl;
	public static String hexColorBegin = "", hexColorEnd = ""; // hex color Syntax

	// All registered custom placeholders
	public static Set<CustomPlaceholders> ph = new HashSet<>();

	private static final Map<String, String> deprecatedPlaceholders = new HashMap<String, String>(11, 1) {
		{
			put("%tps%", "%player_tps%");
			put("%ping%", "%player_ping%");
			put("%money%", "%player_money%");
			put("%rank%", "%player_rank%");
			put("%name%", "%player_name%");
			put("%loc_x%", "%player_loc_x%");
			put("%loc_y%", "%player_loc_y%");
			put("%loc_z%", "%player_loc_z%");
			put("%world%", "%player_world%");
			put("%playeronline%", "%server_online_players%");
			put("%playermax%", "%server_max_players%");
		}
	};
	
	public static String replace(Player p, String s) {
		// Replace all custom placeholders first
		for(CustomPlaceholders ph : ph) {
			String output = ph.replace(p, s);
			if(output == null) {
				PowerBoard.getRateLimitedLogger().addSevere(
						"Output of CustomPlaceholders#replace cannot be null! This is NOT a bug in PowerBoard! " +
								"It is caused by a CustomPlaceholder that was registered by another plugin.",
						true
				);
			}else
				s = output;
		}

		// Replace all PAPI placeholders
		boolean preferPBPlaceholders = pl.getConfig().getBoolean("placeholder.prefer-plugin-placeholders");
		if(!preferPBPlaceholders && ExternalPlugins.hasPapi)
			try {
				s = PlaceholderAPI.setPlaceholders(p, s);
			}catch (Exception e) {
				PowerBoard.getRateLimitedLogger().addSevere(
						"Could not replace PAPI Placeholders in String "+s+". This is NOT a bug of PowerBoard!"
								+ "Instead it is caused by an external plugin that provides placeholders to PAPI."
								+ "It just says PowerBoard there, because PB requests these placeholders from PAPI and therefore is the root cause.",
						true
				);
				if(PowerBoard.debug) {
					e.printStackTrace();
				}
			}
  			
		
		// ---- Deprecated ---- //
		for(Map.Entry<String, String> ph : deprecatedPlaceholders.entrySet()) {
			String oldPH = ph.getKey();
			String newPH = ph.getValue();
			if(s.contains(oldPH)) {
				s = s.replace(oldPH, newPH);
				PowerBoard.getRateLimitedLogger().addWarn(
						"You are using deprecated placeholders! Change "+oldPH+" to "+newPH+" - The old placeholders will be removed soon.",
						true
				);
			}
		}
		
		// ---- Placeholders from PowerBoard ---- //
		String ph;

		// TPS
		ph = "%server_tps%";
		if(s.contains(ph)) {
			s = s.replace(ph, String.valueOf(PowerBoard.getTPSCalc().getCurrentTPS()));
		}
		
		// Players on server
		ph = "%server_online_players%";
		if(s.contains(ph)) {
			s = s.replace(ph, String.valueOf(Bukkit.getOnlinePlayers().size()));
		}
		
		// Max players on server
		ph = "%server_max_players%";
  		if(s.contains(ph)) {
		    s = s.replace(ph, String.valueOf(Bukkit.getMaxPlayers()));
	    }
  		
  		// Player name
		ph = "%player_name%";
  		if(s.contains(ph)) {
		    s = s.replace(ph, p.getName());
	    }

  		// Player name
		ph = "%player_displayname%";
  		if(s.contains(ph)) {
  			s = s.replace(ph, p.getDisplayName());
  		}

  		// World from the player
		ph = "%player_world%";
  		if(s.contains(ph)) {
  			String world = p.getWorld().getName();
  			if(pl.getConfig().isString("placeholder.world-names."+world))
  				world = pl.getConfig().getString("placeholder.world-names."+world);
  			s = s.replace(ph, world);
  		}

  		// Location
		ph = "%player_loc_x%";
  		if(s.contains(ph))
  			s = s.replace(ph, String.valueOf(p.getLocation().getBlockX()));

		ph = "%player_loc_y%";
  		if(s.contains(ph))
  			s = s.replace(ph, String.valueOf(p.getLocation().getBlockY()));

		ph = "%player_loc_z%";
  		if(s.contains(ph))
  			s = s.replace(ph, String.valueOf(p.getLocation().getBlockZ()));
  		
  		// Player food level
		ph = "%player_food%";
  		if(s.contains(ph))
  			s = s.replace(ph, String.valueOf(p.getFoodLevel()));
  		
  		// Player saturation
		ph = "%player_saturation%";
  		if(s.contains(ph))
  			s = s.replace(ph, String.valueOf((int) p.getSaturation()));
  		
  		// Player health
		ph = "%player_health%";
  		if(s.contains(ph))
  			s = s.replace(ph, String.valueOf((int) p.getHealth()));

  		// Server time
		ph = "%time%";
  		if(s.contains(ph)) {
  			String format = pl.getConfig().getString("placeholder.time-format");
  			if(format != null) {
  				try {
  					s = s.replace(ph, new SimpleDateFormat(format).format(new Date()));
  				}catch (Exception e2) {
  					pl.getLogger().severe("Invalid time format! Please check your placeholder settings in your config.yml!");
				}
  			}else
  				s = s.replace(ph, new SimpleDateFormat("HH:mm").format(new Date()));
  		}
  		
  		// Server date
		ph = "%date%";
  		if(s.contains(ph)) {
  			String format = pl.getConfig().getString("placeholder.date-format");
  			if(format != null) {
  				try {
  					s = s.replace(ph, new SimpleDateFormat(format).format(new Date()));
  				}catch (Exception e2) {
  					pl.getLogger().severe("Invalid date format! Please check your placeholder settings in your config.yml!");
				}
  			}else
  				s = s.replace(ph, new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
  		}
  		
  		// Rank displayname
		ph = "%player_rank%";
  		if(s.contains(ph)) {
  			Teams teams = Teams.get(p);
			/*if(teams == null) {
				RankManager.register(p);
				teams = Teams.get(p);
			}*/
  			if(teams != null) {
  				if(teams.getRankDisplayName() == null) {
  					s = s.replace(ph, teams.getPrefix());
  				}else {
  					s = s.replace(ph, teams.getRankDisplayName());
  				}
  			}else
				  s = s.replace(ph, "unknown");
  		}

		// Player prefix
		ph = "%player_prefix%";
		if(s.contains(ph)) {
			Teams teams = Teams.get(p);
			if(teams != null) {
				s = s.replace(ph, teams.getPrefix());
			}else
				s = s.replace(ph, "unknown");
		}

		// Player prefix
		ph = "%player_suffix%";
		if(s.contains(ph)) {
			Teams teams = Teams.get(p);
			if(teams != null)
				s = s.replace(ph, teams.getSuffix());
		}
  		
  		// Money
		ph = "%player_money%";
  		if(s.contains(ph)) {
  			if(ExternalPlugins.hasVault) {
  				int decimals = pl.getConfig().getInt("placeholder.money-decimals");
  				// If the decimals are set to 0, cast it to int to remove the '.0'
  				if(decimals != 0) {
  					s = s.replace(ph, String.valueOf(MathUtils.round(VaultAPI.getAPI().getBalance(p), decimals)));
  				}else
  					s = s.replace(ph, String.valueOf((int) VaultAPI.getAPI().getBalance(p)));
  			}else {
  				pl.getLogger().severe("Could not get the player's money because you haven't Vault installed or set up! You need Vault and a money system that supports Vault on your server!");
  				s = s.replace(ph, "Error: See console");
  			}
  		}
  		
  		// Memory
  		try {
		    ph = "%mem_total%";
  	  		if(s.contains(ph))
  	  			s = s.replace(ph, getReadableSize((int) getTotalMemory()));

		    ph = "%mem_free%";
  	  		if(s.contains(ph))
  	  			s = s.replace(ph, getReadableSize((int) getFreeMemory()));

		    ph = "%mem_used%";
  	  		if(s.contains(ph))
  	  			s = s.replace(ph, getReadableSize((int) getUsedMemory()));

		    ph = "%mem_max%";
  	  		if(s.contains(ph))
  	  			s = s.replace(ph, getReadableSize((int) getMaxMemory()));
  		}catch (Exception e) {pl.getLogger().severe("Failed to get memory information's! This is not a bug with the plugin - please contact your server-hoster.");}

  		// Ping
		ph = "%player_ping%";
  		if(s.contains(ph)) {
		    int ping = VersionSpecific.current.getPing(p);
  			
  			if(ping > 999) {
  				s = s.replace(ph, ChatColor.RED+"999+");
  			}else
  				s = s.replace(ph, String.valueOf(ping));
  		}
  			
  		// -------------------------------------//
  		// Replace colors (MC color codes)
  		s = ChatColor.translateAlternateColorCodes('&', s);

  		// Replace colors (HEX) - only 1.16+
  		s = translateHexColor(s);

  		//s = IridiumColorAPI.process(s);

  		// Replace PAPI if plugin preferred
  		if(preferPBPlaceholders && ExternalPlugins.hasPapi)
  			s = PlaceholderAPI.setPlaceholders(p, s);
  		
		return s;
	}
   
	public static long getTotalMemory() {
		return Runtime.getRuntime().totalMemory() / 1048576L;
	}

	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory() / 1048576L;
	}

	public static long getUsedMemory() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L;
	}

	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory() / 1048576L;
	}

	public static String getReadableSize(int size){
	    String s;
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
    		pl.getLogger().severe("You have an invalid HEX-Color-Code! Please check the syntax. Enable debug mode for more details. String to convert: "+message);
			if(PowerBoard.debug)
				e.printStackTrace();
    		return "Invalid_Color";
		}
    }
}
