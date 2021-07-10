package de.xite.scoreboard.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.manager.PrefixManager;

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
	public void setPlaceholderName(String name) {
		this.placeholderName = name;
	}
	public String getPrefix() {
		if(this.p == null) {
			Main.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return null;
		}
		if(this.prefix == null) {
			Main.pl.getLogger().severe("An error occured while reading the prefix of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return null;
		}
		String prefix = Placeholders.replace(this.p, this.prefix);
		if(Main.getBukkitVersion().compareTo(new Version("1.13")) == 1) { // Below version 1.13 you can use just up to 16 chars
			if(prefix.length() > 16) {
				Main.pl.getLogger().severe("The prefix is too long! The limit is 16 chars included colorcodes. Chars: "+prefix.length()+", Prefix: "+prefix);
				return "too long";
			}
		}else {
			if(prefix.length() > 64) {
				Main.pl.getLogger().severe("The prefix is too long! The limit is 64 chars included colorcodes. Chars: "+prefix.length()+", Prefix: "+prefix);
				return "too long";
			}
		}
		return prefix;
	}
	public String getSuffix() {
		if(this.p == null) {
			Main.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return null;
		}
		if(this.suffix == null) {
			Main.pl.getLogger().severe("An error occured while reading the suffix of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return null;
		}
		String suffix = Placeholders.replace(this.p, this.suffix);
		if(Main.getBukkitVersion().compareTo(new Version("1.13")) == 1) { // Below version 1.13 you can use just up to 16 chars
			if(suffix.length() > 16) {
				Main.pl.getLogger().severe("The suffix is too long! The limit is 16 chars included colorcodes. Chars: "+suffix.length()+", Suffix: "+suffix);
				return "too long";
			}
		}else {
			if(suffix.length() > 64) {
				Main.pl.getLogger().severe("The suffix is too long! The limit is 64 chars included colorcodes. Chars: "+suffix.length()+", Suffix: "+suffix);
				return "too long";
			}
		}
		
		return suffix;
	}
	public ChatColor getNameColor() {
		if(this.p == null) {
			Main.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return ChatColor.WHITE;
		}
		if(this.nameColor == null) {
			Main.pl.getLogger().severe("An error occured while reading the tablist-name-color of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return ChatColor.WHITE;
		}
	
		String nameColorS = this.nameColor;
		nameColorS = nameColorS.replace("&", "").replace("§", "");
		try {
			return ChatColor.getByChar(nameColorS);
		}catch (Exception e) {
			if(!(Main.pl.getConfig().getBoolean("ranks.luckperms-api.enable") || Main.pl.getConfig().getBoolean("ranks.luckperms.enable"))) {
				Main.pl.getLogger().severe("The Name Color in the tablist could not be set! Please check your name-color-codes for the ranks (config.yml)! Current colorcode: "+nameColorS);
			}
			return ChatColor.WHITE;
		}
	}
	public String getChat(String message) {
		if(this.p == null) {
			Main.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return null;
		}
		if(this.chatPrefix == null) {
			Main.pl.getLogger().severe("An error occured while the player "+p.getName()+" sended a chat message! Maybe he has no rank?");
			return message;
		}
		if(!Main.pl.getConfig().getString("chat.colorperm").equals("none") && p.hasPermission(Main.pl.getConfig().getString("chat.colorperm"))) {
			message = ChatColor.translateAlternateColorCodes('&', message);
			
			if(Main.pl.getConfig().getBoolean("chat.allowHexColors"))
				message = Main.translateHexColor(message);
		}
		
		return Placeholders.replace(p, this.chatPrefix)+message;
	}
	public String getTeamName() {
		if(this.p == null) {
			Main.pl.getLogger().severe("An error occured while reading data for the player "+p.getName()+"!");
			return null;
		}
		if(this.teamName == null) {
			Main.pl.getLogger().severe("An error occured while reading the team-name of the player "+p.getName()+"! Maybe a wrong setting in your config.yml?");
			return null;
		}
		return this.teamName;
	}
}
