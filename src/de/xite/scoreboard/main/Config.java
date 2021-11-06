package de.xite.scoreboard.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import de.xite.scoreboard.utils.UpgradeVersion;

public class Config {
	static Main pl = Main.pl;
	public static void loadConfig() {
		File folder = new File(Main.pluginfolder);
		if(folder == null || !folder.isDirectory())
			folder.mkdirs();
		
		// (create) and load config.yml
		pl.getConfig().options().copyDefaults(true);
		pl.saveDefaultConfig();
		pl.reloadConfig();
		
		// Register hex color syntax
		String s = pl.getConfig().getString("placeholder.hexColorSyntax");
		if(s.contains("000000")) {
			String[] s2 = s.split("000000");
			pl.getLogger().info("s: "+s);
			if(s2.length == 1) {
				Main.hexColorBegin = s2[0];
				pl.getLogger().info("begin: "+Main.hexColorBegin);
			}
			if(s2.length == 2) {
				Main.hexColorBegin = s2[1];
				pl.getLogger().info("begin: "+Main.hexColorEnd);
			}
		}
		
		// Create scoreboard folder if not exists
		File sbfolder = new File(Main.pluginfolder+"/scoreboards/");
		if(!sbfolder.exists() || !sbfolder.isDirectory())
			sbfolder.mkdir();
		
		// migrate to multiple scoreboards
		File file = new File(Main.pluginfolder+"/scoreboard.yml");
		if(file.exists())
			UpgradeVersion.updateMultipleScoreboards();
		
		// create default scoreboard.yml
		createDefaultScoreboard();
	}
	public static void createDefaultScoreboard() {
		// readme
		File readme = new File(Main.pluginfolder+"/scoreboards/readme.txt");
		if(!readme.exists()) {
			try {
				readme.createNewFile();
				FileWriter myWriter = new FileWriter(readme);
				myWriter.write("How to use multiple scoreboards:\n"
						+ "1. Set the default scoreboard in the config.yml. The name of the scoreboard is the filename.\n\n"
						+ "2. Now copy the default scoreboard file and rename it to whatever you want. The filename will become the scoreboard name.\n\n"
						+ "3. Open the copied file and go to the end. There you can configurate the conditions to apply the scoreboard. Examples are below.\n\n"
						+ "4. The scoreboard you've set as default doesn't really needs conditions because it will automatically be set when a players joins the server.\n\n\n"
						+ "Examples:\n"
						+ "Apply if a player is in creative mode OR has a permission:\n"
						+ "- 'gamemode:creative'\n"
						+ "- 'permission:some.permission'\n\n"
						+ "Apply if a player is in creative mode OR surival mode AND has a permission:\n"
						+ "- 'gamemode:creative AND permission:some.permission'\n"
						+ "- 'gamemode.survival AND permission:some.permission'\n\n"
						+ "Apply the above, but only in a specific world:\n"
						+ "- 'gamemode:creative AND permission:some.permission AND world:world_nether'\n"
						+ "- 'gamemode.survival AND permission:some.permission AND world:world_nether'\n\n"
						+ "Now you should understand how it works. If you want OR, add a new line. If you want AND, write 'AND' following with the condition.\n\n"
						+ "Here are all conditions you can use:\n"
						+ "- world:<world>\n"
						+ "- permission:<permission>\n"
						+ "- gamemode:<survival/creative/adventure/spectator>");
				myWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// default scoreboard
		File file = new File(Main.pluginfolder+"/scoreboards/scoreboard.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				YamlConfiguration cfg;
				try {
					cfg = YamlConfiguration.loadConfiguration(file);
				}catch (Exception e) {
					pl.getLogger().severe("You have errors in the tablist.yml file! Please check for spacing and typo errors!");
					return;
				}
				cfg.options().header("Here you can edit the screboard.\n"
						+ "You can add as many animation steps as you like.\n"
						+ "If you want a empty line, just set one animation step that is empty.\n\n"
						+ "For every score (line) you can set a different speed.\n"
						+ "You can set up to 14 scores. For that just add a new number, like '7':\n\n"
						+ "If you have static scores (no animations or updates needed): Set the 'speed' value to '9999' or higher. Then the scheduler won't start to save performance.\n"
						+ "Note: Specify the speed in ticks, not seconds. 20 ticks = one second\n\n"
						+ "If you want to use multiple scoreboards, you have to set conditions for all scoreboards, except for the default one.\n");
				//Titel
				ArrayList<String> title = new ArrayList<String>();
				title.add("&4PowerBoard");
				title.add("&4PowerBoard");
				title.add("&cPowerBoard");
				title.add("&cPowerBoard");
				title.add("&6PowerBoard");
				title.add("&6PowerBoard");
				title.add("&ePowerBoard");
				title.add("&ePowerBoard");
				title.add("&2PowerBoard");
				title.add("&2PowerBoard");
				title.add("&aPowerBoard");
				title.add("&aPowerBoard");
				title.add("&bPowerBoard");
				title.add("&bPowerBoard");
				title.add("&3PowerBoard");
				title.add("&3PowerBoard");
				title.add("&1PowerBoard");
				title.add("&1PowerBoard");
				title.add("&9PowerBoard");
				title.add("&9PowerBoard");
				title.add("&dPowerBoard");
				title.add("&dPowerBoard");
				title.add("&5PowerBoard");
				title.add("&5PowerBoard");
				title.add("&fPowerBoard");
				title.add("&fPowerBoard");
				title.add("&7PowerBoard");
				title.add("&7PowerBoard");
				title.add("&8PowerBoard");
				title.add("&8PowerBoard");
				title.add("&0PowerBoard");
				title.add("&0PowerBoard");
				title.add("&aPowerBoard");
				title.add("&aPowerBoard");
				title.add("&f&oPowerBoard");
				title.add("&f&kPowerBoard");
				title.add("&f&mPowerBoard");
				title.add("&f&mPowerBoard");
				title.add("&f&o&nPowerBoard");
				title.add(" ");
				cfg.addDefault("titel.speed", 6);
				cfg.addDefault("titel.titles", title);
				
				//Scores
				ArrayList<String> score_1 = new ArrayList<String>();
				ArrayList<String> score_2 = new ArrayList<String>();
				ArrayList<String> score_3 = new ArrayList<String>();
				ArrayList<String> score_4 = new ArrayList<String>();
				ArrayList<String> score_5 = new ArrayList<String>();
				ArrayList<String> score_6 = new ArrayList<String>();
				ArrayList<String> score_7 = new ArrayList<String>();
				score_1.add("-Not animated-");
				cfg.addDefault("0.speed", 9999);
				cfg.addDefault("0.scores", score_1);
				
				score_2.add(" ");
				cfg.addDefault("1.speed", 9999);
				cfg.addDefault("1.scores", score_2);
				
				score_3.add("&a-A-");
				score_3.add("&b-An-");
				score_3.add("&c-Ani-");
				score_3.add("&d-Anim-");
				score_3.add("&e-Anima-");
				score_3.add("&f-Animat-");
				score_3.add("&6-Animate-");
				score_3.add("&3-Animated-");
				score_3.add("&3-Animated- \\");
				score_3.add("&3-Animated- |");
				score_3.add("&3-Animated- /");
				score_3.add("&3-Animated- -");
				score_3.add("&3-Animated- \\");
				score_3.add("&3-Animated- |");
				score_3.add("&3-Animated-");
				score_3.add("&6-Animate-");
				score_3.add("&f-Animat-");
				score_3.add("&e-Anima-");
				score_3.add("&d-Anim-");
				score_3.add("&c-Ani-");
				score_3.add("&b-An-");
				score_3.add("&a-A-");
				cfg.addDefault("2.speed", 13);
				cfg.addDefault("2.scores", score_3);
				
				score_4.add(" ");
				cfg.addDefault("3.speed", 99999);
				cfg.addDefault("3.scores", score_4);
			
				score_5.add("&dInformations:");
				cfg.addDefault("4.speed", 99999);
				cfg.addDefault("4.scores", score_5);
			
				score_6.add("&bPlayers:");
				score_6.add("&bYour rank:");
				score_6.add("&bYour name:");
				score_6.add("&bTime:");
				score_6.add("&bDate:");
				score_6.add("&bWorld:");
				score_6.add("&bYour ping:");
				score_6.add("&bYour hunger level:");
				score_6.add("&bYour saturation:");
				score_6.add("&bYour hearts:");
				score_6.add("&bServer TPS:");
				score_6.add("&bServer RAM:");
				cfg.addDefault("5.speed", 30);
				cfg.addDefault("5.scores", score_6);
			
				score_7.add("&a%server_online_players%&7/&a%server_max_players%");
				score_7.add("&a%player_rank%");
				score_7.add("&a%player_name%");
				score_7.add("&a%time%");
				score_7.add("&a%date%");
				score_7.add("&a%player_world%");
				score_7.add("&a%player_ping%");
				score_7.add("&a%player_food%");
				score_7.add("&a%player_saturation%");
				score_7.add("&a%player_health%");
				score_7.add("&a%tps%");
				score_7.add("&a%mem_used%/%mem_total%");
				cfg.addDefault("6.speed", 30);
				cfg.addDefault("6.scores", score_7);
				
				List<String> conditions = new ArrayList<>();
				conditions.add("&Add conditions here");
				cfg.addDefault("conditions", conditions);
				
				//Save
				cfg.options().copyDefaults(true);
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//Create tablist.yml file
	public static void createDefaultTablist(File file) {
		if(!file.exists()) {
			try {
				file.createNewFile();
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				cfg.options().header("Here you can customize the tablist.\n"
						+ "The speed value indicates how long (in ticks - 20 ticks = one second) it should take before the new animation step is executed.\n"
						+ "It is recommed to set the speed value for static texts like '&dInformations' or empty lines to a very high value to save performance.\n"
						+ "To add a new line, just add a new number with the speed and line values (or just copy it and edit the number).");
				
				//Header
				ArrayList<String> header1 = new ArrayList<>();
				ArrayList<String> header2 = new ArrayList<>();
				//Line1
				header1.add("&bLocation: &aX: %player_loc_x%; Y: %player_loc_y%; Z: %player_loc_z%");
				cfg.set("header.1.speed", 5);
				cfg.set("header.1.lines", header1);
				//Line2
				header2.add(" ");
				cfg.set("header.2.speed", 99999);
				cfg.set("header.2.lines", header2);
				
				//Footer
				ArrayList<String> footer1 = new ArrayList<>();
				ArrayList<String> footer2 = new ArrayList<>();
				ArrayList<String> footer3 = new ArrayList<>();
				ArrayList<String> footer4 = new ArrayList<>();
				//Line1
				footer1.add(" ");
				cfg.set("footer.1.speed", 99999);
				cfg.set("footer.1.lines", footer1);
				//Line2
				footer2.add("&dInformations:");
				cfg.set("footer.2.speed", 99999);
				cfg.set("footer.2.lines", footer2);
				//Line3
				footer3.add("&bPlayers:");
				footer3.add("&bYour rank:");
				footer3.add("&bYour name:");
				footer3.add("&bTime:");
				footer3.add("&bDate:");
				footer3.add("&bWorld:");
				footer3.add("&bYour ping:");
				footer3.add("&bYour hunger level:");
				footer3.add("&bYour saturation:");
				footer3.add("&bYour hearts:");
				footer3.add("&bServer TPS:");
				cfg.set("footer.3.speed", 30);
				cfg.set("footer.3.lines", footer3);
				//Line4
				footer4.add("&a%server_online_players%&7/&a%server_max_players%");
				footer4.add("&a%player_rank%");
				footer4.add("&a%player_name%");
				footer4.add("&a%time%");
				footer4.add("&a%date%");
				footer4.add("&a%player_world%");
				footer4.add("&a%player_ping%");
				footer4.add("&a%player_food%");
				footer4.add("&a%player_saturation%");
				footer4.add("&a%player_health%");
				footer4.add("&a%tps%");
				cfg.set("footer.4.speed", 30);
				cfg.set("footer.4.lines", footer4);
				//Save
				cfg.options().copyDefaults(true);
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
				pl.getLogger().severe("Could not create the tablist.yml file. Has the plugin/server write permissions?");
			}
		}
	}
}
