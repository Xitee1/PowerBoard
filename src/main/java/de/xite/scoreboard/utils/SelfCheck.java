package de.xite.scoreboard.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.xite.scoreboard.main.Config;
import de.xite.scoreboard.main.PowerBoard;

public class SelfCheck {
	static PowerBoard pl = PowerBoard.pl;
	
	public static boolean checkConfig() {
		String prefix = "(SelfCheck) config.yml -> ";
		
		try {
			boolean allowModify = false;

			File file = new File(PowerBoard.pluginfolder+"/config.yml");
			if(!file.exists()) {
				pl.getLogger().severe("Skipped SelfCheck -> config.yml does not exist!");
				return false;
			}

			if(Version.CURRENT.isAtLeast(Version.v1_18))
				allowModify = true;
			
			// If the boolean is true at the end, the plugin will be disabled
			boolean disablePlugin = false;
			
			// If anything has been added, this is true so the config will be saved.
			boolean needUpdate = false;
			
			pl.getLogger().info(" ");
			pl.getLogger().info(prefix+"Loading...");
			
			/* Load all the configs.
			 * We need 3 versions of the config.yml.
			 * 
			 * First, we have the actual config to write to if necessary.
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
			if(allowModify)
				pl.getConfig().options().parseComments(true);
			
			FileConfiguration cfg = pl.getConfig();
			
			YamlConfiguration cfgNoDefaults = YamlConfiguration.loadConfiguration(file);
			
			// Load the default configurations
			final InputStream defConfigStream = pl.getResource("config.yml");
			if (defConfigStream == null)
				return false;
			YamlConfiguration cfgDefault = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));


			for(Map.Entry<String, Object> e : cfgDefault.getConfigurationSection("").getValues(true).entrySet()) {
				String key = e.getKey();
				Object value = e.getValue();

				String currentType;
				boolean addToConfig = false;
				boolean missingFromConfig = false;

				if(value instanceof String) {
					String v = (String) value;
					currentType = "String";

					if(!cfgNoDefaults.contains(key) || !cfgNoDefaults.isString(key)) {
						cfg.set(key, v);
						addToConfig = true;
					}
				}else

				if(value instanceof Integer) {
					int v = (Integer) value;
					currentType = "Integer";

					if(!cfgNoDefaults.contains(key) || !cfgNoDefaults.isInt(key)) {
						cfg.set(key, v);
						addToConfig = true;
					}
				}else

				if(value instanceof Boolean) {
					boolean v = (Boolean) value;
					currentType = "Boolean";

					List<String> booleanVariants = new ArrayList<>();
					booleanVariants.add("true");
					booleanVariants.add("false");
					booleanVariants.add("'true'");
					booleanVariants.add("'false'");
					booleanVariants.add("\"true\"");
					booleanVariants.add("\"false\"");
					if(cfgNoDefaults.isString(key) && booleanVariants.contains(cfgNoDefaults.getString(key))) {
						pl.getLogger().warning("The option '"+key+"' needs to be a boolean! Please remove any quotes from the value and write down the plain word (true/false).");
					}

					if(!cfgNoDefaults.contains(key) || !cfgNoDefaults.isBoolean(key)) {
						cfg.set(key, v);
						addToConfig = true;
					}
				}else {
					currentType = "other";
				}
				if(PowerBoard.debug)
					pl.getLogger().info(key+" is type "+currentType+"; Value: "+value);

				if(addToConfig) {
					if(allowModify) {
						pl.getLogger().warning(prefix+"The missing option \""+key+"\" has been added to your config.yml");
						needUpdate = true;
					}else {
						pl.getLogger().warning(prefix+"The option \""+key+"\" is missing from your config.yml. Please add it. The default-value is: "+value);
					}
				}
			}
			
			if(needUpdate)
				pl.saveConfig();
			
			pl.getLogger().info(prefix+"Finished!");
			pl.getLogger().info(" ");
			
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
