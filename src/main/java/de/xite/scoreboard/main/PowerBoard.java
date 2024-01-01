package de.xite.scoreboard.main;

import de.xite.scoreboard.versions.VersionSpecific;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.xite.scoreboard.commands.PowerBoardCommand;
import de.xite.scoreboard.listeners.ChatListener;
import de.xite.scoreboard.listeners.ConditionListener;
import de.xite.scoreboard.listeners.JoinQuitListener;
import de.xite.scoreboard.modules.board.ScoreboardManager;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.modules.ranks.RankManager;
import de.xite.scoreboard.modules.tablist.TablistManager;
import de.xite.scoreboard.modules.tablist.TablistPlayer;
import de.xite.scoreboard.utils.TPSCalc;
import de.xite.scoreboard.utils.Teams;
import de.xite.scoreboard.utils.Updater;
import net.md_5.bungee.api.ChatColor;

import java.util.logging.Logger;

public class PowerBoard extends JavaPlugin {
	public static PowerBoard pl; // TODO make private
	private static PowerBoard instance;
	private static Logger logger;
	private static final String permissionPrefix = "powerboard.";
	private static final int spigotMCPluginID = 73854;

	private static Updater updater;
	private static TPSCalc tpsCalc;

	public final static String pr = ChatColor.GRAY+"["+ChatColor.YELLOW+"PowerBoard"+ChatColor.GRAY+"] "; // prefix
	public final static String scoreTeamPrefix = "pb-sc";

	public static boolean debug = false;
	
	@Override
	public void onEnable() {
		logger = this.getLogger();
		logger.info("--------------------------------------------------");
		logger.info("--------------- Loading PowerBoard ---------------");

		// Initialize variables
		pl = this;
		instance = this;
		updater = new Updater(spigotMCPluginID);
		tpsCalc = new TPSCalc();

		VersionSpecific.init();
		
		// Load the config - disable plugin if failed
		if(!Config.loadConfig()) {
			logger.severe("There were severe errors when loading the configuration! You should see more information above. Disabling plugin...");
			logger.severe(" ");
			logger.severe("---- Errors occurred while loading PowerBoard ----");
			logger.severe("--------------------------------------------------");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		// Load all external plugin APIs
		ExternalPlugins.initializePlugins();
	    
		// Check for updates
		Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
			if(updater.isUpdateAvailable()) {
				pl.getLogger().info("-> A new version (v."+updater.getLatestVersion()+") is available! Your version: "+updater.getCurrentVersion());
				pl.getLogger().info("-> Update me! :)");
			}
		});
		

		
		// ---- Register commands and events ---- //
		getCommand("pb").setExecutor(new PowerBoardCommand());
		getCommand("pb").setTabCompleter(new PowerBoardCommand());

		getCommand("powerboard").setExecutor(new PowerBoardCommand());
		getCommand("powerboard").setTabCompleter(new PowerBoardCommand());

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new JoinQuitListener(), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new ConditionListener(), this);
		
		// ---- Load modules ---- //
		// scoreboard
		if(pl.getConfig().getBoolean("scoreboard"))
			ScoreboardManager.registerAllScoreboards();
		
		// tablist
		if(pl.getConfig().getBoolean("tablist.text"))
			TablistManager.registerAllTablists();

		if(pl.getConfig().getBoolean("tablist.ranks"))
			RankManager.startTablistRanksUpdateScheduler();
		
		Bukkit.getScheduler().runTaskLater(pl, () -> {
			pl.getLogger().info("Registering players...");
			for(Player all : Bukkit.getOnlinePlayers()) {
				// Register Teams if chat ranks or tablist ranks are used
				if(pl.getConfig().getBoolean("chat.ranks") || pl.getConfig().getBoolean("tablist.ranks")) {
					RankManager.register(all);
					if(pl.getConfig().getBoolean("tablist.ranks"))
						RankManager.setTablistRanks(all);
				}

				if(pl.getConfig().getBoolean("scoreboard"))
					ScoreboardPlayer.setScoreboard(all, false, null);

				if(pl.getConfig().getBoolean("tablist.text"))
					TablistPlayer.addPlayer(all, null);
			}
			pl.getLogger().info("All players have been registered.");
		}, 30);
		pl.getLogger().info(" ");
		pl.getLogger().info("--------------- PowerBoard  loaded ---------------");
		pl.getLogger().info("--------------------------------------------------");
	}

	@Override
	public void onDisable() {
		// Unregister scoreboards and teams
		for(Player all : Bukkit.getOnlinePlayers()) {
			ScoreboardPlayer.removeScoreboard(all, true);
			Teams.removePlayer(all);
		}
		ScoreboardManager.unregisterAllScoreboards();
		
		// Unregister tablist
		if(pl.getConfig().getBoolean("tablist.text"))
			TablistManager.unregisterAllTablists();

		// Download the newest version if update is available & auto updater is enabled
		if(pl.getConfig().getBoolean("update.autoupdater"))
			if(updater.isUpdateAvailable())
				updater.downloadFile(true);
	}

	public static PowerBoard getInstance() {
		return instance;
	}

	public static Updater getUpdater() {
		return updater;
	}

	public static TPSCalc getTPSCalc() {
		return tpsCalc;
	}

	public static String getPermissionPrefix() {
		return permissionPrefix;
	}
}
