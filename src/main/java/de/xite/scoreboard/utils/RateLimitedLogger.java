package de.xite.scoreboard.utils;

import de.xite.scoreboard.main.PowerBoard;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.logging.Logger;

public class RateLimitedLogger {
	private PowerBoard pb;
	private Logger logger;

	private int scheduler;
	private ArrayList<String> severe = new ArrayList<>();
	private ArrayList<String> warns = new ArrayList<>();
	private ArrayList<String> infos = new ArrayList<>();

	public RateLimitedLogger(PowerBoard pb) {
		this.pb = pb;
		this.logger = pb.getLogger();
		startScheduler();
	}


	public void addSevere(String msg, boolean printNow) {
		if(!severe.contains(msg)) {
			severe.add(msg);
			if(printNow)
				logger.severe(msg);
		}
	}

	public void removeSevere(String msg) {
		severe.remove(msg);
	}


	public void addWarn(String msg, boolean printNow) {
		if(!warns.contains(msg)) {
			warns.add(msg);
			if(printNow)
				logger.warning(msg);
		}
	}

	public void removeWarn(String msg) {
		warns.remove(msg);
	}


	public void addInfo(String msg, boolean printNow) {
		if(!infos.contains(msg)) {
			infos.add(msg);
			if(printNow)
				logger.info(msg);
		}
	}

	public void removeInfo(String msg) {
		infos.remove(msg);
	}

	public void stopScheduler() {
		Bukkit.getScheduler().cancelTask(scheduler);
	}

	private void startScheduler() {
		int interval = 120;

		scheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(pb, this::printLogs, 20 * interval, 20 * interval).getTaskId();
	}

	private void printLogs() {
		for(String msg : severe)
			logger.severe(msg);

		for(String msg : warns)
			logger.warning(msg);

		for(String msg : infos)
			logger.info(msg);
	}
}
