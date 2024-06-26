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
import de.xite.scoreboard.utils.UpgradeVersion;

public class ScoreboardManager {
	static PowerBoard pl = PowerBoard.pl;

	// All registered scoreboards
	public static HashMap<String, ScoreboardManager> scoreboards = new HashMap<>();
	public static ArrayList<String> scoreboardBlacklistConditions = new ArrayList<>();

	String name; // scoreboard name
	List<String> conditions; // scoreboards conditions
	ArrayList<String> title = new ArrayList<>(); // scoreboard title list <animations>
	HashMap<Integer, ArrayList<String>> scores = new HashMap<>(); // scoreboard scores list <score ID, <animations>>
	ArrayList<Player> players = new ArrayList<>(); // players currently having this scoreboard
	ArrayList<BukkitTask> scheduler = new ArrayList<>(); // used schedulers by this sb manager

	// Current title & scores
	String currentTitle;
	HashMap<Integer, String> currentScores = new HashMap<>(); // <score ID,  current text>

	/**
	 * Register a new scoreboard manager by name.
	 * The name must match the config file name (without file extension).
	 *
	 * @param name
	 */
	private ScoreboardManager(String name) {
		this.name = name;

		if(name.equals("blacklisted")) {
			pl.getLogger().severe("Sorry, but the scoreboard name 'blacklisted' is reserved for the system. Please use another name.");
			return;
		}

		// Get the config
		File f = new File(Config.getConfigFolder() + "/scoreboards/"+name+".yml");
		if(!f.exists()) {
			pl.getLogger().severe("Could not load scoreboard named "+name+" because the config file does not exist!");
			return;
		}
		YamlConfiguration cfg = Config.loadConfiguration(f);
		if(cfg == null) {
			unregister(this);
			return;
		}

		// --- Migrate from "titel" to "title" ---
		UpgradeVersion.updateTitelTitle(cfg, f);
		// ---------------------------------------

		conditions = cfg.getStringList("conditions");
		importScores(cfg); // Import all scores
		importTitle(cfg); // Import the title
	}

	/**
	 * Gets a scoreboard manager by its name.
	 * The scoreboard will be registered automatically if not done yet.
	 *
	 * @param name scoreboard name
	 * @return
	 */
	public static ScoreboardManager get(String name) {
		if(!scoreboards.containsKey(name))
			scoreboards.put(name, new ScoreboardManager(name));

		return scoreboards.get(name);
	}

	/**
	 * Unregisters a scoreboard manager.
	 * All animation schedulers will be stopped automatically.
	 *
	 * @param sm
	 */
	public static void unregister(ScoreboardManager sm) {
		for(BukkitTask task : sm.scheduler)
			task.cancel();
		scoreboards.remove(sm.getName());
	}

	/**
	 * Imports the scores from the config file to this sb manager.
	 *
	 * @param cfg
	 */
	private void importScores(YamlConfiguration cfg) {
		int i = 0;

		for(String s : cfg.getConfigurationSection("").getValues(false).keySet()) {
			try {
				int id = Integer.parseInt(s);

				List<String> list = cfg.getStringList(id+".scores");
				if(!list.isEmpty()) {
					int speed = cfg.getInt(id+".speed");

					// Check if the numbers are in the correct order and begin with 0
					if(id != i) {
						pl.getLogger().warning("Your scores of scoreboard '"+name+"' do not begin with 0 or have an incorrect order. Please check that the numbers begin with 0 (not 1) and are sequentially. This could cause problems with your scoreboard!");
						id = i;
					}
					i++;

					// Add all animations
					scores.put(id, new ArrayList<>());
					scores.get(id).addAll(list);

					// Start the animation
					startScoreAnimation(id, speed);
				}

			}catch (IllegalStateException | NumberFormatException ignored) {
			}
		}
		if(scores.size() > 14) // Check if more than 14 scores
			pl.getLogger().warning("You have more than 14 scores in you scoreboard! Some scores cannot be displayed! This is a limitation of Minecraft.");
	}

	/**
	 * Imports the title from the config file to this sb manager.
	 *
	 * @param cfg
	 */
	private void importTitle(YamlConfiguration cfg) {
		title.addAll(cfg.getStringList("title.titles"));
		startTitleAnimation(cfg.getInt("title.speed"));
	}

