package de.xite.scoreboard.utils;

import de.xite.scoreboard.main.PowerBoard;
import org.bukkit.Bukkit;

import org.jetbrains.annotations.NotNull;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {
	private final String version;

	public Version(@NotNull String version) {
		if(!version.matches("[0-9]+(\\.[0-9]+)*"))
			throw new IllegalArgumentException("Invalid version format");
		this.version = version;
	}

	public static final Version v1_8  = new Version("1.8");
	public static final Version v1_9  = new Version("1.9");
	public static final Version v1_10 = new Version("1.10");
	public static final Version v1_11 = new Version("1.11");
	public static final Version v1_12 = new Version("1.12");
	public static final Version v1_13 = new Version("1.13");
	public static final Version v1_14 = new Version("1.14");
	public static final Version v1_15 = new Version("1.15");
	public static final Version v1_16 = new Version("1.16");
	public static final Version v1_17 = new Version("1.17");
	public static final Version v1_18 = new Version("1.18");
	public static final Version v1_20_3 = new Version("1.20.3");

	public static final Version CURRENT;
	static {
		Logger logger = PowerBoard.pl.getLogger();
		Version v;
		String s = Bukkit.getBukkitVersion();
		try {
			// Extracts the leading numeric version, tolerating arbitrary suffixes:
			//   "1.21.4-R0.1-SNAPSHOT"   -> "1.21.4"
			//   "26.1.2-build.12-alpha"  -> "26.1.2"
			//   "26.1.2.build.12-alpha"  -> "26.1.2"
			Matcher m = Pattern.compile("^([0-9]+(?:\\.[0-9]+)*)").matcher(s.replace("_", "."));
			if(!m.find())
				throw new IllegalArgumentException("No leading version number in '" + s + "'");
			String version = m.group(1);

			if(PowerBoard.debug) {
				logger.info(" ");
				logger.info("Detected Server Version (original): " + s);
				logger.info("Detected Server Version (extracted): " + version);
			}

			v = new Version(version);
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe("Could not extract MC version from '" + s + "'! Defaulting to 1.13.");
			v = Version.v1_13;
		}
		CURRENT = v;
	}
	private static boolean isAbove_1_13 = Version.CURRENT.isAtLeast(Version.v1_13);
	private static boolean isAbove_1_20_3 = Version.CURRENT.isAtLeast(Version.v1_20_3);

	/**
	 * int = 1: a is newer than b;
	 * int = 0: equals;
	 * int = -1: a is older than b
	 */
	@Override
	public int compareTo(@NotNull Version that) {
		String[] thisParts = this.version.split("\\.");
		String[] thatParts = that.version.split("\\.");

		int length = Math.max(thisParts.length, thatParts.length);
		for (int i = 0; i < length; i++) {
			int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
			int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;

			int comparison = Integer.compare(thisPart, thatPart);
			if (comparison != 0)
				return comparison;
		}
		return 0;
	}

	public boolean isAtLeast(Version that) {
		return compareTo(that) >= 0;
	}

	@Override
	public boolean equals(Object that) {
		if(this == that)
			return true;
		return that instanceof Version && compareTo((Version) that) == 0;
	}

	public static boolean isAbove_1_13() {
		return isAbove_1_13;
	}

	public static boolean isAbove_1_20_3() {
		return isAbove_1_20_3;
	}
}