package de.xite.scoreboard.depend;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import de.xite.scoreboard.main.PowerBoard;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultAPI {
	public static Economy economy = null;
	
	public static boolean registerEconomy() {
		RegisteredServiceProvider<Economy> rsp = PowerBoard.pl.getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
			return false;
		economy = rsp.getProvider();
		return true;
	}

	public static boolean isActive() {
		return economy != null;
	}

	public static Economy getAPI() {
		return economy;
	}
}
