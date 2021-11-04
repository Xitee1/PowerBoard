package de.xite.scoreboard.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import de.xite.scoreboard.commands.ScoreboardCommand;
import de.xite.scoreboard.listeners.ChatListener;
import de.xite.scoreboard.listeners.JoinQuitListener;
import de.xite.scoreboard.listeners.ScoreboardConditionListener;
import de.xite.scoreboard.modules.board.ScoreboardManager;
import de.xite.scoreboard.modules.board.ScoreboardPlayer;
import de.xite.scoreboard.modules.tablist.TabConfig;
import de.xite.scoreboard.utils.Placeholders;
import de.xite.scoreboard.utils.SelfCheck;
import de.xite.scoreboard.utils.Updater;
import de.xite.scoreboard.utils.UpgradeVersion;
import de.xite.scoreboard.utils.Version;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener{
	public static Main pl;
	
	public static String pluginfolder = "plugins/PowerBoard"; // plugin folder
	public static String pr = ChatColor.GRAY+"["+ChatColor.YELLOW+"PowerBoard"+ChatColor.GRAY+"] "; // prefix
	public static String hexColorBegin = "", hexColorEnd = ""; // hex color Syntax
	
	public static Version version; // Minecraft version
	public static boolean debug = false;
	
	@Override
	public void onEnable() {
		// ---- Load the plugin ----//
		pl = this;
		version = getBukkitVersion();
		
		UpgradeVersion.rename();
		Config.loadConfig(); // load the config.yml
		if(SelfCheck.check()) { // start the self check
	    	pl.getLogger().severe("self-check -> Fatal errors were found! Please fix you config! Disabling Plugin...");
	    	Bukkit.getPluginManager().disablePlugin(pl);
	    	return;
		}
		
		if(pl.getConfig().getBoolean("debug")) // Check if the debug is enabled in the config.yml
			debug = true;
		
		ExternalPlugins.initializePlugins(); // Load all external plugin APIs
	    
		if(Updater.checkVersion()) { // Check for updates
			pl.getLogger().info("-> A new version (v."+Updater.version+") is available! Your version: "+pl.getDescription().getVersion());
			pl.getLogger().info("-> Update me! :)");
		}
		// ---- Register commands and events ---- //
		getCommand("sb").setExecutor(new ScoreboardCommand());
		getCommand("scoreboard").setExecutor(new ScoreboardCommand());
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new JoinQuitListener(), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new ScoreboardConditionListener(), this);
		pm.registerEvents(this, this);
		
		// ---- Load Modules ---- //
		if(pl.getConfig().getBoolean("scoreboard"))
			ScoreboardManager.registerAllScoreboards(); // Register all Scoreboards
		
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				// set scoreboard and ranks
				if(pl.getConfig().getBoolean("scoreboard") || pl.getConfig().getBoolean("tablist.ranks"))
					ScoreboardPlayer.players.clear();
					for(Player all : Bukkit.getOnlinePlayers())
						ScoreboardPlayer.setScoreboard(all);
				// set tablist
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
			for(Entry<Player, String> all : ScoreboardPlayer.players.entrySet())
				ScoreboardPlayer.removeScoreboard(all.getKey(), true);
		ScoreboardManager.unregisterAllScoreboards();
		Placeholders.ph.clear();
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
    	try {
            final Pattern hexPattern = Pattern.compile(hexColorBegin + "([A-Fa-f0-9]{6})" + hexColorEnd);
            Matcher matcher = hexPattern.matcher(message);
            StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                        + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                        + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                        + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                        );
            }
            return matcher.appendTail(buffer).toString();
    	}catch (Exception e) {
    		pl.getLogger().severe("You have an invalid HEX-Color-Code! Please check the syntax! String: "+message);
    		return "InvalidHexColor";
		}
    }
}
