package de.xite.scoreboard.modules.tablist;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.xite.scoreboard.modules.board.ScoreboardManager;
import de.xite.scoreboard.versions.VersionSpecific;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Placeholders;
import de.xite.scoreboard.utils.SelfCheck;

public class TablistManager {
	private static PowerBoard pl = PowerBoard.pl;
	// All registered scoreboards
	public static HashMap<String, TablistManager> tablists = new HashMap<>();
	
	// The name of the tablist
	String name;
	
	// Conditions
	List<String> conditions;
	
	// All available lines
	HashMap<Integer, ArrayList<String>> headers = new HashMap<>(); // line, values (animation stages)
	HashMap<Integer, ArrayList<String>> footers = new HashMap<>();
	
	// The current lines
	HashMap<Integer, String> currentHeader = new HashMap<>(); // line, value
	HashMap<Integer, String> currentFooter = new HashMap<>(); // line, value
	
	// Store all schedulers to stop them later
	ArrayList<BukkitTask> scheduler = new ArrayList<>();
	
	// Store all players with this scoreboard
	ArrayList<Player> players = new ArrayList<>();
	
	
	private TablistManager(String name) {
		this.name = name;
		
		// Get the config
		File f = new File(PowerBoard.pluginfolder+"/"+name+".yml");
		if(!f.exists()) {
			PowerBoard.pl.getLogger().severe("Could not load tablist named "+name+", because the config file does not exists!");
			return;
		}
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		conditions = cfg.getStringList("conditions");
		register(cfg);
	}
	public static TablistManager get(String name) {
		if(!tablists.containsKey(name))
			tablists.put(name, new TablistManager(name));
			
		return tablists.get(name);
	}
	
	
	private void register(YamlConfiguration cfg) {
		// ---- Initialize ---- //
		
		// Check for errors
		SelfCheck.checkTablist(name, cfg);
		
		// ---- Read Data ---- //
		// Header
		for(String line : cfg.getConfigurationSection("header").getValues(false).keySet()) {
			try {
				int i = Integer.parseInt(line);
				if(!headers.containsKey(i))
					headers.put(i, new ArrayList<>());
				for(String header : cfg.getStringList("header."+i+".lines")) {
					headers.get(i).add(header);
				}
			}catch (Exception e) {
				PowerBoard.pl.getLogger().severe("Invalid header entry in tablist '"+name+"'! Line: "+line);
				return;
			}
		}
		
		// Footer
		for(String line : cfg.getConfigurationSection("footer").getValues(false).keySet()) {
			try {
				int i = Integer.parseInt(line);
				if(!footers.containsKey(i))
					footers.put(i, new ArrayList<>());
				for(String footer : cfg.getStringList("footer."+i+".lines")) {
					footers.get(i).add(footer);
				}
			}catch (Exception e) {
				PowerBoard.pl.getLogger().severe("Invalid footer entry in tablist '"+name+"'! Line: "+line);
			}
		}
		
		// ---- Start the animation ---- //
		int interval = 20*60;
		// Header
		for(int line : headers.keySet()) {
			int speed = cfg.getInt("header."+line+".speed");
			if(startHeaderAnimation(line, speed))
				interval = Math.min(interval, speed); // Set interval to speed if the speed value is smaller
		}
		// Footer
		for(int line : footers.keySet()) {
			int speed = cfg.getInt("footer."+line+".speed");
			if(startFooterAnimation(line, speed))
				interval = Math.min(interval, speed); // Set interval to speed if the speed value is smaller
		}
		
		// The scheduler which will send the tablist to all players
		scheduler.add(
			Bukkit.getScheduler().runTaskTimerAsynchronously(PowerBoard.pl, () -> {
				for(Player p : players) {
					sendPlayer(p);
				}
			}, 20, interval)
		);
		if(PowerBoard.debug)
			pl.getLogger().info("Tablist '"+name+"' loaded.");
	}


