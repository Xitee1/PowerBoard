package de.xite.scoreboard.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import de.xite.scoreboard.main.PowerBoard;

public class SelfCheck {
	static PowerBoard pl = PowerBoard.pl;
	
	public static void checkConfig() {
		
		//Yaml cfg = new Yaml("config.yml", PowerBoard.pluginfolder);
		//cfg.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS);
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
