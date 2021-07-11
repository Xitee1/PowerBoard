package de.xite.scoreboard.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.xite.scoreboard.listeners.LuckPermsListener;
import de.xite.scoreboard.utils.BStatsMetrics;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;

public class ExternalPlugins {
	static Main pl = Main.pl;
	static Boolean debug = Main.debug;
	
	// APIs
	public static Economy econ = null;
	public static LuckPerms luckPerms = null;
	// Supported Plugins
	public static boolean hasVault = false;
	public static boolean hasPex = false;
	public static boolean hasPapi = false;
	public static boolean hasLuckPerms = false;
	
	public static void initializePlugins() {
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
					pl.getLogger().severe("There was an error while loading Vault! Make sure that you have a money system on your server that also supports Vault.");
			}catch (NoClassDefFoundError  e) {
				pl.getLogger().severe("There was an error while loading Vault! Make sure that you have a money system on your server that also supports Vault.");
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
			new LuckPermsListener(pl, luckPerms);
	    }
		// BStats analytics
		try {
			int pluginId = 6722; // <-- Replace with the id of your plugin!
	        BStatsMetrics metrics = new BStatsMetrics(pl, pluginId);
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
    private static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = pl.getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null)
            return false;
        econ = rsp.getProvider();
        return econ != null;
    }
}
