package de.xite.scoreboard.modules.tablist;

import org.bukkit.entity.Player;

import de.xite.scoreboard.main.PowerBoard;
import de.xite.scoreboard.utils.Version;
import de.xite.scoreboard.versions.version_1_08;
import de.xite.scoreboard.versions.version_1_10;
import de.xite.scoreboard.versions.version_1_11;
import de.xite.scoreboard.versions.version_1_12;
import de.xite.scoreboard.versions.version_1_13;
import de.xite.scoreboard.versions.version_1_14;
import de.xite.scoreboard.versions.version_1_15;
import de.xite.scoreboard.versions.version_1_16;

public class Tabpackage {
	static PowerBoard pl = PowerBoard.pl;

	public static void send(Player p, String header, String footer) {		
		// Send tablist
		if(PowerBoard.getBukkitVersion().compareTo(new Version("1.17")) >= 0) {
			p.setPlayerListHeaderFooter(header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.16")) >= 0) {
			version_1_16.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.15")) >= 0) {
			version_1_15.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.14")) >= 0) {
			version_1_14.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.13")) >= 0) {
			version_1_13.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.12")) >= 0) {
			version_1_12.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.11")) >= 0) {
		  version_1_11.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.10")) >= 0) {
			version_1_10.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.9")) >= 0) {
			version_1_08.sendTab(p, header, footer);
		}else if(PowerBoard.getBukkitVersion().compareTo(new Version("1.8")) >= 0) {
			version_1_08.sendTab(p, header, footer);
		}else
			pl.getLogger().severe("You are using a unsupported Minecraft version!");
	}
}
