package de.xite.scoreboard.depend;

import java.util.UUID;

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
        eventBus.subscribe(UserDataRecalculateEvent.class, e -> updateRank(e.getUser().getUniqueId()));
    }
    
    private void updateRank(UUID uuid) {
    	Bukkit.getScheduler().runTaskLaterAsynchronously(PowerBoard.pl, new Runnable() { // Run half a second later, so it won't update if a player disconnects
			@Override
			public void run() {
				Player p = Bukkit.getPlayer(uuid);
				if(p != null) {
		  			Teams teams = Teams.get(p);
					if(teams != null) {
			        	if(PowerBoard.pl.getConfig().getBoolean("tablist.ranks")) {
			        		if(RankManager.updateTablistRanks(p))
			        			if(PowerBoard.debug)
			        				PowerBoard.pl.getLogger().info("(LuckPermsAPI) Updated "+p.getName()+"'s rank");
		        		}
			        }
				}
			}
		}, 10);
    }
}
