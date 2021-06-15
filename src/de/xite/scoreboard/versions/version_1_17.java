package de.xite.scoreboard.versions;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;



public class version_1_17 {
	public static Integer getPing(Player p) {
		return ((CraftPlayer)p).getPing();
	}
	public static void sendTab(Player p, String msg1, String msg2) {
		p.setPlayerListHeaderFooter(msg1, msg2);
	}
}