	/**
	 * Starts the title animation scheduler.
	 *
	 * @param speed animation speed
	 */
	private void startTitleAnimation(int speed) {
		if(title.isEmpty()) {
			pl.getLogger().severe("Could not load the title for scoreboard \""+name+"\"!");
			pl.getLogger().severe("Disabling plugin...");
			pl.getServer().getPluginManager().disablePlugin(pl);
			return;
		}

		currentTitle = title.get(0);

		// check if scheduler is needed (don't schedule if higher than '9999' or negative)
		if(speed > 9999 || speed < 0) {
			if(PowerBoard.debug)
				pl.getLogger().info("Scoreboard-Title (Name: "+name+"): no animation needed (speed higher than 9999 or negative)");
			return;
		}else
		if(PowerBoard.debug)
			pl.getLogger().info("Scoreboard-Title (Name: "+name+"): animation started");

		// Check for config errors
		if(title.isEmpty()) {
			pl.getLogger().severe("You have an error in your scoreboard config! ("+name+".yml - title)");
			return;
		}

		// Start animation scheduler
		scheduler.add(
				Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
					int count = 0;
					@Override
					public void run() {
						if(!players.isEmpty()) {
							String s = title.get(count); // get the current score (text)
							currentTitle = s;
							for(Player p : players)
								ScoreTitleUtils.setTitle(p, s, true); // set the score
							if(count >= title.size()-1) {
								count = 0;
							}else
								count++;
						}
					}
				}, 20, speed));
	}

	/**
	 * Starts the score animation scheduler.
	 *
	 * @param id the score id
	 * @param speed animation speed
	 */
	private void startScoreAnimation(int id, int speed) {
		currentScores.put(id, scores.get(id).get(0));

		// check if scheduler is needed (don't schedule if higher than '9999')
		if(speed >= 9999 || speed < 0) {
			if(PowerBoard.debug)
				pl.getLogger().info("Scoreboard-Score (ID: "+id+", Name: "+name+"): no animation needed (speed higher than 9999 or negative)");
			return;
		}else
		if(PowerBoard.debug)
			pl.getLogger().info("Scoreboard-Score (ID: "+id+", Name: "+name+"): animation started");

		// Check for config errors
		if(scores.isEmpty()) {
			pl.getLogger().severe("You have an error in your scoreboard config! Please check it for any typing errors. Even a simple space can create this error. Look closely. ("+name+".yml - scores)");
			return;
		}
		for(Entry<Integer, ArrayList<String>> e : scores.entrySet()) {
			if(e.getValue().isEmpty()) {
				pl.getLogger().severe("You have an error in your scoreboard config! Please check it for any typing errors. Even a simple space can create this error. Look closely. ("+name+".yml - scores)");
				return;
			}
		}
		// Start animation scheduler
		scheduler.add(
				Bukkit.getScheduler().runTaskTimerAsynchronously(pl, new Runnable() {
					int count = 0;
					@Override
					public void run() {
						if(!players.isEmpty()) {
							String score = scores.get(id).get(count); // get the current score (text)
							int i = scores.size()-id-1;
							currentScores.replace(id, score);

							List<Player> all = new ArrayList<>(players);
							for(Player p : all)
								ScoreTitleUtils.setScore(p, score, i, true, name); // set the score

							if(count >= scores.get(id).size()-1) {
								count = 0;
							}else
								count ++;
						}
					}
				}, 20, speed));
	}

	/**
	 * Adds a player to this scoreboard.
	 *
	 * @param p the player
	 */
	public void addPlayer(Player p) {
		if(!players.contains(p))
			players.add(p);
		ScoreboardPlayer.players.put(p, name);
	}

	/**
	 * Removes a player from this scoreboard.
	 *
	 * @param p the player
	 */
	public void removePlayer(Player p) {
		// containsKey removed because it's not necessary.
		players.remove(p);
		ScoreboardPlayer.players.remove(p);
	}

	/**
	 * Returns the current title of the scoreboard.
	 *
	 * @return
	 */
	public String getCurrentTitle() {
		return currentTitle;
	}

	/**
	 * Returns the current scores of the scoreboard.
	 *
	 * @return
	 */
	public ArrayList<String> getCurrentScores() {
		return new ArrayList<>(currentScores.values());
	}

	/**
	 * Returns the name of the scoreboard.
	 *
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Loads all config files from the scoreboard config folder and registers (starts) the scoreboard managers for these.
	 */
	public static void registerAllScoreboards() {
		ArrayList<String> boards = new ArrayList<>();

		// Get all scoreboards from the scoreboard folder
		File f = new File(Config.getConfigFolder() + "/scoreboards/");
		FilenameFilter filter = (f1, name) -> name.endsWith(".yml") && !name.equals("scoreboard-blacklist.yml");
		File[] files = f.listFiles(filter);
		if(files != null) {
			for (File file : files) {
				String s = file.getName();
				boards.add(s.substring(0, s.lastIndexOf(".yml")));
			}
		}

		for(String board : boards) {
			ScoreboardManager.get(board);
			pl.getLogger().info("Registered scoreboard '"+board+"'.");
		}

		File sbBlacklist = new File(Config.getConfigFolder() + "/scoreboards/scoreboard-blacklist.yml");
		YamlConfiguration cfg = Config.loadConfiguration(sbBlacklist);
		if(cfg != null)
			scoreboardBlacklistConditions.addAll(cfg.getStringList("conditions"));
	}

	/**
	 * Unregister all currently loaded scoreboard managers.
	 */
	public static void unregisterAllScoreboards() {
		for(Iterator<ScoreboardManager> iterator = scoreboards.values().iterator(); iterator.hasNext();) {
			ScoreboardManager sm = iterator.next();
			for(BukkitTask task : sm.scheduler)
				task.cancel();
			iterator.remove();
		}
		scoreboardBlacklistConditions.clear();
	}
}
