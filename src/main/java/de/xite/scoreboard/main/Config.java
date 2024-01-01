package de.xite.scoreboard.main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
	static PowerBoard pl = PowerBoard.pl;
	private static final String configFolder = "plugins/PowerBoard";

	public static boolean loadConfig() {
		pl.getLogger().info(" ");
		pl.getLogger().info("Loading configs..");
		
		File folder = new File(configFolder);
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
		String hexSyntax = pl.getConfig().getString("placeholder.hexColorSyntax");
		loadHEXColorSyntax(hexSyntax);
		
		// Create scoreboard folder if not exists
		File sbfolder = new File(configFolder +"/scoreboards/");
		if(!sbfolder.exists() || !sbfolder.isDirectory())
			sbfolder.mkdir();
		
		// Create tablist folder if not exists
		//File tabfolder = new File(PowerBoard.pluginfolder+"/tablists/");
		//if(!tabfolder.exists() || !tabfolder.isDirectory())
			//tabfolder.mkdir();
		
		// Migrate to multiple scoreboards - migration will be removed on v3.7
		UpgradeVersion.updateMultipleScoreboards();
		
		// create default scoreboard.yml & scoreboard-blacklist.yml
		copyDefaultConfig("scoreboards/scoreboard.yml");
		copyDefaultConfig("scoreboards/scoreboard-blacklist.yml");
		
		// migrate from tablist_footer.yml and tablist_header.yml - migration will be removed on v3.7
		UpgradeVersion.upgradeDoubleTabConfig();
		
		// create default tablist.yml
		copyDefaultConfig("tablist.yml");
		
		pl.getLogger().info("Configs loaded!");
		pl.getLogger().info(" ");
		return true;
	}
	

	private static void copyDefaultConfig(String configPath) {
		File file = new File(PowerBoard.pl.getDataFolder(), configPath);
		if(file.exists())
			return;

		pl.saveResource(configPath, false);
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

	public static YamlConfiguration getConfigNoDefaults() {
		return Config.loadConfiguration(new File(pl.getDataFolder(), "config.yml"));
	}

	private static void loadHEXColorSyntax(String syntax) {
		if(syntax != null && !syntax.isEmpty()) {
			if(syntax.contains("000000")) {
				syntax = syntax.replace("{", "").replace("}", "").replace("(", "").replace(")", "");
				String[] s2 = syntax.split("000000");
				if(s2.length > 0)
					Placeholders.hexColorBegin = s2[0];
				if(s2.length > 1)
					Placeholders.hexColorEnd = s2[1];
			}else {
				pl.getLogger().severe("You have an invalid HEX-Color syntax in your config!");
			}
		}
	}
	
	private static boolean reloadDelay = false;

	public static void reloadConfigs(CommandSender s) {
		Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
			// Reload delay
			if(reloadDelay) {
				s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.RED+"Please wait 2 seconds before you reload again.");
				return;
			}
			reloadDelay = true;
			Bukkit.getScheduler().runTaskLater(pl, () -> reloadDelay = false, 40);

			pl.getLogger().info(" ");
			// General config
			sendConfigReloadMessage(s, ChatColor.DARK_AQUA+"Reloading "+ChatColor.YELLOW+"config"+ChatColor.GRAY+"...");
			Config.loadConfig();

			// Load all external plugin APIs
			sendConfigReloadMessage(s, ChatColor.DARK_AQUA+"Initializing "+ChatColor.YELLOW+"external plugins"+ChatColor.GRAY+"...");
			ExternalPlugins.initializePlugins();

			// Scoreboards
			sendConfigReloadMessage(s, ChatColor.DARK_AQUA+"Reloading "+ChatColor.YELLOW+"scoreboards"+ChatColor.GRAY+"...");
			List<Player> players = new ArrayList<>(ScoreboardPlayer.players.keySet());
			for(Player p : players)
				ScoreboardPlayer.removeScoreboard(p, true);
			ScoreboardManager.unregisterAllScoreboards();
			if(PowerBoard.pl.getConfig().getBoolean("scoreboard")) {
				ScoreboardManager.registerAllScoreboards();
				Bukkit.getScheduler().runTaskLaterAsynchronously(pl, () -> {
					for(Player all : Bukkit.getOnlinePlayers())
						ScoreboardPlayer.setScoreboard(all, false, null);
				}, 5);
			}

			// Ranks
			if(pl.getConfig().getBoolean("tablist.ranks") || PowerBoard.pl.getConfig().getBoolean("chat.ranks")) {
				sendConfigReloadMessage(s, ChatColor.DARK_AQUA+"Reloading "+ChatColor.YELLOW+"ranks"+ChatColor.GRAY+"...");
				for(Player all : Bukkit.getOnlinePlayers())
					Teams.removePlayer(all);
				for(Player all : Bukkit.getOnlinePlayers()) {
					RankManager.register(all);
					if(pl.getConfig().getBoolean("tablist.ranks"))
						RankManager.setTablistRanks(all);
				}
			}
			if(pl.getConfig().getBoolean("tablist.ranks"))
				RankManager.startTablistRanksUpdateScheduler();

			if(PowerBoard.pl.getConfig().getBoolean("tablist.text")) {
				sendConfigReloadMessage(s, ChatColor.DARK_AQUA+"Reloading "+ChatColor.YELLOW+"tablists"+ChatColor.GRAY+"...");
				TablistManager.unregisterAllTablists();
				TablistManager.registerAllTablists();
				for(Player all : Bukkit.getOnlinePlayers())
					TablistPlayer.addPlayer(all, null);
			}

			sendConfigReloadMessage(s, ChatColor.GREEN+"Plugin reloaded!");
			if(s instanceof Player)
				s.sendMessage(PowerBoard.pbChatPrefix +ChatColor.DARK_GRAY+"Possible errors aren't displayed here. You should check the console.");
			pl.getLogger().info(" ");
		});
	}

	private static void sendConfigReloadMessage(CommandSender s, String message) {
		if(s instanceof Player)
			s.sendMessage(PowerBoard.pbChatPrefix +"Config: "+message);
		pl.getLogger().info("Config: "+message);
	}

	public static String getConfigFolder() {
		return configFolder;
	}
}
