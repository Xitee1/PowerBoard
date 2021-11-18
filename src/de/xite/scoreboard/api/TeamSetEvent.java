package de.xite.scoreboard.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamSetEvent extends Event implements Cancellable{
	
	private final Player p;
	private boolean isCancelled;
	private String prefix;
	private String suffix;
	private String nameColorChar;
	private String chatPrefix;
	private String placeholderName;
	private int weight;
	
	public TeamSetEvent(Player p) {
		this.p = p;
		this.prefix = "";
		this.suffix = "";
		this.nameColorChar = "f";
		this.chatPrefix = "";
		this.placeholderName = "";
		this.placeholderName = "";
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
		return this.prefix;
	}
	public String getSuffix() {
		return this.suffix;
	}
	public String getNameColorChar() {
		return this.nameColorChar;
	}
	public String getChatPrefix() {
		return this.chatPrefix;
	}
	public String getPlaceholderName() {
		return this.placeholderName;
	}
	public Integer getWeight() {
		return this.weight;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public void setNameColor(String nameColorChar) {
		this.nameColorChar = nameColorChar;
	}
	public void setChatPrefix(String chatPrefix) {
		this.chatPrefix = chatPrefix;
	}
	public void setPlaceholderName(String placeholderName) {
		this.placeholderName = placeholderName;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
