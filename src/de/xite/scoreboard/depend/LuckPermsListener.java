package de.xite.scoreboard.depend;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.ranks.RankManager;
import de.xite.scoreboard.utils.Teams;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;

public class LuckPermsListener {
    public LuckPermsListener(PowerBoard pl, LuckPerms api) {
        // get the LuckPerms event bus
        EventBus eventBus = api.getEventBus();

      	// subscribe to an event using a lambda
        eventBus.subscribe(UserDataRecalculateEvent.class, e -> {
        	Bukkit.getScheduler().runTaskLater(pl, new Runnable() {// Run a half second later that it doesn't update if a player disconnects
				@Override
				public void run() {
					Player p = Bukkit.getPlayer(e.getUser().getUniqueId());
					if(p != null) {
			  			Teams teams = Teams.get(p);
						if(teams != null) {
				        	if(pl.getConfig().getBoolean("tablist.ranks")) {
				        		if(!RankManager.updateDelay.contains(p)) {
					        		RankManager.register(p);
					        		RankManager.updateTablistRanks(p);
				        			pl.getLogger().info("(LuckPermsAPI) Updated player "+p.getName());
				        		}
				        	}
				        }
					}
				}
			}, 10);
        });
    }
}
