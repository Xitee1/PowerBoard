package de.xite.scoreboard.versions;

import org.bukkit.entity.Player;

public class version_1_16 extends version_1_15 {

	// 1.16.5 added Player#ping() EntityPlayer#ping can be glitchy
	private final boolean newMethod = NMS_VERSION.contains("R3");

	@Override
	public int getPing(Player p) {
		return newMethod ? p.getPing() : super.getPing(p);
	}
}