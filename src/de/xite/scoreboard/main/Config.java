package de.xite.scoreboard.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.xite.scoreboard.modules.board.ScoreboardManager;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.modules.ranks.RankManager;
import de.xite.scoreboard.modules.tablist.TablistManager;
import de.xite.scoreboard.modules.tablist.TablistPlayer;
import de.xite.scoreboard.utils.Placeholders;
import de.xite.scoreboard.utils.SelfCheck;
import de.xite.scoreboard.utils.Teams;
import de.xite.scoreboard.utils.UpgradeVersion;
import net.md_5.bungee.api.ChatColor;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

public class Config {
	// TODO: 03/06/2023 Remove this
	static PowerBoard pl = PowerBoard.pl;

	public static ArrayList<String> scoreboardBlacklistConditions = new ArrayList<>();
	
	public static boolean loadConfig() {
		pl.getLogger().info(" ");
		pl.getLogger().info("Loading configs..");
		
		File folder = new File(PowerBoard.pluginfolder);
		if(!folder.isDirectory())
			folder.mkdirs();
		
		// (create) and load config.yml
		pl.getConfig().options().copyDefaults(true);
		pl.saveDefaultConfig();
		pl.reloadConfig();
		
		// Check if the debug is enabled in the config.yml
		PowerBoard.debug = pl.getConfig().getBoolean("debug");
		
		// Run the SelfCheck for config.yml
		if(!SelfCheck.checkConfig()) {
			pl.getLogger().severe("Severe errors have been found in your config.yml! Please check your configuration!");
			return false;
		}
		
		// Register hex color syntax
		String s = pl.getConfig().getString("placeholder.hexColorSyntax");
		if(s.length() != 0) {
			if(s.contains("000000")) {
				s = s.replace("{", "").replace("}", "").replace("(", "").replace(")", "");
				String[] s2 = s.split("000000");
				if(s2.length > 0)
					Placeholders.hexColorBegin = s2[0];
				if(s2.length > 1)
					Placeholders.hexColorEnd = s2[1];
			}else {
				pl.getLogger().severe("You have an invalid HEX-Color syntax in your config!");
			}
		}
		
		// Create scoreboard folder if not exists
		File sbfolder = new File(PowerBoard.pluginfolder+"/scoreboards/");
		if(!sbfolder.exists() || !sbfolder.isDirectory())
			sbfolder.mkdir();
		
		// Create tablist folder if not exists
		//File tabfolder = new File(PowerBoard.pluginfolder+"/tablists/");
		//if(!tabfolder.exists() || !tabfolder.isDirectory())
			//tabfolder.mkdir();
		
		// Migrate to multiple scoreboards - migration will be removed on v3.7
		UpgradeVersion.updateMultipleScoreboards();
		
		// create default scoreboard.yml
		createDefaultScoreboard();
		createScoreboardBlacklist();
		
		// migrate from tablist_footer.yml and tablist_header.yml - migration will be removed on v3.6
		UpgradeVersion.upgradeDoubleTabConfig();
		
		// create default tablist.yml
		Config.createDefaultTablist();
		
		pl.getLogger().info("Configs loaded!");
		pl.getLogger().info(" ");
		return true;
	}
	
	
	//----------------------//
	// Create default files //
	//----------------------//
	private static void createDefaultScoreboard() {
		// default scoreboard
		File file = new File(PowerBoard.pluginfolder+"/scoreboards/scoreboard.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				String header = "Here you can customize the scoreboard.\n"
						+ "You can add as many animation steps as you like.\n\n"
						+ "For every score (line) you can set a different speed.\n"
						+ "You can set up to 14 scores. For that, just add a new number like \"'7':\"\n\n"
						+ "If you have static scores (no animations or updates needed): Set the 'speed' value to '-1' (or lower) or '9999' (or higher). Then the scheduler won't start to save performance.\n"
						+ "Note: Specify the speed in ticks, not seconds. 20 ticks = one second\n\n"
						+ "To use multiple scoreboards, read this wiki: https://github.com/Xitee1/PowerBoard/wiki/Create-and-use-multiple-scoreboards\n";
						
				//if(new Version("1.17").compareTo(PowerBoard.version) == 1) { // For MC Versions 1.18+ we use "setHeader", because "header" is deprecated.
					cfg.options().header(header);
				//}else {
					//cfg.options().setHeader(header);
				//}
				
				
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
				cfg.addDefault("title.speed", 6);
				cfg.addDefault("title.titles", title);
				
				//Scores
				ArrayList<String> score_1 = new ArrayList<>();
				ArrayList<String> score_2 = new ArrayList<>();
				ArrayList<String> score_3 = new ArrayList<>();
				ArrayList<String> score_4 = new ArrayList<>();
				ArrayList<String> score_5 = new ArrayList<>();
				ArrayList<String> score_6 = new ArrayList<>();
				ArrayList<String> score_7 = new ArrayList<>();
				score_1.add("-Not animated-");
				cfg.addDefault("0.speed", -1);
				cfg.addDefault("0.scores", score_1);
				
				score_2.add(" ");
				cfg.addDefault("1.speed", -1);
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
				cfg.addDefault("3.speed", -1);
				cfg.addDefault("3.scores", score_4);
			
				score_5.add("&dInformations:");
				cfg.addDefault("4.speed", -1);
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
				score_7.add("&a%server_tps%");
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
	
	private static void createScoreboardBlacklist() {
		File file = new File(PowerBoard.pluginfolder+"/scoreboards/scoreboard-blacklist.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				cfg.options().header("If the conditions match, no scoreboard will be displayed.\n"
						+ "Here is explained what conditions there are and how to use them: https://github.com/Xitee1/PowerBoard/wiki/Conditions\n");
				
				ArrayList<String> list = new ArrayList<>();
				list.add("world:disabled_sb_world");
				cfg.set("conditions", list);
				
				//Save
				cfg.options().copyDefaults(true);
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
				pl.getLogger().severe("Could not create the scoreboard-blacklist.yml file. Has the plugin/server write permissions?");
				return;
			}
		}
		YamlConfiguration cfg = Config.loadConfiguration(file);
		scoreboardBlacklistConditions.clear(); // In case this is only a config reload the old conditions would remain in the list
		scoreboardBlacklistConditions.addAll(cfg.getStringList("conditions"));
	}
	
