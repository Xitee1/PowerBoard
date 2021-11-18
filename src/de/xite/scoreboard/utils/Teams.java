package de.xite.scoreboard.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.modules.ranks.PrefixManager;

public class Teams {
	Player p;
	String prefix;
	String suffix;
	String nameColor;
	String teamName;
	String chatPrefix;
	String placeholderName;
	public Teams(Player p, String prefix, String suffix, String nameColor, String teamName, String chatPrefix, String placeholderName) {
		this.p = p;
		this.prefix = prefix;
		this.suffix = suffix;
		this.nameColor = nameColor;
		this.teamName = teamName;
		this.chatPrefix = chatPrefix;
		this.placeholderName = placeholderName;
	}
	public static Teams addPlayer(Player p, String prefix, String suffix, String nameColor, String teamName, String chatPrefix, String placeholderName) {
		Teams teams = new Teams(p, prefix, suffix, nameColor, teamName, chatPrefix, placeholderName);
		PrefixManager.TeamsList.put(p, teams);
		return teams;
	}
	public static void removePlayer(Player p) {
		if(PrefixManager.TeamsList.containsKey(p))
			PrefixManager.TeamsList.remove(p);
	}
	public static Teams get(Player p) {
		if(PrefixManager.TeamsList.containsKey(p))
			return PrefixManager.TeamsList.get(p);
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
		String prefix = Placeholders.replace(this.p, this.prefix);
		if(PowerBoard.getBukkitVersion().compareTo(new Version("1.13")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.13"))) { // Under version 1.13+ you can just use up to 16 chars.
			if(prefix.length() > 64) {
				PowerBoard.pl.getLogger().severe("The prefix is too long! The limit is 64 chars included colorcodes. Chars: "+prefix.length()+", Prefix: "+prefix);
				return "too long";
			}
		}else {
			if(prefix.length() > 16) {
				PowerBoard.pl.getLogger().severe("The prefix is too long! The limit is 16 chars included colorcodes. Chars: "+prefix.length()+", Prefix: "+prefix);
				return "too long";
			}
		}
		return prefix;
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
		String suffix = Placeholders.replace(this.p, this.suffix);
		if(PowerBoard.getBukkitVersion().compareTo(new Version("1.13")) == 1 || PowerBoard.getBukkitVersion().equals(new Version("1.13"))) { // Under version 1.13+ you can just use up to 16 chars.
			if(suffix.length() > 64) {
				PowerBoard.pl.getLogger().severe("The suffix is too long! The limit is 64 chars included colorcodes. Chars: "+suffix.length()+", Suffix: "+suffix);
				return "too long";
			}
		}else {
			if(suffix.length() > 16) {
				PowerBoard.pl.getLogger().severe("The suffix is too long! The limit is 16 chars included colorcodes. Chars: "+suffix.length()+", Suffix: "+suffix);
				return "too long";
			}
		}
		
		return suffix;
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
			if(!PowerBoard.pl.getConfig().getBoolean("ranks.luckperms-api.enable"))
				PowerBoard.pl.getLogger().severe("The Name Color in the tablist could not be set! Please check your name-color-codes for the ranks (config.yml)! Current colorcode: "+nameColorS);
			
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
				String hex = PowerBoard.translateHexColor(message);
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
