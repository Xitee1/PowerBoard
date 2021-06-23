package de.xite.scoreboard.files;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.xite.scoreboard.main.Main;
import de.xite.scoreboard.manager.Tabpackage;
import de.xite.scoreboard.utils.Placeholders;
import de.xite.scoreboard.utils.UpgradeVersion;

public class TabConfig {
	static Main pl = Main.pl;
	public static HashMap<Integer, ArrayList<String>> headers = new HashMap<>();//line, values (animation stages)
	public static HashMap<Integer, ArrayList<String>> footers = new HashMap<>();
	
	public static HashMap<Player, HashMap<Integer, String>> currentHeader = new HashMap<>();//Player, HashMap[line, value]
	public static HashMap<Player, HashMap<Integer, String>> currentFooter = new HashMap<>();//Player, HashMap[line, value]
	
	public static boolean disabled = false;
	public void register() {
		// ---- Initialize ---- //
		File folder = new File(Main.pluginfolder);
		if(folder == null || !folder.isDirectory()) {
			folder.mkdirs();
		}
		File file = new File(folder, "tablist.yml");
		
		//Migrate from tablist_footer.yml and tablist_header.yml 
		UpgradeVersion.upgradeDoubleTabConfig(file);
		
		//Create the tablist.yml file if not exists
		Config.createDefaultTablist(file);
		
		// Check for errors
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
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
				Main.pl.getLogger().severe("Wrong tablist-header entry!");
				Main.pl.getLogger().severe("Error in line: "+line);
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
				Main.pl.getLogger().severe("Wrong tablist-footer entry!");
				Main.pl.getLogger().severe("Error in line: "+line);
			}
		}
		// Start the animation
		startAnimation();
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
	public void startAnimation() {//Start the animation
		File file = new File(Main.pluginfolder+"/tablist.yml");
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		
		int intervall = 20;
		for(int line : headers.keySet()) {//For all lines (header)
			int speed = cfg.getInt("header."+line+".speed");
			if(speed < 1)
				speed = 1;
			Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
				int step = 0;
				@Override
				public void run() {
					String text = headers.get(line).get(step);
					for(Player all : Bukkit.getOnlinePlayers()) {
						setHeader(all, line, text);
					}
					if(step >= headers.get(line).size()-1) {
						step = 0;
					}else
						step++;
				}
			}, 0, speed);
			if(speed < intervall)
				intervall = speed;
		}
		for(int line : footers.keySet()) {//For all lines (footer)
			int speed = cfg.getInt("footer."+line+".speed");
			if(speed < 1)
				speed = 1;
			Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
				int step = 0;
				@Override
				public void run() {
					String text = footers.get(line).get(step);
					for(Player all : Bukkit.getOnlinePlayers()) {
						setFooter(all, line, text);
					}
					if(step >= footers.get(line).size()-1) {
						step = 0;
					}else
						step++;
				}
			}, 0, speed);
			if(speed < intervall)
				intervall = speed;
		}
		if(disabled)
			intervall = 20*10;// to not spam the console if there are errors
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.pl, new Runnable() {
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(TabConfig.currentHeader.containsKey(p) && TabConfig.currentFooter.containsKey(p)) // Prevent error messages
						Tabpackage.send(p);// Send Tablist
				}
			}
		}, 20, intervall);
	}
}