package de.xite.scoreboard.main;

import de.xite.scoreboard.depend.*;
import org.bukkit.Bukkit;

public class ExternalPlugins {
	static PowerBoard pl = PowerBoard.pl;
	static Boolean debug = PowerBoard.debug;
	
	// APIs
	private static boolean papiRegistered = false;

	// Supported Plugins
	public static boolean hasVault = false;
	public static boolean hasPapi = false;
	public static boolean hasLuckPerms = false;
	
	public static void initializePlugins() {
		// ---- Check for compatible plugins ---- //
		if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			hasVault = true;
			if(VaultAPI.isActive()) {
				pl.getLogger().info("External plugin already loaded: Vault-Economy");
			}else {
				if(VaultAPI.registerEconomy()) {
					if(debug)
						pl.getLogger().info("Loaded external plugin: Vault-Economy");
				}else {
					pl.getLogger().warning("Error hooking into Vault-Economy! <- Ignore this if you don't have a money system or don't need PowerBoards's %player_money% placeholder. Otherwise check, if your money system supports Vault.");
				}
			}
		}

		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			hasPapi = true;
			Bukkit.getScheduler().runTask(pl, () -> {
				if(new PlaceholderAPIExpansion().register()) {
					papiRegistered = true;
					if(debug)
						pl.getLogger().info("Loaded external plugin: PlaceholderAPI");
				}else if(!papiRegistered) {
					pl.getLogger().warning("Could not load PlaceholderAPI!");
				}
			});
		}
		
		if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
			hasLuckPerms = true;
			if(LuckPermsAPI.isActive()) {
				pl.getLogger().info("External plugin already loaded: LuckPerms");
			}else {
				if(pl.getConfig().getBoolean("ranks.luckperms-api.enable") || pl.getConfig().getString("ranks.permissionsystem").equalsIgnoreCase("luckperms")) {
					if(LuckPermsAPI.register()) {
						if(debug)
							pl.getLogger().info("Loaded external plugin: LuckPerms");
					}else {
						pl.getLogger().info("LuckPerms could not be loaded!");
					}
				}else {
					pl.getLogger().warning("You have changed the rank permissions system from LuckPerms to something different. LuckPerms cannot be completely disabled with a PB reload. Please restart your server soon.");
				}
			}
		}

		// BStats analytics
		Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
			try {
				int pluginId = 6722;
				BStatsMetrics metrics = new BStatsMetrics(pl, pluginId);
				// Custom charts
				metrics.addCustomChart(new BStatsMetrics.SimplePie("update_auto_update", () -> pl.getConfig().getBoolean("update.autoupdater") ? "Enabled" : "Disabled"));
				metrics.addCustomChart(new BStatsMetrics.SimplePie("update_notifications", () -> pl.getConfig().getBoolean("update.notification") ? "Enabled" : "Disabled"));

				metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_scoreboard", () -> pl.getConfig().getBoolean("scoreboard") ? "Enabled" : "Disabled"));
				metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_tablist_text", () -> pl.getConfig().getBoolean("tablist.text") ? "Enabled" : "Disabled"));
				metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_tablist_ranks", () -> pl.getConfig().getBoolean("tablist.ranks") ? "Enabled" : "Disabled"));
				metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_use_chat", () -> pl.getConfig().getBoolean("chat.ranks") ? "Enabled" : "Disabled"));
				metrics.addCustomChart(new BStatsMetrics.SimplePie("setting_permsystem", () -> pl.getConfig().getString("ranks.permissionsystem").toLowerCase()));

			} catch (Exception e) {
				pl.getLogger().warning("Could not send analytics to BStats!");
			}
		});
	}

}
