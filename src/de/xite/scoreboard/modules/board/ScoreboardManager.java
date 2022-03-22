package de.xite.scoreboard.modules.board;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.xite.scoreboard.main.Config;
import de.xite.scoreboard.main.PowerBoard;

public class ScoreboardManager {
	// All registered scoreboards
	public static HashMap<String, ScoreboardManager> scoreboards = new HashMap<>();
	
	
	// The name of the scoreboard
	String name;
	
	// Conditions
	List<String> conditions;
	
	// all scores with all animations
	HashMap<Integer, ArrayList<String>> scores = new HashMap<>(); // <score ID, <animations>>
	
	// the title with all animations
	ArrayList<String> title = new ArrayList<>(); // <animatons>
	
	// Store all schedulers to stop them later
	ArrayList<BukkitTask> scheduler = new ArrayList<>();
	
	// Store all players with this scoreboard
	ArrayList<Player> players = new ArrayList<>();
	
	// Current title & scores
	String currentTitle;
	HashMap<Integer, String> currentScores = new HashMap<>();
	
	private ScoreboardManager(String name) {
		this.name = name;
		
		// Get the config
		File f = new File(PowerBoard.pluginfolder+"/scoreboards/"+name+".yml");
		if(!f.exists()) {
			PowerBoard.pl.getLogger().severe("Could not load Scoreboard named "+name+", because the config file does not exists!");
			return;
		}
		YamlConfiguration cfg = Config.loadConfiguration(f);
		if(cfg == null) {
			PowerBoard.pl.getLogger().severe("Could not load scoreboard '"+name+"'! This is probably caused by a typing error in your scoreboard config. Check for spaces in the wrong location or other typos. Look closely and use some editor like Notepad++.");
			unregister(this);
			return;
		}
		
		conditions = cfg.getStringList("conditions");
		importScores(cfg); // Import all scores
		importTitle(cfg); // Import the title
	}
	public static ScoreboardManager get(String name) {
		if(!scoreboards.containsKey(name))
			scoreboards.put(name, new ScoreboardManager(name));
			
		return scoreboards.get(name);
	}
	
