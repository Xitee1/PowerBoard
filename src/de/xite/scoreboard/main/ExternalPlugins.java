package de.xite.scoreboard.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import de.xite.scoreboard.depend.LuckPermsListener;
import de.xite.scoreboard.depend.VaultChatImpl;
import de.xite.scoreboard.depend.VaultPermissionImpl;
import de.xite.scoreboard.utils.BStatsMetrics;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

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
			if(setupEconomy()) {
				hasVault = true;
				if(debug)
					pl.getLogger().info("Successfully loaded Vault-Economy!");
			}
			setupChat();
		}
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
			hasPapi = true;
		
		
		if(Bukkit.getPluginManager().isPluginEnabled("PermissionsEx"))
			hasPex = true;
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
			// Custom charts
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
		if(rsp == null) {
			pl.getLogger().warning("Error hooking into Vault-Economy!");
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	private static void setupChat() {
	    ServicesManager servicesManager = pl.getServer().getServicesManager();

	    Permission permission = new VaultPermissionImpl();

	    servicesManager.register(Permission.class, permission, pl, ServicePriority.Highest);
		
	    servicesManager.register(Chat.class, new VaultChatImpl(permission), pl, ServicePriority.Highest);
		
		pl.getLogger().info("Registered Vault-Chat");
	}
}
