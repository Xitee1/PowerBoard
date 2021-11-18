package de.xite.scoreboard.utils;

import org.bukkit.Bukkit;
import org.fusesource.jansi.Ansi;

import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.xite.scoreboard.main.PowerBoard;

public class SelfCheck {
	public static boolean hasErrors = false;
	public static boolean hasFatalErrors = false;
	
	static String r = "";
	static String y = "";
	static String g = "";
	static String w = "";
	
	static PowerBoard pl = PowerBoard.pl;
	
	public static boolean check() {
		try {
			r = Ansi.ansi().fg(Ansi.Color.RED).boldOff().toString();
			y = Ansi.ansi().fg(Ansi.Color.YELLOW).boldOff().toString();
			g = Ansi.ansi().fg(Ansi.Color.GREEN).boldOff().toString();
			w = Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString();
		}catch (NoClassDefFoundError e) {}
		
		Yaml cfg = new Yaml("config.yml", PowerBoard.pluginfolder);
		cfg.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS);
		//---Begin the check---/
		pl.getLogger().info(y+"self-check -> Checking for configuration errors.."+w);
		hasErrors = false; // reset if there were any errors in previos scan
		//Updated config warnings
		if(!cfg.contains("placeholder.money-decimals")) {
			pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please change ->'money-digits' to 'money-decimals'<- to the ->'placeholder'<- section!"+w);
			cfg.set("placeholder.money-decimals", 2);
			hasErrors = true;
		}
		if(cfg.contains("ranks.luckperms")) {
	    	pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please change ->'luckperms' to 'luckperms-api'<- to the ->'ranks'<- section!"+w);
	    	hasErrors = true;
		}
		if(cfg.contains("chat.prefixes")) {
			pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please change ->'prefixes' to 'ranks'<- to the ->'chat'<- section!"+w);
			hasErrors = true;
		}
		if(cfg.contains("chat.enable")) {
			pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please change ->'enable' to 'ranks'<- to the ->'chat'<- section!"+w);
			hasErrors = true;
		}
		if(!cfg.contains("chat.allowHexColors")) {
			pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please add ->'allowHexColors: true'<- to the ->'chat'<- section."+w);
			hasErrors = true;
		}
		if(!cfg.contains("placeholder.prefer-plugin-placeholders")) {
			pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! Please add ->'prefer-plugin-placeholders: true'<- to the ->'placeholder'<- section in your config.yml. More infos are in the changelog."+w);
			hasErrors = true;
		}
		if(!cfg.contains("scoreboard-default")) {
			pl.getLogger().warning("--- UPDATE ---");
			pl.getLogger().warning("Please add the config option");
			pl.getLogger().warning("\"scoreboard-default: 'scoreboard' # The scoreboard that will be set after a player joins the server\"");
			pl.getLogger().warning("to your config.yml below \"scoreboard: true\"");
			pl.getLogger().warning("--- UPDATE ---");
			hasErrors = true;
		}
		if(!cfg.contains("placeholder.world-names")) {
			pl.getLogger().warning(r+"self-check -> Your config.yml is out of date! A new config option was added. Have look at the changelogs to see how to add it."+w);
			hasErrors = true;
		}
		//Check for errors
		/*if(!cfg.isBoolean("scoreboard")) {
			pl.getLogger().warning(r+"self-check -> The setting 'scoreboard' in the section '' is not valid!"+w);
			hasErrors = true;
		}
		if(!cfg.isString("scoreboard-default")) {
			pl.getLogger().warning(r+"self-check -> The setting 'scoreboard-default' is not valid!"+w);
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
		if(!cfg.isBoolean("chat.allowHexColors")) {
			pl.getLogger().warning(r+"self-check -> The setting 'allowHexColors' in the section 'chat' is not valid!"+w);
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
		if(cfg.getConfigurationSection("ranks.list") != null) {
			for(String s : cfg.getConfigurationSection("ranks.list").getValues(false).keySet()) {
				if(PowerBoard.debug)
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
					}else {
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
		}
		if(!cfg.isBoolean("placeholder.prefer-plugin-placeholders")) {
			pl.getLogger().warning(r+"self-check -> The setting 'prefer-plugin-placeholders' in the section 'placeholder' is not valid!"+w);
			hasErrors = true;
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
		}*/
		// Send the result to the console
		if(!(hasErrors || hasFatalErrors))
			pl.getLogger().info(g+"self-check -> No errors were found!"+w);
		
		if(hasFatalErrors)
			return true;
		
		// Send every 30 minutes the message that there are errors and you should fix them
		if(hasErrors) {
			pl.getLogger().severe(r+"self-check -> Errors were found. These are no fatal errors, so normally the plugin should still work. But you should fix them soon!"+w);
			Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
				@Override
				public void run() {
					check();
				}
			}, 20*60*30);
		}
		
		return false;
	}
	
}
