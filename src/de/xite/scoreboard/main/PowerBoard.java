package de.xite.scoreboard.main;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.xite.scoreboard.commands.ScoreboardCommand;
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

public class PowerBoard extends JavaPlugin {
	public static PowerBoard pl;
	
	public static String pluginfolder = "plugins/PowerBoard"; // plugin folder
	public static String pr = ChatColor.GRAY+"["+ChatColor.YELLOW+"PowerBoard"+ChatColor.GRAY+"] "; // prefix
	
	public static Version version; // Minecraft version
	public static boolean aboveMC_1_13 = false;
	public static boolean debug = false;
	
	@Override
	public void onEnable() {
		// Initialize variables
		pl = this;
		pl.getLogger().info("--------------------------------------------------");
		pl.getLogger().info("--------------- Loading PowerBoard ---------------");
		pl.getLogger().info(" ");
		
		// In 1.13+ a lot of things have changed. For example 128 Chars in the scoreboard instead of 32
		version = getBukkitVersion();
		if(version.compareTo(new Version("1.13")) >= 0)
			aboveMC_1_13 = true;
		
		// Migrate from old versions:
		UpgradeVersion.rename(); // Rename Scoreboard to PowerBoard - migration will be removed on v3.7
		
		// Load the config - disable plugin if failed
		if(!Config.loadConfig()) {
			pl.getLogger().severe("There were errors when loading the configuration! You should see more informations above. Disabling plugin...");
			sendPluginLoadFailed();
			Bukkit.getPluginManager().disablePlugin(pl);
			return;
		}
		
		// Load all external plugin APIs
		ExternalPlugins.initializePlugins(); 
		
		// Start TPS calculator
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new TPS(), 100L, 1L);
		TPS.start();
	    
		// Check for updates
		Bukkit.getScheduler().runTaskAsynchronously(pl, new Runnable() {
			@Override
			public void run() {
				if(Updater.checkVersion()) { 
					pl.getLogger().info("-> A new version (v."+Updater.getVersion()+") is available! Your version: "+pl.getDescription().getVersion());
					pl.getLogger().info("-> Update me! :)");
				}
			}
		});
		

		
		// ---- Register commands and events ---- //
		getCommand("pb").setExecutor(new ScoreboardCommand());
		getCommand("powerboard").setExecutor(new ScoreboardCommand());
		getCommand("pb").setTabCompleter(new ScoreboardCommand());
		getCommand("powerboard").setTabCompleter(new ScoreboardCommand());
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
		
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				pl.getLogger().info("Registering players...");
				for(Player all : Bukkit.getOnlinePlayers()) {
					// Register Teams if chat ranks or tablist ranks are used
					if(pl.getConfig().getBoolean("chat.ranks") || pl.getConfig().getBoolean("tablist.ranks")) {
						Teams teams = Teams.get(all);
						if(teams == null)
							RankManager.register(all);
						RankManager.startTablistRanksUpdateScheduler();
					}
					if(pl.getConfig().getBoolean("tablist.ranks"))
						RankManager.setTablistRanks(all);
					
					if(pl.getConfig().getBoolean("scoreboard"))
						ScoreboardPlayer.setScoreboard(all);
					
					if(pl.getConfig().getBoolean("tablist.text"))
						TablistPlayer.addPlayer(all, null);
				}
				pl.getLogger().info("All players have been registered.");
			}
		}, 30);
		pl.getLogger().info(" ");
		pl.getLogger().info("--------------- PowerBoard  loaded ---------------");
		pl.getLogger().info("--------------------------------------------------");
	}
	@Override
	public void onDisable() {
		// Download newest version if update is available
		if(pl.getConfig().getBoolean("update.autoupdater"))
			if(Updater.checkVersion())
				Updater.downloadFile();
		
		// Unregister scoreboards and teams
		for(Player all : Bukkit.getOnlinePlayers()) {
			ScoreboardPlayer.removeScoreboard(all, true);
			Teams.removePlayer(all);
		}
		ScoreboardManager.unregisterAllScoreboards();
		
		// Unregister tablist
		if(pl.getConfig().getBoolean("tablist.text"))
			TablistManager.unregisterAllTablists();
	}

	
    // ---- Utils ---- //
	public static Version getBukkitVersion() {
		if(version != null)
			return version;
		try {
			String s = Bukkit.getBukkitVersion();
			String version = s.substring(0, s.lastIndexOf("-R")).replace("_", ".");
			pl.getLogger().info("Detected Server Version (original): "+s);
			pl.getLogger().info("Detected Server Version (extracted): "+version);
			return new Version(version);
		}catch (Exception e) {
			e.printStackTrace();
			pl.getLogger().severe("Could not extract MC version! Defaulting to 1.13.");
			return new Version("1.13");
		}
	}
	
	public static void sendPluginLoadFailed() {
		pl.getLogger().severe(" ");
		pl.getLogger().severe("---- Errors occurred while loading PowerBoard ----");
		pl.getLogger().severe("--------------------------------------------------");
	}
}
