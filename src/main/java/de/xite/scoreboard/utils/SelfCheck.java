package de.xite.scoreboard.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.xite.scoreboard.main.Config;
import de.xite.scoreboard.main.PowerBoard;

public class SelfCheck {
	static PowerBoard pl = PowerBoard.pl;
	
	public static boolean checkConfig() {
		String prefix = "(SelfCheck) config.yml -> ";
		
		try {
			File file = new File(PowerBoard.pluginfolder+"/config.yml");
			if(!file.exists()) {
				pl.getLogger().severe("Skipped SelfCheck -> config.yml does not exist!");
				return false;
			}
			if(!Version.CURRENT.isAtLeast(Version.v1_18)) {
				pl.getLogger().warning("Skipped SelfCheck. Only works on 1.18+.");
				return true;
			}
			
			// If the boolean is true at the end, the plugin will be disabled
			boolean disablePlugin = false;
			
			// If anything has been added, this is true so the config will be saved.
			boolean needUpdate = false;
			
			pl.getLogger().info(" ");
			pl.getLogger().info(prefix+"Loading...");
			
			/* Load all the configs.
			 * We need 3 versions of the config.yml.
			 * 
			 * First, we have the actual config to write to if nessasary.
			 * We just shorten it here for easier access.
			 * 
			 * Second, we have the actual config but without the default settings in it if something is missing.
			 * We need this to check if everything is in there.
			 * 
			 * Third, we have the default config only. Without any user settings.
			 * We need this so we can add a value if something is missing.
			 * 
			 */
			
			// Preserving the comments when making changes
			pl.getConfig().options().parseComments(true);
			
			FileConfiguration cfg = pl.getConfig();
			
			YamlConfiguration cfgNoDefaults = YamlConfiguration.loadConfiguration(file);
			
			// Load the default configurations
			File config_selfcheck = new File(PowerBoard.pluginfolder+"/.config_selfcheck.yml"); // This file is temporary. We delete it at the end of SelfCheck.
			try(InputStream inputStream = pl.getClass().getClassLoader().getResourceAsStream("config.yml");
			     OutputStream outputStream = new FileOutputStream(config_selfcheck)) {
			        int length;
			        byte[] bytes = new byte[1024];
			        // copy data from input stream to output stream
			        while ((length = inputStream.read(bytes)) != -1) {
			            outputStream.write(bytes, 0, length);
			        }

			} catch (IOException ex) {
			    ex.printStackTrace();
			}
			YamlConfiguration cfgDefault = Config.loadConfiguration(config_selfcheck);
			
			
			/*	Here we could just use the "isX" fuctions to do everything with a single for-loop. Could.
			 * 	The reason we don't do that here is because the user could for example by mistake set an option
			 * 	to 'true' instead true, which would make it to a string and cause errors.
			 * 
			 *	The only settings that won't be checked are the ranks list, luckperms-api chat-prefix and placeholder world-names because it is too complicated (at least for me right now).
			 *	But if you are reading this, feel free to implement it.
			 * 
			 */
			
			
			String[] checkBoolean = {
					"scoreboard",
					"tablist.text",
					"tablist.ranks",
					"chat.ranks",
					"chat.allowHexColors",
					"ranks.luckperms-api.enable",
					"ranks.luckperms-api.prefix-suffix-space",
					"placeholder.prefer-plugin-placeholders",
					"update.notification",
					"update.autoupdater",
					"debug"
					};
			
			String[] checkString = {
					"scoreboard-default",
					"tablist.text-default",
					"chat.colorperm",
					"ranks.permissionsystem",
					"ranks.luckperms-api.chat-layout",
					
					"placeholder.time-format",
					"placeholder.date-format",
					"placeholder.hexColorSyntax"
					};
			
			String[] checkInt = {
					"ranks.update-interval",
					"placeholder.money-decimals",
					};
			
			
			for(String s : checkBoolean) {
				// Here we check if the option exists.
				// We re-add it if it is new or the user (accidentally) deleted it.
				if(!cfgNoDefaults.contains(s)) {
					pl.getLogger().warning(prefix+"The setting \""+s+"\" does not exists! Adding..");
					cfg.set(s, cfgDefault.getBoolean(s));
					needUpdate = true;
				}
				// Here we check if it's the correct type
				if(!cfg.isBoolean(s)) {
					pl.getLogger().severe(prefix+"The setting \""+s+"\" is invalid! Please check your config.yml!");
					disablePlugin = true;
				}
			}
			
			for(String s : checkString) {
				if(!cfgNoDefaults.contains(s)) {
					pl.getLogger().warning(prefix+"The setting \""+s+"\" does not exists! Adding..");
					cfg.set(s, cfgDefault.getString(s));
					needUpdate = true;
				}
				if(!cfg.isString(s)) {
					pl.getLogger().severe(prefix+"The setting \""+s+"\" is invalid! Please check your config.yml!");
					disablePlugin = true;
				}
			}
			
			for(String s : checkInt) {
				if(!cfgNoDefaults.contains(s)) {
					pl.getLogger().warning(prefix+"The setting \""+s+"\" does not exists! Adding..");
					cfg.set(s, cfgDefault.getInt(s));
					needUpdate = true;
				}
				if(!cfg.isInt(s)) {
					pl.getLogger().severe(prefix+"The setting \""+s+"\" is invalid! Please check your config.yml!");
					disablePlugin = true;
				}
			}
			
			
			config_selfcheck.delete();
			
			if(needUpdate)
				pl.saveConfig();
			
			pl.getLogger().info(prefix+"Finished!");
			pl.getLogger().info(" ");
			
			if(disablePlugin)
				return false;
			
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public static boolean checkTablist(String name, YamlConfiguration cfg) {
		if(!(cfg.contains("header") || cfg.contains("footer")
				|| cfg.isList("header") || cfg.isList("footer"))) {
			pl.getLogger().severe("You have an error in your Tablist '"+name+"'! Please check it for typing errors. Look closely.");
			return false;
		}
		return true;
	}
}