	private boolean startHeaderAnimation(int line, int speed) {
		String text = headers.get(line).get(0);
		currentHeader.put(line, text);
		
		if(speed < 0 || speed >= 9999) {
			if(PowerBoard.debug)
				pl.getLogger().info("Tablist header line "+line+" (Name: "+name+"): no animation needed (speed higher than 9999 or negative)");
			return false;
		}else
			if(PowerBoard.debug)
				pl.getLogger().info("Tablist header line "+line+" (Name: "+name+"): animation started");
		
		scheduler.add(
			Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
				int step = 0;

				@Override
				public void run() {
					String text = headers.get(line).get(step);
					currentHeader.put(line, text);
					if(step >= headers.get(line).size()-1) {
						step = 0;
					}else
						step++;
				}
			}, 0, speed)
		);
		return true;
	}
	private boolean startFooterAnimation(int line, int speed) {
		String text = footers.get(line).get(0);
		currentFooter.put(line, text);
		
		if(speed < 0 || speed >= 9999) {
			if(PowerBoard.debug)
				pl.getLogger().info("Tablist footer line "+line+" (Name: "+name+"): no animation needed (speed higher than 9999 or negative)");
			return false;
		}else
			if(PowerBoard.debug)
				pl.getLogger().info("Tablist footer line "+line+" (Name: "+name+"): animation started");
		
		scheduler.add(
			Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
				int step = 0;

				@Override
				public void run() {
					String text = footers.get(line).get(step);
					currentFooter.put(line, text);
					if(step >= footers.get(line).size()-1) {
						step = 0;
					}else
						step++;
				}
			}, 0, speed)
		);
		return true;
	}
	
	private void sendPlayer(Player p) {
		String header = "", footer = "";

		for(Entry<Integer, String> e : currentHeader.entrySet())
			header += e.getValue()+"\n";
		for(Entry<Integer, String> e : currentFooter.entrySet())
			footer += e.getValue()+"\n";

		// Remove the empty line at the end
		if(header.length() > 0)
			header = header.substring(0, header.length()-1);
		if(footer.length() > 0)
			footer = footer.substring(0, footer.length()-1);
		
		// Placeholders
		header = Placeholders.replace(p, header);
		footer = Placeholders.replace(p, footer);
		
		VersionSpecific.current.sendTab(p, header, footer);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void unregister() {
		for(BukkitTask task : scheduler)
			task.cancel();
		for(Player p : players)
			TablistPlayer.players.remove(p);
		players.clear();
		tablists.remove(name);
	}
	public static void unregisterAllTablists() {
		List<TablistManager> list = new ArrayList<>(tablists.values());
		for(TablistManager sm : list)
			sm.unregister();
	}
	public static void registerAllTablists() {
		/*
		ArrayList<String> tabs = new ArrayList<>();
		// Get all scoreboards from the scoreboard folder
		File f = new File(PowerBoard.pluginfolder+"/tablists/");
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File f, String name) {
				return name.endsWith(".yml");
			}
		};
		File[] files = f.listFiles(filter);
		
		for(int i = 0; i < files.length; i++) {
			String s = files[i].getName();
			tabs.add(s.substring(0, s.lastIndexOf(".yml")));
		}
		for(String tab : tabs) {
			TablistManager.get(tab);
			pl.getLogger().info("Registered tablist '"+tab+"'.");
		}
		*/

		TablistManager.get(pl.getConfig().getString("tablist.text-default"));
		pl.getLogger().info("Registered tablist.");
	}
	
	public void addPlayer(Player p) {
		if(!players.contains(p)) {
			players.add(p);
			sendPlayer(p);
		}
	}

	public void removePlayer(Player p, boolean sendBlankTablist) {
		if(players.contains(p)) {
			players.remove(p);
			if(sendBlankTablist)
				VersionSpecific.current.sendTab(p, null, null);
		}
	}
}