	// Import
	private void importScores(YamlConfiguration cfg) {
		for(String s : cfg.getConfigurationSection("").getValues(false).keySet()) {
			try {
				int id = Integer.parseInt(s);
				if(cfg.getStringList(id+".scores") != null && !cfg.getStringList(id+".scores").isEmpty()) {
					
					// Add all animations
					scores.put(id, new ArrayList<String>());
					scores.get(id).addAll(cfg.getStringList(id+".scores")); 
					
					// Migrate from old syntax
					if(cfg.getInt(id+".wait") != 0) {
						cfg.set(id+".speed", cfg.getInt(id+".wait"));
						cfg.set(id+".wait", null);
					}
					
					// Start the animation
					startScoreAnimation(id, cfg.getInt(id+".speed"));
				}
			}catch (Exception e) {}
		}
		if(scores.size() > 14) // Check if more than 14 scores
			PowerBoard.pl.getLogger().warning("You have more than 14 scors in you scoreboard! Some scores cannot be displayed! This is a limitation of Minecraft.");
		
	}
	private void importTitle(YamlConfiguration cfg) {
		// Migrate from old syntax
		if(cfg.getInt("titel.wait") != 0) {
			cfg.set("titel.speed", cfg.getInt("titel.wait"));
			cfg.set("titel.wait", null);
		}
		
		title.addAll(cfg.getStringList("titel.titles"));
		startTitleAnimation(cfg.getInt("titel.speed"));
	}
	
	
	// ---- Start the animations ---- //
	private void startTitleAnimation(int speed) {
		if(title.size() == 0) {
			PowerBoard.pl.getLogger().severe("Could not load scoreboard title for scoreboard \""+name+"\"!");
			PowerBoard.pl.getLogger().severe("Disabling plugin...");
			PowerBoard.pl.getServer().getPluginManager().disablePlugin(PowerBoard.pl);
			return;
		}
		
		currentTitle = title.get(0);
		
		// check if scheduler is needed (don't schedule if higher than '9999')
		if(speed >= 9999 || speed < 0) {
			if(PowerBoard.debug)
				PowerBoard.pl.getLogger().info("Scoreboard-Title (Name: "+name+"): no animation needed (speed higher than 9999 or negative)");
			return;
		}else
			if(PowerBoard.debug)
				PowerBoard.pl.getLogger().info("Scoreboard-Title (Name: "+name+"): animation started");
		
		// Check for config errors
		if(title.size() == 0) {
			PowerBoard.pl.getLogger().severe("You have an error in your scoreboard config! Even a simple space can create this error. Look closely. ("+name+".yml - title)");
			return;
		}
		// Start animation scheduler
		scheduler.add(
			Bukkit.getScheduler().runTaskTimerAsynchronously(PowerBoard.pl, new Runnable() {
				int count = 0;
				@Override
				public void run() {
					if(players.size() != 0) {
						String s = title.get(count); // get the current score (text)
						currentTitle = s;
						for(Player p : players)
							ScoreTitleUtils.setTitle(p, p.getScoreboard(), s, true, get(name)); // set the score
						if(count >= title.size()-1) {
							count = 0;
						}else
							count++;
					}
				}
			}, 20, speed));
	}
	private void startScoreAnimation(int id, int speed) {
		currentScores.put(id, scores.get(id).get(0));
		
		// check if scheduler is needed (don't schedule if higher than '9999')
		if(speed >= 9999 || speed < 0) {
			if(PowerBoard.debug)
				PowerBoard.pl.getLogger().info("Scoreboard-Score (ID: "+id+", Name: "+name+"): no animation needed");
			return;
		}else
			if(PowerBoard.debug)
				PowerBoard.pl.getLogger().info("Scoreboard-Score (ID: "+id+", Name: "+name+"): animation started");
		
		// Check for config errors
		if(scores.size() == 0) {
			PowerBoard.pl.getLogger().severe("You have an error in your scoreboard config! Please check it for any typing errors. Even a simple space can create this error. Look closely. ("+name+".yml - scores)");
			return;
		}
		for(Entry<Integer, ArrayList<String>> e : scores.entrySet()) {
			if(e.getValue().size() == 0) {
				PowerBoard.pl.getLogger().severe("You have an error in your scoreboard config! Please check it for any typing errors. Even a simple space can create this error. Look closely. ("+name+".yml - scores)");
				return;
			}
		}
		// Start animation scheduler
		scheduler.add(
			Bukkit.getScheduler().runTaskTimerAsynchronously(PowerBoard.pl, new Runnable() {
				int count = 0;
				@Override
				public void run() {
					if(players.size() != 0) {
						String score = scores.get(id).get(count); // get the current score (text)
						int i = scores.size()-id-1;
						currentScores.replace(id, score);
						for(Player p : players) {
							
							//Bukkit.getServer().getScheduler().runTask(PowerBoard.pl, new Runnable(){
								//@Override
								//public void run() {
									ScoreTitleUtils.setScore(p, p.getScoreboard(), score, i, true, get(name)); // set the score
								//}
							//});
						}
						if(count >= scores.get(id).size()-1) {
							count = 0;
						}else
							count ++;
					}
				}
			}, 20, speed));
	}
	public void addPlayer(Player p) {
		if(!players.contains(p))
			players.add(p);
		if(ScoreboardPlayer.players.containsKey(p))
			ScoreboardPlayer.players.remove(p);
		ScoreboardPlayer.players.put(p, name);
	}
	public void removePlayer(Player p) {
		if(players.contains(p))
			players.remove(p);
		if(ScoreboardPlayer.players.containsKey(p))
			ScoreboardPlayer.players.remove(p);
	}
	public String getCurrentTitle() {
		return currentTitle;
	}
	public ArrayList<String> getCurrentScores() {
		return new ArrayList<String>(currentScores.values());
	}
	public String getName() {
		return this.name;
	}
	
	public static void unregister(ScoreboardManager sm) {
		for(BukkitTask task : sm.scheduler)
			task.cancel();
		scoreboards.remove(sm.getName());
	}
	
	
	public static void registerAllScoreboards() {
		ArrayList<String> boards = new ArrayList<>();
		// Get all scoreboards from the scoreboard folder
		File f = new File(PowerBoard.pluginfolder+"/scoreboards/");
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File f, String name) {
				return name.endsWith(".yml");
			}
		};
		File[] files = f.listFiles(filter);
		
		for(int i = 0; i < files.length; i++) {
			String s = files[i].getName();
			boards.add(s.substring(0, s.lastIndexOf(".yml")));
		}
		new ScoreboardPlayer(); // prepare the scoreboard
		for(String board : boards)
			ScoreboardManager.get(board);
	}
	public static void unregisterAllScoreboards() {
		for(Iterator<ScoreboardManager> iterator = scoreboards.values().iterator(); iterator.hasNext();) {
			ScoreboardManager sm = iterator.next();
			for(BukkitTask task : sm.scheduler)
				task.cancel();
			iterator.remove();
		}
	}
}
