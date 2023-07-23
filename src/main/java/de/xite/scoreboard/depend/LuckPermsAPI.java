package de.xite.scoreboard.depend;

import de.xite.scoreboard.main.PowerBoard;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsAPI {
	public static LuckPerms luckPerms = null;

	public static boolean register() {
		if(isActive())
			return true;

		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if(provider == null)
			return false;
		luckPerms = provider.getProvider();
		new LuckPermsListener(PowerBoard.pl, luckPerms);
		return true;
	}

	public static boolean isActive() {
		return luckPerms != null;
	}

	public static LuckPerms getAPI() {
		return luckPerms;
	}
}
