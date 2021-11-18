package de.xite.scoreboard.modules.tablist;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.xite.scoreboard.main.Config;
import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Placeholders;
import de.xite.scoreboard.utils.UpgradeVersion;

public class TabManager {
	static PowerBoard pl = PowerBoard.pl;
	public static HashMap<Integer, ArrayList<String>> headers = new HashMap<>(); // line, values (animation stages)
	public static HashMap<Integer, ArrayList<String>> footers = new HashMap<>();
	
	public static HashMap<Player, HashMap<Integer, String>> currentHeader = new HashMap<>(); // Player, HashMap[line, value]
	public static HashMap<Player, HashMap<Integer, String>> currentFooter = new HashMap<>(); // Player, HashMap[line, value]
	
	public static ArrayList<BukkitTask> scheduler = new ArrayList<>();
	
	public static boolean disabled = false;
	public static void register() {
		// ---- Initialize ---- //
		File folder = new File(PowerBoard.pluginfolder);
		if(folder == null || !folder.isDirectory()) {
			folder.mkdirs();
		}
		File file = new File(folder, "tablist.yml");
		
		//Migrate from tablist_footer.yml and tablist_header.yml 
		UpgradeVersion.upgradeDoubleTabConfig(file);
		
		//Create the tablist.yml file if not exists
		Config.createDefaultTablist(file);
		
		// Check for errors
		YamlConfiguration cfg;
		try {
			cfg = YamlConfiguration.loadConfiguration(file);
		}catch (Exception e) {
			pl.getLogger().severe("You have errors in the tablist.yml file! Please check for spacing and typo errors!");
			return;
		}
		
		if(!(cfg.contains("header") || cfg.contains("footer"))) {
			pl.getLogger().severe("The tablist config file is empty or the header/footer is not configurated!");
			disabled = true;
			return;
		}
		// ---- Read Data ---- //
		// Header
		for(String line : cfg.getConfigurationSection("header").getValues(false).keySet()) {
			try {
				int i = Integer.parseInt(line);
				if(!headers.containsKey(i))
					headers.put(i, new ArrayList<String>());
				for(String header : cfg.getStringList("header."+i+".lines")) {
					headers.get(i).add(header);
				}
			}catch (Exception e) {
				PowerBoard.pl.getLogger().severe("Wrong tablist-header entry!");
				PowerBoard.pl.getLogger().severe("Error in line: "+line);
			}
		}
		// Footer
		for(String line : cfg.getConfigurationSection("footer").getValues(false).keySet()) {
			try {
				int i = Integer.parseInt(line);
				if(!footers.containsKey(i))
					footers.put(i, new ArrayList<String>());
				for(String footer : cfg.getStringList("footer."+i+".lines")) {
					footers.get(i).add(footer);
				}
			}catch (Exception e) {
				PowerBoard.pl.getLogger().severe("Wrong tablist-footer entry!");
				PowerBoard.pl.getLogger().severe("Error in line: "+line);
			}
		}
		// Start the animation
		startAnimation();
	}
	public static void unregister() {
		for(BukkitTask task : scheduler)
			task.cancel();
		headers.clear();
		footers.clear();
		currentHeader.clear();
		currentFooter.clear();
	}
	public static void setHeader(Player p, int line, String text) {
		if(!currentHeader.containsKey(p))
			currentHeader.put(p, new HashMap<>());
		currentHeader.get(p).put(line, Placeholders.replace(p, text));
	}
	public static void setFooter(Player p, int line, String text) {
		if(!currentFooter.containsKey(p))
			currentFooter.put(p, new HashMap<>());
		currentFooter.get(p).put(line, Placeholders.replace(p, text));
	}
	private static void startAnimation() {//Start the animation
		File file = new File(PowerBoard.pluginfolder+"/tablist.yml");
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		
		int interval = 20;
		for(int line : headers.keySet()) { // For all lines (header)
			int speed = cfg.getInt("header."+line+".speed");
			if(speed < 1)
				speed = 1;
			scheduler.add(
				Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
					int step = 0;
					@Override
					public void run() {
						String text = headers.get(line).get(step);
						for(Player all : Bukkit.getOnlinePlayers())
							setHeader(all, line, text);
						if(step >= headers.get(line).size()-1) {
							step = 0;
						}else
							step++;
					}
				}, 0, speed)
			);
			if(speed < interval)
				interval = speed;
		}
		for(int line : footers.keySet()) { // For all lines (footer)
			int speed = cfg.getInt("footer."+line+".speed");
			if(speed < 1)
				speed = 1;
			scheduler.add(
				Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
					int step = 0;
					@Override
					public void run() {
						String text = footers.get(line).get(step);
						for(Player all : Bukkit.getOnlinePlayers())
							setFooter(all, line, text);
						if(step >= footers.get(line).size()-1) {
							step = 0;
						}else
							step++;
					}
				}, 0, speed)
			);
			if(speed < interval)
				interval = speed;
		}
		if(disabled)
			interval = 20*10; // to not spam the console if there are errors
		scheduler.add(
			Bukkit.getScheduler().runTaskTimerAsynchronously(PowerBoard.pl, new Runnable() {
				@Override
				public void run() {
					for(Player p : Bukkit.getOnlinePlayers())
						Tabpackage.send(p); // Send Tablist
				}
			}, 20, interval)
		);
	}
}