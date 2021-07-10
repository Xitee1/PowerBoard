package de.xite.scoreboard.main;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.xite.scoreboard.api.CustomPlaceholders;
import de.xite.scoreboard.board.ScoreboardManager;
import de.xite.scoreboard.board.ScoreboardPlayer;
import de.xite.scoreboard.commands.ScoreboardCommand;
import de.xite.scoreboard.files.Config;
import de.xite.scoreboard.files.TabConfig;
import de.xite.scoreboard.listeners.Chat;
import de.xite.scoreboard.listeners.JoinQuitListener;
import de.xite.scoreboard.listeners.LuckPermsEvent;
import de.xite.scoreboard.utils.BStatsMetrics;
import de.xite.scoreboard.utils.SelfCheck;
import de.xite.scoreboard.utils.Updater;
import de.xite.scoreboard.utils.Version;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener{
	public static Main pl;
	
	// APIs
	public static Economy econ = null;
	public LuckPerms luckPerms = null;
	// Supported Plugins
	public static boolean hasVault = false;
	public static boolean hasPex = false;
	public static boolean hasPapi = false;
	public static boolean hasLuckPerms = false;
	
	// prefix & plugin folder
	public static String pluginfolder = "plugins/Scoreboard";
	public static String pr = "§7[§eScoreboard§7] ";
	
	// All registered scoreboards
	public static HashMap<String, ScoreboardManager> scoreboards = new HashMap<>();
	
	// All registered scoreboards
	public static HashMap<Player, String> players = new HashMap<>(); // Player; Scoreboard config file name
	
	// All registered custom placeholders
	public static ArrayList<CustomPlaceholders> ph = new ArrayList<>();
	
	// Minecraft Version
	public static Version version;
	
	// Debug enabled/disabled
	public static boolean debug = false;
	
	@Override
	public void onEnable() {
		// ---- Load Plugin ----//
		pl = this;
		version = getBukkitVersion();
		
		Config.loadConfig(); // load the config.yml
		if(SelfCheck.check()) { // start the self check
	    	pl.getLogger().severe("self-check -> Fatal errors were found! Please fix you config! Disabling Plugin...");
	    	Bukkit.getPluginManager().disablePlugin(pl);
	    	return;
		}
		
		if(pl.getConfig().getBoolean("debug")) // Check if the debug is enabled in the config.yml
			debug = true;
		
		initializePlugins(); // Load all external plugin APIs
	    
		// Updater
		if(Updater.checkVersion()) {
			pl.getLogger().info("-> A new version (v."+Updater.version+") is available! Your version: "+pl.getDescription().getVersion());
			pl.getLogger().info("-> Update me! :)");
		}
		// ---- Register commands and events ---- //
		getCommand("sb").setExecutor(new ScoreboardCommand());
		getCommand("scoreboard").setExecutor(new ScoreboardCommand());
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new JoinQuitListener(), this);
		pm.registerEvents(new Chat(), this);
		pm.registerEvents(this, this);
		
		// ---- Load Modules ---- //
		if(pl.getConfig().getBoolean("scoreboard"))
			registerScoreboards(); // Register all Scoreboards
		
		// Set the scoreboard and prefixes for all online players
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				if(pl.getConfig().getBoolean("scoreboard") || pl.getConfig().getBoolean("tablist.ranks"))
					players.clear();
					for(Player all : Bukkit.getOnlinePlayers())
						ScoreboardPlayer.setScoreboard(all, pl.getConfig().getString("scoreboard-default"));
				if(pl.getConfig().getBoolean("tablist.text")) {
					TabConfig tab = new TabConfig();
					tab.register();
				}
			}
		}, 30);
	}
	@Override
	public void onDisable() {
		if(pl.getConfig().getBoolean("update.autoupdater"))
			if(Updater.checkVersion())
				Updater.downloadFile();
		if(pl.getConfig().getBoolean("scoreboard"))
			for(Entry<Player, String> all : Main.players.entrySet())
				ScoreboardPlayer.removeScoreboard(all.getKey(), true);
		Main.unregisterScoreboards();
		ph.clear();
	}
	public void initializePlugins() {
		// ---- Check for compatible plugins ---- //
		if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			if(debug)
				pl.getLogger().info("Loading Vault...");
			try{
				if(setupEconomy()) {
					hasVault = true;
					if(debug)
						pl.getLogger().info("Successfully loaded Vault!");
				}else
					pl.getLogger().severe("There was an error while loading Vault! Make sure that you have a money Plugin on your server that also supports Vault.");
			}catch (NoClassDefFoundError  e) {
				pl.getLogger().severe("There was an error while loading Vault! Make sure that you have a money Plugin on your server that also supports Vault.");
			}
		}
	    if(Bukkit.getPluginManager().isPluginEnabled("PermissionsEx"))
	        hasPex = true;
	    if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
	    	hasPapi = true;
	    if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
	    	hasLuckPerms = true;
			RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
			if(provider != null) {
				luckPerms = provider.getProvider();
			}
			new LuckPermsEvent(pl, luckPerms);
	    }
		// BStats analytics
		try {
			int pluginId = 6722; // <-- Replace with the id of your plugin!
	        BStatsMetrics metrics = new BStatsMetrics(this, pluginId);
	        //Costom charts
	        metrics.addCustomChart(new BStatsMetrics.SimplePie("update_auto_update", () -> pl.getConfig().getBoolean("update.autoupdater") ? "Aktiviert" : "Deaktiviert"));
	        metrics.addCustomChart(new BStatsMetrics.SimplePie("update_notifications", () -> pl.getConfig().getBoolean("update.notification") ? "Aktiviert" : "Deaktiviert"));
	        
	        metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_scoreboard", () -> pl.getConfig().getBoolean("scoreboard") ? "Aktiviert" : "Deaktiviert"));
	        metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_tablist_text", () -> pl.getConfig().getBoolean("tablist.text") ? "Aktiviert" : "Deaktiviert"));
	        metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_tablist_ranks", () -> pl.getConfig().getBoolean("tablist.ranks") ? "Aktiviert" : "Deaktiviert"));
	        metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_chat", () -> pl.getConfig().getBoolean("chat.ranks") ? "Atktiviert" : "Deaktiviert"));
	        metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_permsystem", () -> pl.getConfig().getString("ranks.permissionsystem").toLowerCase()));
	        if(Main.debug)
	        	pl.getLogger().info("Analytics sent to BStats");
		} catch (Exception e) {
			pl.getLogger().warning("Could not send analytics to BStats!");
		}
	}
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null)
            return false;
        econ = rsp.getProvider();
        return econ != null;
    }
	public static void registerScoreboards() {
		ArrayList<String> boards = new ArrayList<>();
		// Get all scoreboards from the scoreboard folder
		File f = new File(pluginfolder+"/scoreboards/");
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File f, String name) {
				return name.endsWith(".yml");
			}
		};
		File[] files = f.listFiles(filter);
		
		for(int i = 0; i < files.length; i++) {
			String s = files[i].getName();
			boards.add(s.substring(0, s.lastIndexOf(".yml")));
		}
		new ScoreboardPlayer(); // prepare the scoreboard
		for(String board : boards)
			ScoreboardManager.get(board);
	}
	public static void unregisterScoreboards() {
		for(Entry<String, ScoreboardManager> sm : Main.scoreboards.entrySet()) {
			String name = sm.getKey();
			ScoreboardManager.unregister(name);
		}
	}
	

    // ---- Utils ---- //
	public static Version getBukkitVersion() {
		if(version != null)
			return version;
		String s = Bukkit.getBukkitVersion();
		String version = s.substring(0, s.lastIndexOf("-R")).replace("_", ".");
		pl.getLogger().info("Detected Server Version (original): "+s);
		pl.getLogger().info("Detected Server Version (extracted): "+version);
		// compareTo: 1 = a equals or is newer than b
		// compareTo: -1 = a is older than b
		return new Version(version);
	}

    public static double round(double value, int places) {
    	if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public final static char COLOR_CHAR = ChatColor.COLOR_CHAR;
    public static String translateHexColor(String message) {
    	String startTag = "#";
    	String endTag = "";
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                    );
        }
        return matcher.appendTail(buffer).toString();
    }
}
