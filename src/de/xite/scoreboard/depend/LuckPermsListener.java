package de.xite.scoreboard.depend;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.modules.ranks.PrefixManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;

public class LuckPermsListener {
    public LuckPermsListener(Main pl, LuckPerms api) {
    	if(!pl.getConfig().getBoolean("tablist.ranks"))
    		return;
        // get the LuckPerms event bus
        EventBus eventBus = api.getEventBus();

      	// subscribe to an event using a lambda
        eventBus.subscribe(UserDataRecalculateEvent.class, e -> {
        	Bukkit.getScheduler().runTaskLater(pl, new Runnable() {// Run a half second later that it doesn't update if a player disconnects
				@Override
				public void run() {
		        	Player p = Bukkit.getPlayer(e.getUser().getUniqueId());
		        	if(p != null)
		            	PrefixManager.update(p);
				}
			}, 10);
        });
    }
}