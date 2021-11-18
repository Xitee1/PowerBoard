package de.xite.scoreboard.depend;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import de.xite.scoreboard.main.PowerBoard;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultAPI {
	static PowerBoard pl = PowerBoard.pl;
	public static Economy econ = null;
	
	public static boolean setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = pl.getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null) {
			pl.getLogger().warning("Error hooking into Vault-Economy! <- Ignore if you don't have a economy plugin.");
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	public static void setupChat() {
	    ServicesManager servicesManager = pl.getServer().getServicesManager();

	    Permission permission = new VaultPermissionImpl();

	    servicesManager.register(Permission.class, permission, pl, ServicePriority.Highest);
		
	    servicesManager.register(Chat.class, new VaultChatImpl(permission), pl, ServicePriority.Highest);
		
		pl.getLogger().info("Registered Vault-Chat");
	}
}
