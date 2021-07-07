package de.xite.scoreboard.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import de.xite.scoreboard.main.Main;

public class UpgradeVersion {
	static Main pl = Main.pl;
	public static void updateMultipleScoreboards() {
		pl.getLogger().info("Upgrading multiple scoreboard support..");
		pl.getLogger().info("Move files..");
		File file = new File(Main.pluginfolder+"/scoreboard.yml");
		if(file.exists()) {
			File folder = new File(Main.pluginfolder+"/scoreboards");
			folder.mkdir();
			file.renameTo(new File(Main.pluginfolder+"/scoreboards/scoreboard.yml"));
		}
		pl.getConfig().set("scoreboard-default", "scoreboard");
		pl.getLogger().warning("--- WARNING ---");
		pl.getLogger().warning("Please add the config option");
		pl.getLogger().warning("\"scoreboard-default: 'scoreboard' # The scoreboard that will be set after a player joins the server\"");
		pl.getLogger().warning("to your config.yml below \"scoreboard: true\"");
		pl.getLogger().warning("--- WARNING ---");
	}
	public static void upgradeDoubleTabConfig(File file) {
		//Migrate from tablist_footer.yml and tablist_header.yml 
		
		File folder = new File(Main.pluginfolder);
		File old_header = new File(folder, "tablist_header.yml");
		File old_footer = new File(folder, "tablist_footer.yml");
		if(old_header.exists() && old_footer.exists() && !file.exists()) {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			pl.getLogger().warning("Old files detected - starting migration");
			try {
				file.createNewFile();
				cfg.options().header("Here you can customize the tablist.\n"
						+ "The speed value indicates how long (in ticks - 20 ticks = one second) it should take before the new animation step is executed.\n"
						+ "It is recommed to set the speed value for static texts like '&dInformations' or empty lines to a very high value to save performance.\n"
						+ "To add a new line, just add a new number with the speed and line values (or just copy it and edit the number).");
				//Header
				for(String line : YamlConfiguration.loadConfiguration(old_header).getConfigurationSection("").getValues(false).keySet()) {
					try {
						int i = Integer.parseInt(line);
						cfg.set("header."+i+".speed", YamlConfiguration.loadConfiguration(old_header).getInt(i+".wait")*20);
						cfg.set("header."+i+".lines", YamlConfiguration.loadConfiguration(old_header).getStringList(i+".lines"));
					}catch (Exception e) {
						Main.pl.getLogger().severe("Wrong tablist-header entry!");
						Main.pl.getLogger().severe("Error in line: "+line);
					}
				}
				//Footer
				for(String line : YamlConfiguration.loadConfiguration(old_footer).getConfigurationSection("").getValues(false).keySet()) {
					try {
						int i = Integer.parseInt(line);
						cfg.set("footer."+i+".speed", YamlConfiguration.loadConfiguration(old_footer).getInt(i+".wait")*20);
						cfg.set("footer."+i+".lines", YamlConfiguration.loadConfiguration(old_footer).getStringList(i+".lines"));
					}catch (Exception e) {
						Main.pl.getLogger().severe("Wrong tablist-footer entry!");
						Main.pl.getLogger().severe("Error in line: "+line);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				pl.getLogger().severe("Could not create the tablist.yml file. Has the Plugin/Server write permissions?");
			}
			try {
				cfg.save(file);
				old_header.delete();
				old_footer.delete();
				pl.getLogger().info("Migration successful!");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
