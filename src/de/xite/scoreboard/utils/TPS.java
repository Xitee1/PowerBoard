package de.xite.scoreboard.utils;

import org.bukkit.Bukkit;

import de.xite.scoreboard.main.PowerBoard;

public class TPS implements Runnable {
	public static int TICK_COUNT= 0;
	public static long[] TICKS= new long[600];
	public static long LAST_TICK= 0L;
	
	public static double currentTPS = 20;

	public static double getTPS() {
		return getTPS(100);
	}

	public static double getTPS(int ticks) {
		if(TICK_COUNT < ticks)
			return 20.0D;
		int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
		long elapsed = System.currentTimeMillis() - TICKS[target];

		return ticks / (elapsed / 1000.0D);
	}
	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PowerBoard.pl, new Runnable() {
			@Override
			public void run() {
				currentTPS = getTPS();
			}
		}, 0, 20);
	}
	
	
	public void run() {
		TICKS[(TICK_COUNT% TICKS.length)] = System.currentTimeMillis();

		TICK_COUNT+= 1;
	}
	
}
