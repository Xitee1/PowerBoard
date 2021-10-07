package de.xite.scoreboard.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.utils.Teams;

public class ChatListener implements Listener{
	static Main pl = Main.pl;
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(pl.getConfig().getBoolean("chat.ranks")) {
			Teams teams = Teams.get(p);
			if(teams != null) {
				if(!teams.getChatPrefix().equals("noRank")) {
					String message = teams.getChat(e.getMessage());
					message = message.replace("%", "%%"); //Fix the Chat % bug
					e.setFormat(message);
				}else {
					pl.getLogger().warning("The player "+p.getName()+" has no Rank! Make sure that he has the correct permissions.");
				}
			}else {
				pl.getLogger().warning("The player "+p.getName()+" has no team! Please rejoin and try it again. If the problem persist, please check your Plugin configuration!");
			}
		}
	}
}
