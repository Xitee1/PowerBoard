package de.xite.scoreboard.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.fusesource.jansi.Ansi;

import de.xite.scoreboard.main.Main;

public class SelfCheck {
	public static boolean hasErrors = false;
	public static boolean hasFatalErrors = false;
	
	static String r = "";
	static String y = "";
	static String g = "";
	static String w = "";
	
	static Main pl = Main.pl;
	
	public static boolean check() {
		try {
			r = Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString();
			y = Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString();
			g = Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString();
			w = Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString();
		}catch (NoClassDefFoundError  e) {
		}
		
		
		FileConfiguration cfg = pl.getConfig();
		
		//---Begin the check---/
		pl.getLogger().info(y+y+"self-check -> Checking for configuration errors.."+w);
		hasErrors = false; // reset if there were any errors in previos scan
		
		//Updated config warnings
	    if(cfg.isInt("placeholder.money-digits")) {
	    	pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please change ->'money-digits' to 'money-decimals'<- in the ->'placeholder'<- section! Support for the old config will drop in version 4.5"+w);
	    	hasErrors = true;
	    }
	    if(cfg.getConfigurationSection("ranks.luckperms") != null) {
	    	pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please change ->'luckperms' to 'luckperms-api'<- in the ->'ranks'<- section! Support for the old config will drop in version 4.5"+w);
	    	hasErrors = true;
	    }
	    if(cfg.isBoolean("chat.prefixes")) {
	    	pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please change ->'prefixes' to 'ranks'<- in the ->'chat'<- section! Support for the old config will drop in version 4.5"+w);
	    	hasErrors = true;
	    }
	    if(cfg.isBoolean("chat.enable")) {
	    	
	    	pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please change ->'enable' to 'ranks'<- in the ->'chat'<- section! Support for the old config will drop in version 4.5"+w);
	    	hasErrors = true;
	    }
	    if(!cfg.isBoolean("chat.allowHexColors")) {
	    	pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please add ->'allowHexColors'<- in the ->'chat'<- section in your config.yml to allow players to use hex colors."+w);
	    	hasErrors = true;
	    }
	    
	    //Check for errors
	    if(!cfg.isBoolean("scoreboard")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'scoreboard' in the section '' is not valid!"+w);
	    	hasErrors = true;
	    }
	    if(!cfg.isString("scoreboard-default")) {
			pl.getLogger().warning("--- WARNING ---");
			pl.getLogger().warning("Please add the config option");
			pl.getLogger().warning("\"scoreboard-default: 'scoreboard' # The scoreboard that will be set after a player joins the server\"");
			pl.getLogger().warning("to your config.yml below \"scoreboard: true\"");
			pl.getLogger().warning("--- WARNING ---");
	    	hasErrors = true;
	    }
	    if(!cfg.isBoolean("tablist.text")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'text' in the section 'tablist' is not valid!"+w);
	    	hasErrors = true;
	    }
	    if(!cfg.isBoolean("tablist.ranks")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'ranks' in the section 'tablist' is not valid!"+w);
	    	hasErrors = true;
	    }
	    if(!cfg.isBoolean("chat.ranks")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'ranks' in the section 'chat' is not valid!"+w);
	    	hasErrors = true;
	    }
	    if(!cfg.isString("chat.colorperm")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'colorperm' in the section 'chat' is not valid!"+w);
	    	hasErrors = true;
	    }

	    
	    if(!cfg.isString("ranks.permissionsystem")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'permissionsystem' in the section 'ranks' is not valid!"+w);
	    	hasErrors = true;
	    }
	    if(!cfg.isBoolean("ranks.luckperms-api.enable")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'enable' in the section 'ranks.luckperms-api' is not valid!"+w);
	    	hasErrors = true;
	    }
	    if(!cfg.isString("ranks.luckperms-api.chat-layout")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'chat-layout' in the section 'ranks.luckperms-api' is not valid!"+w);
	    	hasErrors = true;
	    }
	    
	    
	    //Check ranks
	    for(String s : cfg.getConfigurationSection("ranks.list").getValues(false).keySet()) {
	    	pl.getLogger().info(y+"self-check -> Checking rank '"+s+"'"+w);
	    	if(!cfg.isString("ranks.list."+s+".permission")) {
	    		pl.getLogger().severe(r+"self-check -> Your rank named '"+s+"' has no valid permission set!"+w);
	    		hasFatalErrors = true;
		    }
	    	if(!cfg.isString("ranks.list."+s+".prefix")) {
	    		pl.getLogger().severe(r+"self-check -> Your rank named '"+s+"' has no valid prefix!"+w);
	    		hasFatalErrors = true;
		    }
	    	if(!cfg.isString("ranks.list."+s+".suffix")) {
	    		pl.getLogger().severe(r+"self-check -> Your rank named '"+s+"' has no valid suffix!"+w);
	    		hasFatalErrors = true;
		    }
	    	if(!cfg.isString("ranks.list."+s+".nameColor")) {
		    	String s2 = cfg.getString("ranks.list."+s+".nameColor");
		    	if(s.length() > 2) {
		    		pl.getLogger().severe(r+"self-check -> Your rank named '"+s+"' has no valid name-color!"+w);
		    		hasFatalErrors = true;
		    	}else
		    	if(s2.length() == 2) {
		    		if(!s2.startsWith("&")) {
		    			pl.getLogger().severe(r+"self-check -> Your rank named '"+s+"' has no valid name-color!"+w);
		    			hasFatalErrors = true;
		    		}
		    	}else
		    	if(s2.length() != 1) {
		    		pl.getLogger().severe(r+"self-check -> Your rank named '"+s+"' has no valid name-color!"+w);
		    		hasFatalErrors = true;
		    	}
		    }
	    	if(!cfg.isString("ranks.list."+s+".chatPrefix")) {
	    		pl.getLogger().severe(r+"self-check -> Your rank named '"+s+"' has no valid chat prefix!"+w);
	    		hasFatalErrors = true;
		    }
	    	if(!cfg.isString("ranks.list."+s+".placeholder-name")) {
	    		pl.getLogger().warning(r+"self-check -> Your rank named '"+s+"' has no valid placeholder name!"+w);
	    		hasErrors = true;
		    }
	    }
	    if(!cfg.isString("placeholder.time-format")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'time-format' in the section 'placeholder' is not valid!"+w);
	    	hasErrors = true;
	    }
	    if(!cfg.isString("placeholder.date-format")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'date-format' in the section 'placeholder' is not valid!"+w);
	    	hasErrors = true;
	    }
	    if(!cfg.isInt("placeholder.money-decimals")) {
	    	pl.getLogger().warning(r+"self-check -> The setting 'money-decimals' in the section 'placeholder' is not valid!"+w);
	    	hasErrors = true;
	    }
	    // Send the result to the console
	    if(!(hasErrors || hasFatalErrors))
	    	pl.getLogger().info(g+"self-check -> No errors were found!"+w);
	    
	    if(hasFatalErrors)
	    	return true;
		
		// Send every 5 minutes the message that there are errors and you should fix them
	    if(hasErrors) {
	    	pl.getLogger().severe(r+"self-check -> Errors were found. These are no fatal errors, so normally the plugin should still work. But you should fix them soon!"+w);
		    Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
				@Override
				public void run() {
					check();
				}
			}, 20*60*5);
	    }

	    return false;
	}
	
}
