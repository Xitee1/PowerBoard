package de.xite.scoreboard.utils;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;

public class Teams {
	public static HashMap<Player, Teams> TeamsList = new HashMap<>();
	private static int TeamCount = 0;
	
	Player p;
	String prefix;
	String suffix;
	String nameColor;
	String teamName;
	String chatPrefix;
	String placeholderName;
	
	public Teams(Player p, String prefix, String suffix, String nameColor, String chatPrefix, String placeholderName, int weight) {
		this.p = p;
		this.prefix = prefix;
		this.suffix = suffix;
		this.nameColor = nameColor;
		this.chatPrefix = chatPrefix;
		this.placeholderName = placeholderName;
		
		
		if(weight < 0 || weight > 999) {
			PowerBoard.pl.getLogger().warning("---------------------------------------------------------------------------------------------------------------------------");
			PowerBoard.pl.getLogger().warning("Warning! You cannot use negative or above 999 weights! Player \""+p.getName()+"\". This will cause issues with the tablist sorting.");
			PowerBoard.pl.getLogger().warning("---------------------------------------------------------------------------------------------------------------------------");
		}
		
		TeamCount++;
		this.teamName = String.format("%03d", weight)
				+"team-"
				+TeamCount;
	}
	public static Teams addPlayer(Player p, String prefix, String suffix, String nameColor, String chatPrefix, String placeholderName, int weight) {
		Teams teams = new Teams(p, prefix, suffix, nameColor, chatPrefix, placeholderName, weight);
		TeamsList.put(p, teams);
		return teams;
	}
	public static void removePlayer(Player p) {
		if(TeamsList.containsKey(p)) {
			if(p.getScoreboard().getTeam(Teams.get(p).teamName) != null)
				p.getScoreboard().getTeam(Teams.get(p).teamName).unregister();
			
			TeamsList.remove(p);
		}
	}
	public static Teams get(Player p) {
		if(TeamsList.containsKey(p))
			return TeamsList.get(p);
		return null;
	}
	public String getChatPrefix() {
		return chatPrefix;
	}
	public String getPlaceholderName() {
		return placeholderName;
	}

	public String getPrefix() {
		if(this.p == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return null;
		}
		if(this.prefix == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading the prefix of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return null;
		}
		
		return Placeholders.replace(this.p, this.prefix);
	}
	public String getSuffix() {
		if(this.p == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return null;
		}
		if(this.suffix == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading the suffix of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return null;
		}
		
		return Placeholders.replace(this.p, this.suffix);
	}
	public ChatColor getNameColor() {
		if(this.p == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return ChatColor.WHITE;
		}
		if(this.nameColor == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading the tablist-name-color of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return ChatColor.WHITE;
		}
	
		String nameColorS = this.nameColor;
		nameColorS = nameColorS.replace("&", "").replace("§", "");
		try {
			return ChatColor.getByChar(nameColorS);
		}catch (Exception e) {
			return ChatColor.WHITE;
		}
	}
	public String getChat(String message) {
		if(this.p == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return null;
		}
		if(this.chatPrefix == null) {
			PowerBoard.pl.getLogger().severe("An error occured while the player "+p.getName()+" was sending a chat message! Maybe he has no rank?");
			return message;
		}
		if(!PowerBoard.pl.getConfig().getString("chat.colorperm").equals("none") && p.hasPermission(PowerBoard.pl.getConfig().getString("chat.colorperm"))) {
			message = ChatColor.translateAlternateColorCodes('&', message);
			
			if(PowerBoard.pl.getConfig().getBoolean("chat.allowHexColors")) {
				String hex = Placeholders.translateHexColor(message);
				if(!hex.equalsIgnoreCase("InvalidHexColor"))
					message = hex;
			}
		}
		
		return Placeholders.replace(p, this.chatPrefix)+message;
	}
	public String getTeamName() {
		if(this.p == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return null;
		}
		if(this.teamName == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading the team-name of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return null;
		}
		return this.teamName;
	}
	
	
	
	public void setPlaceholderName(String name) {
		this.placeholderName = name;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public void setNameColor(String color) {
		this.nameColor = color;
	}
}
