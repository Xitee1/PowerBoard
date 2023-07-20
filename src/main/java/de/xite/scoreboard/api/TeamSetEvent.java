package de.xite.scoreboard.api;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.xite.scoreboard.utils.Teams;

public class TeamSetEvent extends Event implements Cancellable{
	
	private final Player p;
	private boolean isCancelled;
	private String prefix;
	private String suffix;
	private ChatColor nameColor;
	private String chatPrefix;
	private String rankDisplayName;
	private String playerListName;
	private int weight = 0;
	
	public TeamSetEvent(Player p) {
		this.p = p;
	}
	
	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}
	
	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public Player getPlayer() {
		return this.p;
	}
	public String getPrefix() {
		if(prefix == null) {
			Teams t = Teams.get(p);
			if(t != null) {
				prefix = t.getPrefix();
			}else
				prefix = "Not defined";
		}
			
		return prefix;
	}
	public String getSuffix() {
		if(suffix == null) {
			Teams t = Teams.get(p);
			if(t != null) {
				suffix = t.getSuffix();
			}else
				suffix = "Not defined";
		}
		
		return suffix;
	}
	public ChatColor getNameColor() {
		if(nameColor == null) {
			Teams t = Teams.get(p);
			if(t != null) {
				nameColor = t.getNameColor();
			}else
				nameColor = ChatColor.WHITE;
		}
		
		return nameColor;
	}
	public String getChatPrefix() {
		if(chatPrefix == null) {
			Teams t = Teams.get(p);
			if(t != null) {
				chatPrefix = t.getChatPrefix();
			}else
				chatPrefix = "Not defined";
		}
		
		return chatPrefix;
	}
	public String getRankDisplayName() {
		if(rankDisplayName == null) {
			Teams t = Teams.get(p);
			if(t != null) {
				rankDisplayName = t.getRankDisplayName();
			}else
				rankDisplayName = "Not defined";
		}
		
		return rankDisplayName;
	}
	public String getPlayerListName() {
		if(playerListName == null) {
			Teams t = Teams.get(p);
			if(t != null)
				playerListName = t.getPlayerListName();
			// Not setting to "Not defined" because it can be null and is optional
		}
		
		return playerListName;
	}
	public Integer getWeight() {
		if(weight == 0) {
			Teams t = Teams.get(p);
			if(t != null) {
				weight = t.getWeight();
			}else
				weight = 0;
		}
		
		return weight;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public void setNameColor(ChatColor nameColor) {
		this.nameColor = nameColor;
	}
	public void setChatPrefix(String chatPrefix) {
		this.chatPrefix = chatPrefix;
	}
	public void setRankDisplayName(String rankDisplayName) {
		this.rankDisplayName = rankDisplayName;
	}
	public void setPlayerListName(String playerListName) {
		this.playerListName = playerListName;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