	// Create tablist.yml file
	private static void createDefaultTablist() {
		File file = new File(PowerBoard.pluginfolder+"/tablist.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				cfg.options().header("Here you can customize the tablist.\n"
						+ "You can add as many animation steps as you like.\n"
						+ "For every line you can set a different speed.\n\n"
						+ "To add a new line, just add a new number like \"'3':\"\n"
						+ "If you have static lines (no animations or updates needed): Set the 'speed' value to '-1' (or lower) or '9999' (or higher). Then the scheduler won't start to save performance.\n"
						+ "Note: Specify the speed in ticks, not seconds. 20 ticks = one second\n");
				
				//Header
				ArrayList<String> header1 = new ArrayList<>();
				ArrayList<String> header2 = new ArrayList<>();
				//Line1
				header1.add("&bLocation: &aX: %player_loc_x%; Y: %player_loc_y%; Z: %player_loc_z%");
				cfg.set("header.1.speed", 5);
				cfg.set("header.1.lines", header1);
				//Line2
				header2.add(" ");
				cfg.set("header.2.speed", -1);
				cfg.set("header.2.lines", header2);
				
				//Footer
				ArrayList<String> footer1 = new ArrayList<>();
				ArrayList<String> footer2 = new ArrayList<>();
				ArrayList<String> footer3 = new ArrayList<>();
				ArrayList<String> footer4 = new ArrayList<>();
				//Line1
				footer1.add(" ");
				cfg.set("footer.1.speed", -1);
				cfg.set("footer.1.lines", footer1);
				//Line2
				footer2.add("&dInformations:");
				cfg.set("footer.2.speed", -1);
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
				footer4.add("&a%server_tps%");
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
	
	public static YamlConfiguration loadConfiguration(File file) {
	    Validate.notNull(file, "File cannot be null");
	 
	    YamlConfiguration config = new YamlConfiguration();
	 
	    try {
	        config.load(file);
	    } catch (FileNotFoundException ex) {
	    	pl.getLogger().severe("Failed to load configuration '"+file.getAbsolutePath()+"'! File does not exists.");
	        return null;
	    } catch (IOException ex) {
	        return null;
	    } catch (InvalidConfigurationException ex) {
	    	pl.getLogger().severe("Cannot read configuration '"+file.getAbsolutePath()+"'!");
	    	pl.getLogger().severe("This is probably caused by a typing error in your scoreboard config. Check for spaces in the wrong location or other typos. Look closely and use some editor like Notepad++.");
	        return null;
	    }
	 
	    return config;
	}
	
	private static boolean reloadDelay = false;
	public static void reloadConfigs(CommandSender s) {
		Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
			@Override
			public void run() {
				if(reloadDelay) {
					s.sendMessage(PowerBoard.pr+ChatColor.RED+"Please wait 2 seconds before you reload again.");
					return;
				}
				reloadDelay = true;
				Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
					@Override
					public void run() {
						reloadDelay = false;
					}
				}, 40);
				// General config
				sendConfigReloadMessage(s, ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"config"+ChatColor.GRAY+"...");
				Config.loadConfig();
				
				// Load all external plugin APIs
				sendConfigReloadMessage(s, ChatColor.YELLOW+"Initializing external plugins"+ChatColor.GRAY+"...");
				Bukkit.getScheduler().runTask(pl, () -> ExternalPlugins.initializePlugins());
				
				// Scoreboards
				sendConfigReloadMessage(s, ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"scoreboards"+ChatColor.GRAY+"...");
				ArrayList<Player> players = new ArrayList<Player>();
				players.addAll(ScoreboardPlayer.players.keySet());
				for(Player p : players)
					ScoreboardPlayer.removeScoreboard(p, true);
				ScoreboardManager.unregisterAllScoreboards();
				if(PowerBoard.pl.getConfig().getBoolean("scoreboard")) {
					ScoreboardManager.registerAllScoreboards();
					Bukkit.getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
						@Override
						public void run() {
							for(Player all : Bukkit.getOnlinePlayers())
								ScoreboardPlayer.setScoreboard(all, false, null);		
						}
					}, 5);
				}
				
				// Ranks
				if(pl.getConfig().getBoolean("tablist.ranks") || PowerBoard.pl.getConfig().getBoolean("chat.ranks")) {
					sendConfigReloadMessage(s, ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"ranks"+ChatColor.GRAY+"...");
					for(Player all : Bukkit.getOnlinePlayers())
						Teams.removePlayer(all);
					for(Player all : Bukkit.getOnlinePlayers())
						RankManager.register(all);
				}

				if(PowerBoard.pl.getConfig().getBoolean("tablist.text")) {
					sendConfigReloadMessage(s, ChatColor.GRAY+"Reloading "+ChatColor.YELLOW+"tablists"+ChatColor.GRAY+"...");
					TablistManager.unregisterAllTablists();
					TablistManager.registerAllTablists();
					for(Player all : Bukkit.getOnlinePlayers())
						TablistPlayer.addPlayer(all, null);
				}
				
				sendConfigReloadMessage(s, ChatColor.GREEN+"Plugin reloaded!");
			}
		});
	}
	private static void sendConfigReloadMessage(CommandSender s, String message) {
		if(s instanceof Player)
			s.sendMessage(PowerBoard.pr+"Config Reload: "+message);
		pl.getLogger().info("Config Reload: "+message);
	}
}
