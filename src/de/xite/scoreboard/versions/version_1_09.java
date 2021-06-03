package de.xite.scoreboard.versions;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class version_1_09 {
	public static Integer getPing(Player p) {
		return ((CraftPlayer) p).getHandle().ping;
	}
}
