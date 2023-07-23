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
	ChatColor nameColor;
	String teamName;
	String chatPrefix;
	String rankDisplayName;
	String playerListName;
	int weight;
	
	public Teams(Player p, String prefix, String suffix, ChatColor nameColor, String chatPrefix, String rankDisplayName, String playerListName, int weight) {
		this.p = p;
		this.setPrefix(prefix);
		this.setSuffix(suffix);
		this.nameColor = nameColor;
		this.chatPrefix = chatPrefix;
		this.rankDisplayName = rankDisplayName;
		this.playerListName = playerListName;
		this.weight = weight;
		
		if(p == null) {
			PowerBoard.pl.getLogger().severe("Could not register team "+rankDisplayName+". Player is null!");
			return;
		}
		
		if(weight < 0 || weight > 9999) {
			PowerBoard.pl.getLogger().warning("---------------------------------------------------------------------------------------------------------------------------");
			PowerBoard.pl.getLogger().warning("Warning! You cannot use negative or above 9999 weights! Player \""+p.getName()+"\". This will cause issues with the tablist sorting.");
			PowerBoard.pl.getLogger().warning("---------------------------------------------------------------------------------------------------------------------------");
		}
		
		TeamCount++;
		this.teamName = String.format("%04d", weight) +"t-" + TeamCount;
	}

	public static Teams addPlayer(Player p, String prefix, String suffix, ChatColor nameColor, String chatPrefix, String placeholderName, String playerListName, int weight) {
		Teams teams = new Teams(p, prefix, suffix, nameColor, chatPrefix, placeholderName, playerListName, weight);
		TeamsList.put(p, teams);
		return teams;
	}

	public static Teams addPlayer(Player p, String prefix, String suffix, String nameColor, String chatPrefix, String placeholderName, String playerListName, int weight) {
		nameColor = nameColor.replace("&", "").replace("§", "");
		ChatColor nameColorChat = ChatColor.WHITE;
		try {
			nameColorChat = ChatColor.getByChar(nameColor);
		}catch (Exception e) {
			PowerBoard.pl.getLogger().warning("Could not read "+p.getName()+"'s name color."
					+ "The player's name will be white in the tablist."
					+ "To avoid this, make sure, you have a valid colorcode at the end of your prefix.");
		}
		return addPlayer(p, prefix, suffix, nameColorChat, chatPrefix, placeholderName, playerListName, weight);
	}

	public static void removePlayer(Player p) {
		if(TeamsList.containsKey(p)) {
			Teams t = Teams.get(p);
			if(p.getScoreboard().getTeam(t.getTeamName()) != null)
				p.getScoreboard().getTeam(t.getTeamName()).unregister();
			
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
	public String getRankDisplayName() {
		return rankDisplayName;
	}
	public String getPlayerListName() { // Custom playername
		return playerListName;
	}
	public String getRawPrefix() {
		if(this.prefix == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading the prefix of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return null;
		}
		return this.prefix;
	}
	public String getRawSuffix() {
		if(this.suffix == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading the suffix of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return null;
		}
		return this.suffix;
	}
	public String getPrefix() {
		return Placeholders.replace(this.p, getRawPrefix());
	}
	public String getSuffix() {
		return Placeholders.replace(this.p, getRawSuffix());
	}
	public ChatColor getNameColor() {
		if(this.nameColor == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading the name color of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return ChatColor.WHITE;
		}
		return this.nameColor;
	}
	public String getChat(String message) {
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
		if(this.teamName == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading the team-name of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return null;
		}
		return this.teamName;
	}
	public int getWeight() {
		if(this.teamName == null) {
			PowerBoard.pl.getLogger().severe("An error occured while reading the team-name of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return 0;
		}
		return this.weight;
	}
	
	
	
	public void setRankDisplayName(String name) {
		this.rankDisplayName = name;
	}
	public void setPrefix(String prefix) {
		if(prefix.contains("%player_prefix%") || prefix.contains("%player_suffix%")) {
			PowerBoard.pl.getLogger().severe("The placeholders %player_prefix% and %player_suffix% are not allowed in a prefix!");
			prefix = "invalid";
		}
		this.prefix = prefix;
	}
	public void setSuffix(String suffix) {
		if(suffix.contains("%player_prefix%") || suffix.contains("%player_suffix%")) {
			PowerBoard.pl.getLogger().severe("The placeholders %player_prefix% and %player_suffix% are not allowed in a suffix!");
			suffix = "invalid";
		}
		this.suffix = suffix;
	}
	public void setNameColor(ChatColor color) {
		this.nameColor = color;
	}
	public void setPlayerListName(String playerListName) {
		this.playerListName = playerListName;
	}
}
