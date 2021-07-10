package de.xite.scoreboard.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import de.xite.scoreboard.main.Main;

public class UpgradeVersion {
	static Main pl = Main.pl;
	public static void updateMultipleScoreboards() {
		pl.getLogger().info("Upgrading multiple scoreboard support..");
		pl.getLogger().info("Moving files..");
		
		File oldFile = new File(Main.pluginfolder+"/scoreboard.yml");
		File file = new File(Main.pluginfolder+"/scoreboards/scoreboard.yml");
		oldFile.renameTo(file);
		
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		cfg.options().header("Here you can edit the screboard.\n"
				+ "You can add as many animation steps as you like.\n"
				+ "If you want a empty line, just set one animation step that is empty.\n\n"
				+ "For every score (line) you can set a different speed.\n"
				+ "You can set up to 14 scores. For that just add a new number, like '7':\n\n"
				+ "If you have static scores (no animations or updates needed): Set the 'speed' value to '9999' or higher. Then the scheduler won't start to save performance.\n"
				+ "Note: Specify the speed in ticks, not seconds. 20 ticks = one second\n\n"
				+ "If you want to use multiple scoreboards, you have to set conditions for all scoreboards, except for the default one.\n");
		ArrayList<String> list = new ArrayList<>();
		list.add("world:world AND permission:some.permission");
		list.add("world:world AND permission:some.other.permission");
		list.add("world:world AND gamemode:creative");
		list.add("world:world_nether");
		cfg.set("conditions", list);
		try {
			cfg.save(file);
		} catch (IOException e) {e.printStackTrace();}
		
		pl.getLogger().info("----- Upgrade successful! -----");
		pl.getLogger().info("Done! The scoreboard.yml file is now located in "+file.getPath()+". To add new scoreboards, just copy the scoreboard.yml and rename it to what you want. "
				+ "The filename of the copied scoreboard will be the scoreboard name. To set the conditions when the scoreboard should be applied, open the file and scroll down to the end. "
				+ "There is a new option where you can set the conditions.");
		pl.getLogger().info("----- Upgrade successful! -----");
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
