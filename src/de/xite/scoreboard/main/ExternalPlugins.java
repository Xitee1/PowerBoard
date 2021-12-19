package de.xite.scoreboard.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.xite.scoreboard.depend.LuckPermsListener;
import de.xite.scoreboard.depend.PlaceholderAPIExpansion;
import de.xite.scoreboard.depend.VaultAPI;
import de.xite.scoreboard.utils.BStatsMetrics;
import net.luckperms.api.LuckPerms;

public class ExternalPlugins {
	static PowerBoard pl = PowerBoard.pl;
	static Boolean debug = PowerBoard.debug;
	
	// APIs
	public static LuckPerms luckPerms = null;
	// Supported Plugins
	public static boolean hasVault = false;
	public static boolean hasPapi = false;
	public static boolean hasLuckPerms = false;
	
	public static void initializePlugins() {
		// ---- Check for compatible plugins ---- //
		if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			if(debug)
				pl.getLogger().info("Loading Vault...");
			if(VaultAPI.setupEconomy()) {
				hasVault = true;
				if(debug)
					pl.getLogger().info("Successfully loaded Vault-Economy!");
			}
			//setupChat();
		}
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			hasPapi = true;
			new PlaceholderAPIExpansion().register();
		}
			
		
		if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
			hasLuckPerms = true;
			RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
			if(provider != null)
				luckPerms = provider.getProvider();
			new LuckPermsListener(pl, luckPerms);
		}
		// BStats analytics
		try {
			int pluginId = 6722;
			BStatsMetrics metrics = new BStatsMetrics(pl, pluginId);
			// Custom charts
			metrics.addCustomChart(new BStatsMetrics.SimplePie("update_auto_update", () -> pl.getConfig().getBoolean("update.autoupdater") ? "Aktiviert" : "Deaktiviert"));
			metrics.addCustomChart(new BStatsMetrics.SimplePie("update_notifications", () -> pl.getConfig().getBoolean("update.notification") ? "Aktiviert" : "Deaktiviert"));
	        
			metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_scoreboard", () -> pl.getConfig().getBoolean("scoreboard") ? "Aktiviert" : "Deaktiviert"));
			metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_tablist_text", () -> pl.getConfig().getBoolean("tablist.text") ? "Aktiviert" : "Deaktiviert"));
			metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_tablist_ranks", () -> pl.getConfig().getBoolean("tablist.ranks") ? "Aktiviert" : "Deaktiviert"));
			metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_chat", () -> pl.getConfig().getBoolean("chat.ranks") ? "Atktiviert" : "Deaktiviert"));
			metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_permsystem", () -> pl.getConfig().getString("ranks.permissionsystem").toLowerCase()));
			if(PowerBoard.debug)
				pl.getLogger().info("Analytics sent to BStats");
		} catch (Exception e) {
			pl.getLogger().warning("Could not send analytics to BStats!");
		}
	}

}
