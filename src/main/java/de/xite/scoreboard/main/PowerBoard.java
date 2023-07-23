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
import de.xite.scoreboard.utils.TPS;
import de.xite.scoreboard.utils.Teams;
import de.xite.scoreboard.utils.Updater;
import de.xite.scoreboard.utils.UpgradeVersion;
import de.xite.scoreboard.utils.Version;
import net.md_5.bungee.api.ChatColor;

import java.util.logging.Logger;

public class PowerBoard extends JavaPlugin {
	public static PowerBoard pl;
	
	public static String pluginfolder = "plugins/PowerBoard"; // plugin folder
	public static String pr = ChatColor.GRAY+"["+ChatColor.YELLOW+"PowerBoard"+ChatColor.GRAY+"] "; // prefix
	
	public static boolean aboveMC_1_13 = false;
	public static boolean debug = false;
	
	@Override
	public void onEnable() {
		// Initialize variables
		pl = this;
		Logger logger = getLogger();
		logger.info("--------------------------------------------------");
		logger.info("--------------- Loading PowerBoard ---------------");
		
		// In 1.13+ a lot of things have changed. For example 128 Chars in the scoreboard instead of 32
		if(Version.CURRENT.isAtLeast(Version.v1_13))
			aboveMC_1_13 = true;

		VersionSpecific.init();

		// Migrate from old versions:
		UpgradeVersion.rename(); // Rename Scoreboard to PowerBoard - migration will be removed on v3.7
		
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
		
		// Start TPS calculator
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPS(), 100L, 1L);
		TPS.start();
	    
		// Check for updates
		Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
			if(Updater.checkVersion()) {
				pl.getLogger().info("-> A new version (v."+Updater.getVersion()+") is available! Your version: "+pl.getDescription().getVersion());
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
				if(pl.getConfig().getBoolean("chat.ranks") || pl.getConfig().getBoolean("tablist.ranks"))
					RankManager.register(all);

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
			if(Updater.checkVersion())
				Updater.downloadFile(true);
	}
}
