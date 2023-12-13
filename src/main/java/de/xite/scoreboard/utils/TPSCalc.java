package de.xite.scoreboard.utils;

import org.bukkit.Bukkit;

import de.xite.scoreboard.main.PowerBoard;

public class TPSCalc {
	private static final PowerBoard instance = PowerBoard.getInstance();

	private int TICK_COUNT = 0;
	private int ticksToMeasure;
	private final long[] TICKS = new long[600];
	
	private double currentTPS = 20;

	public TPSCalc() {
		ticksToMeasure = 100;
		startScheduler();
	}

	public TPSCalc(int ticksToMeasure) {
		this.ticksToMeasure = ticksToMeasure;
		startScheduler();
	}

	private void startScheduler() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, () -> {
			TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
			TICK_COUNT += 1;
		}, 100L, 1L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, () -> currentTPS = calculateTPS(), 0, 20);
	}
	
	public double getCurrentTPS() {
		return MathUtils.round(currentTPS, 1);
	}

	private double calculateTPS() {
		if(TICK_COUNT < ticksToMeasure)
			return 20.0D;
		int target = (TICK_COUNT - 1 - ticksToMeasure) % TICKS.length;
		long elapsed = System.currentTimeMillis() - TICKS[target];

		return ticksToMeasure / (elapsed / 1000.0D);
	}
}
