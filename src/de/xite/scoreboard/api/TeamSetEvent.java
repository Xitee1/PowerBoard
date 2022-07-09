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
	private int weight;
	
	public TeamSetEvent(Player p) {
		this.p = p;
		this.prefix = "";
		this.suffix = "";
		this.nameColor = ChatColor.WHITE;
		this.chatPrefix = "";
		this.rankDisplayName = "";
		this.weight = 0;
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
		Teams t = Teams.get(p);
		if(t != null && this.prefix.length() == 0)
			this.prefix = t.getPrefix();
		return this.prefix;
	}
	public String getSuffix() {
		Teams t = Teams.get(p);
		if(t != null && this.suffix.length() == 0)
			this.suffix = t.getSuffix();
		return this.suffix;
	}
	public ChatColor getNameColorChar() {
		Teams t = Teams.get(p);
		if(t != null && this.nameColor == ChatColor.WHITE)
			this.nameColor = t.getNameColor();
		return this.nameColor;
	}
	public String getChatPrefix() {
		Teams t = Teams.get(p);
		if(t != null && this.chatPrefix.length() == 0)
			this.chatPrefix = t.getChatPrefix();
		return this.chatPrefix;
	}
	public String getRankDisplayName() {
		Teams t = Teams.get(p);
		if(t != null && this.rankDisplayName.length() == 0)
			this.rankDisplayName = t.getRankDisplayName();
		return this.rankDisplayName;
	}
	public Integer getWeight() {
		Teams t = Teams.get(p);
		if(t != null && this.weight == 0)
			this.weight = t.getWeight();
		return this.weight;
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
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
